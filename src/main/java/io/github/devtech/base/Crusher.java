package io.github.devtech.base;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.fabric.provider.BlockEntityProvider;
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
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.InsertableParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.util.v0.api.Val;
import io.github.devtech.Devtech;
import io.github.devtech.api.StandardDevtechMachine;
import io.github.devtech.api.participants.type.RPT;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.port.base.InputItemPort;
import io.github.devtech.api.port.base.OutputItemPort;
import io.github.devtech.api.port.base.RotationalInputPort;
import io.github.devtech.api.registry.DAccess;
import io.github.devtech.api.registry.DLang;
import io.github.devtech.api.registry.DPorts;
import io.github.devtech.api.registry.DSprites;
import io.github.devtech.base.gui.APortSlot;
import io.github.devtech.client.DevtechModelProvider;
import io.github.devtech.client.model.CubeData;
import io.github.devtech.client.model.CubeMachineBakedModel;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class Crusher extends StandardDevtechMachine {
	/**
	 * input item minimum torque rotational energy
	 */
	public static final Recipe.Quad<Inventory, Integer, Val<Integer>, Inventory> RECIPE = Recipe.builder()
		.add(FabricIngredients.ITEM_INGREDIENT)
		.add(Ingredients.REQUIRE_INT)
		.add(Ingredients.INTEGER)
		.outputs(Devtech.id2("solid_solid"))
		.add(FabricOutputs.ITEM_STACK)
		.build("Solid->Solid Crusher");
	public static final RecipeFile RECIPE_FILE = new RecipeFile(Devtech.id("crusher")).add(RECIPE);

	public Crusher(Identifier id) {
		super(id);
		this.configs.add(new ItemIn());
	}

	@Override
	protected AbstractBlock.Settings createBlockSettings() {
		return super.createBlockSettings().luminance(value -> 0);
	}

	@Override
	protected BlockEntity createBlockEntity(@Nullable BlockView world) {
		return new Tile();
	}

	@Override
	public void clientInit() {
		super.clientInit();
		CubeData off = CubeData.withAll(Devtech.id("block/tier4/steampunk_side"))
		                       .withBlock(Direction.UP, Devtech.id("block/tier4/crusher_top"), false)
		                       .withBlock(Direction.DOWN, Devtech.id("block/tier4/steampunk_top"), false)
		                       .withBlock(Direction.NORTH, Devtech.id("block/tier4/crusher_off"), false);
		DevtechModelProvider.add(new CubeMachineBakedModel(DSprites.FURNACE_CHASSIS, off),
				"block/crusher_off",
				"item/crusher");
		DevtechModelProvider.add(new CubeMachineBakedModel(DSprites.FURNACE_CHASSIS,
				off.withBlock(Direction.NORTH, Devtech.id("block/tier4/crusher_on"), false)), "block/crusher_lit");
	}

	public class Tile extends FacingBlockEntity implements BlockEntityProvider, Insertable<RPT> {
		private final Participant<RPT> this_ = new InsertableParticipant<>(this);
		public final Val<Integer> energy = Val.ofInteger();
		public final AProgressBar progress = new AProgressBar(
				AstrarreIcons.FURNACE_PROGRESS_BAR_FULL,
				AstrarreIcons.FURNACE_PROGRESS_BAR_EMPTY,
				AProgressBar.Direction.RIGHT);
		public float lastTotalEnergy;

		public Tile() {
			this.energy.addListener((integer, v1) -> this.progress.progress.set(v1 / this.lastTotalEnergy));
			this.energy.addListener((old, current) -> this.markDirty());
			this.progress.setTransformation(Transformation.translate(76, 36, 0));
		}

		@Override
		public @Nullable Object get(Access<?> access, Direction direction) {
			return access == DAccess.ROTATIONAL_ENERGY_ACCESS ? this.this_ : null;
		}

		boolean ticked = false;
		@Override
		public void tick() {
			super.tick();
			if(!ticked) {
				this.setPort(Direction.UP, new InputItemPort(PortColor.NONE));
				this.setPort(Direction.NORTH, new RotationalInputPort(PortColor.NONE));
				this.setPort(Direction.DOWN, new OutputItemPort(PortColor.NONE));
				ticked = true;
			}
			if(!this.world.isClient) {
				this.insert(Transaction.GLOBAL, new RPT(1), 10);
			}
		}

		// todo fix transactions
		@Override
		public int insert(@Nullable Transaction transaction, RPT type, int power) {
			Configuration config = this.activeConfig;
			if(config instanceof Insert) {
				return ((Insert) config).insert(this, this.sortedPorts, type.rpt, power);
			}
			return 0;
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			tag.putInt("energy", this.energy.get());
			return super.toTag(tag);
		}

		@Override
		public void fromTag(BlockState state, CompoundTag tag) {
			super.fromTag(state, tag);
			this.energy.set(tag.getInt("energy"));
		}
	}
	
	public final static class ItemIn extends Configuration implements Insert {
		public ItemIn() {
			super(true,
					new Port.Type(DPorts.ROTATIONAL_INPUT, PortColor.NONE),
					new Port.Type(DPorts.ITEM_INPUT, PortColor.NONE),
					new Port.Type(DPorts.ITEM_OUTPUT, PortColor.NONE));
		}

		@Override
		public int insert(Tile tile, List<Port> ports, int rpt, int power) {
			int torque = power / rpt;
			Result result = RECIPE.apply((InputItemPort)ports.get(1), torque, tile.energy, ((OutputItemPort)ports.get(2)).getInventoryDelegate());
			int failIndex = result.getFailedIndex();
			if(failIndex == 2) {
				Integer recipeTorque = result.getInput(Ingredients.REQUIRE_INT, 1);
				int consumed = recipeTorque * rpt;
				tile.energy.set(tile.energy.get() + consumed);
				tile.lastTotalEnergy = result.getInput(Ingredients.REQUIRE_INT, 2);
				return consumed;
			} else {
				tile.energy.set(0);
			}

			return 0;
		}

		@Override
		public void openGui(DefaultBlockEntity blockEntity, PlayerEntity player, RootContainer cont) {
			Tile tile = (Tile) blockEntity;
			List<ASlot> playerSlots = new ArrayList<>(36);
			ASlot inputA = new APortSlot(blockEntity, 1);
			ASlot inputB = new APortSlot(blockEntity, 2);
			List<ASlot> prioritySlots = ImmutableList.of(inputA, inputB);
			ACenteringPanel panel = defaultGui(cont, player, playerSlots, prioritySlots);
			panel.add(new ALabel(DLang.BASIC_ALLOY_KILN, 0x404040, false).setTransformation(Transformation.translate(5, 5, 0)));
			inputA.linkAll(cont, playerSlots);
			panel.add(inputA.setTransformation(Transformation.translate(45, 35, 0)));
			inputB.linkAll(cont, playerSlots);
			panel.add(inputB.setTransformation(Transformation.translate(110, 35, 0)));
			panel.add(tile.progress);
		}
	}
	
	public interface Insert {
		int insert(Tile tile, List<Port> ports, int rpt, int power);
	}
}
