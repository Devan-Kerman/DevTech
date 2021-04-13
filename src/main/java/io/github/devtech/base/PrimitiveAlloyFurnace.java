package io.github.devtech.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.widgets.ALabel;
import io.github.astrarre.gui.v0.api.base.widgets.AProgressBar;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.recipes.v0.api.RecipeFile;
import io.github.astrarre.recipes.v0.api.ingredient.Ingredients;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.recipes.v0.api.recipe.Result;
import io.github.astrarre.recipes.v0.fabric.ingredient.FabricIngredients;
import io.github.astrarre.recipes.v0.fabric.output.FabricOutputs;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.transfer.v0.fabric.inventory.CombinedInventory;
import io.github.astrarre.util.v0.api.Val;
import io.github.devtech.Devtech;
import io.github.devtech.api.StandardDevtechMachine;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.port.base.InputItemPort;
import io.github.devtech.api.port.base.OutputItemPort;
import io.github.devtech.api.registry.DLang;
import io.github.devtech.api.registry.DPorts;
import io.github.devtech.api.registry.DSprites;
import io.github.devtech.base.gui.APortSlot;
import io.github.devtech.client.DevtechModelProvider;
import io.github.devtech.client.model.CubeData;
import io.github.devtech.client.model.CubeMachineBakedModel;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.registry.FuelRegistry;

public class PrimitiveAlloyFurnace extends StandardDevtechMachine {
	public static final Recipe.Quad<Inventory, Inventory, Val<Integer>, Inventory> RECIPE = Recipe.builder()
	                                                                                              .add(FabricIngredients.ITEM_INGREDIENT)
	                                                                                              .add(FabricIngredients.ITEM_INGREDIENT)
	                                                                                              .add(Ingredients.INTEGER)
	                                                                                              .outputsNoId()
	                                                                                              .add(FabricOutputs.ITEM_STACK)
	                                                                                              .build("Primitive Alloy Furnace");
	public static final RecipeFile RECIPE_FILE = new RecipeFile(Devtech.id("primitive_alloy_furnace")).add(RECIPE);

	public PrimitiveAlloyFurnace(Identifier id) {
		super(id);
		this.configs.add(new Configuration(Arrays.asList(new Port.Type(DPorts.ITEM_INPUT, PortColor.RED),
				new Port.Type(DPorts.ITEM_INPUT, PortColor.BLUE),
				new Port.Type(DPorts.ITEM_INPUT, PortColor.GREEN),
				new Port.Type(DPorts.ITEM_OUTPUT, PortColor.NONE)), true) {
			@Override
			public void tick(DefaultBlockEntity blockEntity, List<Port> ports) {
				Tile tile = (Tile) blockEntity;
				Inventory combined = CombinedInventory.combine(((InputItemPort) ports.get(0)), ((InputItemPort) ports.get(1)));
				Result result = RECIPE.apply(combined, combined, tile.progress, ((OutputItemPort) ports.get(3)).getInventoryDelegate());
				if (result.getFailedIndex() == 2) {
					if (tile.burnTime.get() <= 1) {
						InputItemPort input = (InputItemPort) ports.get(2);
						ItemStack stack = input.getStack(0);
						Integer fuel = FuelRegistry.INSTANCE.get(stack.getItem());
						if (fuel != null && fuel > 0) {
							ItemStack copied = stack.copy();
							copied.decrement(1);
							input.setStack(0, copied);
							tile.burnTime.set(tile.burnTime.get() + fuel);
							tile.lastBurnTime = fuel;
							blockEntity.getWorld().setBlockState(blockEntity.getPos(), blockEntity.getCachedState().with(Properties.LIT, true));
						} else {
							blockEntity.getWorld().setBlockState(blockEntity.getPos(), blockEntity.getCachedState().with(Properties.LIT, false));
							tile.burnTime.set(0);
							tile.progress.set(Math.max(0, tile.progress.get() - 1));
							return;
						}
					}
					tile.lastTotalProgress = result.getInput(Ingredients.INTEGER, 2);
					tile.progress.set(tile.progress.get() + 1);
				} else if (result.isSuccess()) {
					tile.lastTotalProgress = 1;
					tile.progress.set(0);
				} else {
					tile.progress.set(0);
				}
				tile.burnTime.set(Math.max(tile.burnTime.get() - 1, 0));
			}

			@Override
			public void openGui(DefaultBlockEntity blockEntity, PlayerEntity player, RootContainer cont) {
				Tile tile = (Tile) blockEntity;
				List<ASlot> playerSlots = new ArrayList<>(36);
				ASlot inputA = new APortSlot(blockEntity, 0);
				ASlot inputB = new APortSlot(blockEntity, 1);
				ASlot fuel = new APortSlot(blockEntity, 2);
				ASlot output = new APortSlot(blockEntity, 3);
				List<ASlot> prioritySlots = ImmutableList.of(inputA, inputB, fuel, output);
				ACenteringPanel panel = defaultGui(cont, player, playerSlots, prioritySlots);
				panel.add(new ALabel(DLang.BASIC_ALLOY_KILN, 0x404040, false).setTransformation(Transformation.translate(5, 5, 0)));

				inputA.linkAll(cont, playerSlots);
				panel.add(inputA.setTransformation(Transformation.translate(46, 15, 0)));
				inputB.linkAll(cont, playerSlots);
				panel.add(inputB.setTransformation(Transformation.translate(64, 15, 0)));
				panel.add(fuel.setTransformation(Transformation.translate(55, 50, 0)));
				fuel.linkAll(cont, playerSlots);
				panel.add(output.setTransformation(Transformation.translate(120, 30, 0)));
				output.linkAll(cont, playerSlots);

				AProgressBar progress = new AProgressBar(AstrarreIcons.FURNACE_PROGRESS_BAR_FULL,
						AstrarreIcons.FURNACE_PROGRESS_BAR_EMPTY,
						AProgressBar.Direction.RIGHT);
				AProgressBar burnTime = new AProgressBar(AstrarreIcons.FURNACE_FLAME_ON, AstrarreIcons.FURNACE_FLAME_OFF, AProgressBar.Direction.UP);
				Val.Listener<Integer> listener =
						tile.burnTime.addListener((integer, v1) -> burnTime.progress.set(v1.floatValue() / tile.lastBurnTime));
				cont.addCloseListener(() -> tile.burnTime.removeListener(listener));
				Val.Listener<Integer> listener2 =
						tile.progress.addListener((integer, v1) -> progress.progress.set(v1.floatValue() / tile.lastTotalProgress));
				cont.addCloseListener(() -> tile.progress.removeListener(listener2));
				panel.add(burnTime.setTransformation(Transformation.translate(57, 35, 0)));
				panel.add(progress.setTransformation(Transformation.translate(90, 31, 0)));
			}
		});
	}

	@Override
	protected BlockEntity createBlockEntity(@Nullable BlockView world) {
		return new Tile();
	}

	@Override
	@Environment (EnvType.CLIENT)
	public void clientInit() {
		super.clientInit();
		// models go brr
		CubeData off = CubeData.withAll(new Identifier("block/furnace_side"))
		                       .withBlock(Direction.UP, new Identifier("block/furnace_top"), false)
		                       .withBlock(Direction.DOWN, new Identifier("block/furnace_top"), false)
		                       .withBlock(Direction.NORTH, Devtech.id("block/tier2/basic_alloy_kiln_off"), false);
		DevtechModelProvider.add(new CubeMachineBakedModel(DSprites.FURNACE_CHASSIS, off),
				"block/primitive_alloy_furnace_off",
				"item/primitive_alloy_furnace");
		DevtechModelProvider.add(new CubeMachineBakedModel(DSprites.FURNACE_CHASSIS,
				off.withBlock(Direction.NORTH, Devtech.id("block/tier2/basic_alloy_kiln_on"), false)
				   .addBlock(Direction.NORTH, Devtech.id("block/tier2/basic_alloy_kiln_flame"), true)), "block/primitive_alloy_furnace_lit");
	}

	public class Tile extends FacingBlockEntity {
		public final Val<Integer> progress = Val.ofInteger(), burnTime = Val.ofInteger();
		public int lastTotalProgress = 1, lastBurnTime = 1;

		public Tile() {
			// primitive alloy furnace comes with ports installed in it, this is to ease the player into the concept
			// and most importantly, it means ports can be crafted with alloys for balance:tm:
			this.relativePorts.put(Direction.EAST, new InputItemPort(PortColor.RED)); // alloy 1
			this.relativePorts.put(Direction.WEST, new InputItemPort(PortColor.BLUE)); // alloy 2
			this.relativePorts.put(Direction.SOUTH, new InputItemPort(PortColor.GREEN)); // fuel
			this.relativePorts.put(Direction.DOWN, new OutputItemPort(PortColor.NONE)); // output
		}

		@Override
		public void fromTag(BlockState state, CompoundTag tag) {
			super.fromTag(state, tag);
			this.progress.set(tag.getInt("progress"));
			this.burnTime.set(tag.getInt("burnTime"));
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			tag.putInt("progress", this.progress.get());
			tag.putInt("burnTime", this.burnTime.get());
			return super.toTag(tag);
		}

		@Override
		public boolean setPort(Direction direction, @Nullable Port port) {
			return false;
		}
	}
}
