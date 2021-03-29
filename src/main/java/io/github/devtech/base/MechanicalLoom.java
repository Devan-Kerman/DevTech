package io.github.devtech.base;

import java.util.List;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.fabric.provider.BlockEntityProvider;
import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.borders.ABeveledBorder;
import io.github.astrarre.gui.v0.api.base.borders.ASimpleBorder;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.base.statik.ADarkenedBackground;
import io.github.astrarre.gui.v0.api.base.widgets.AProgressBar;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ABlockEntityInventorySlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.recipes.v0.api.RecipeFile;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.transfer.internal.inventory.InventoryDelegate;
import io.github.astrarre.transfer.v0.api.participants.ExtractableParticipant;
import io.github.astrarre.transfer.v0.api.participants.InsertableParticipant;
import io.github.astrarre.transfer.v0.fabric.item.ItemSlotParticipantInventory;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.devtech.Devtech;
import io.github.devtech.api.StandardDevtechMachine;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.port.base.ItemPort;
import io.github.devtech.api.port.base.RotationalInputPort;
import io.github.devtech.api.registry.DLang;
import io.github.devtech.api.registry.DSprites;
import io.github.devtech.base.gui.ABar;
import io.github.devtech.base.gui.ARotatingWheel;
import io.github.devtech.client.MachineModelProvider;
import io.github.devtech.client.model.CubeData;
import io.github.devtech.client.model.CubeMachineBakedModel;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

public class MechanicalLoom extends StandardDevtechMachine {
	public static final RecipeFile MECHANICAL_LOOM_FILE = new RecipeFile(Devtech.id("mechanical_loom"));

	public MechanicalLoom() {
		super("test_machine");
	}

	@Override
	protected BlockEntity createBlockEntity(@Nullable BlockView world) {
		return new BlockEntity();
	}

	@Override
	protected Item createItem(Block block, Item.Settings settings) {
		return new BlockItem(block, settings) {
			@Override
			@Environment (EnvType.CLIENT)
			public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
				super.appendTooltip(stack, world, tooltip, context);
				tooltip.add(DLang.MECHANICAL_LOOM_DESC);
				tooltip.add(DLang.MECHANICAL_LOOM_HELPER);
				if (Screen.hasShiftDown()) {
					tooltip.add(DLang.FILLER);
					tooltip.add(DLang.INTEGRATED_PORTS);
					tooltip.add(Text.of("  - ").copy().append(DLang.INPUT_ITEM_PORT).formatted(Formatting.BLUE));
					tooltip.add(Text.of("  - ").copy().append(DLang.OUTPUT_ITEM_PORT).formatted(Formatting.RED));
					tooltip.add(Text.of("  - ").copy().append(DLang.INPUT_ROTATIONAL_ENERGY_PORT));
				} else {
					tooltip.add(DLang.HOLD_SHIFT_FOR_MORE);
				}
			}
		};
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void clientInit() {
		MachineModelProvider.add("block/test_machine_off", new CubeMachineBakedModel(
				DSprites.WOODEN_CHASSIS,
				CubeData.withAll(Devtech.id("block/tier1/wooden_chassis")).withBlock(Direction.UP, Devtech.id("block/tier1/mechanical_loom_off"))
						.withBlock(Direction.NORTH, Devtech.id("block/tier1/mechanical_loom_front"))));
		MachineModelProvider.add("block/test_machine_lit", new CubeMachineBakedModel(
				DSprites.WOODEN_CHASSIS,
				CubeData.withAll(Devtech.id("block/tier1/wooden_chassis")).withBlock(Direction.UP, Devtech.id("block/tier1/mechanical_loom_on"))
						.withBlock(Direction.NORTH, Devtech.id("block/tier1/mechanical_loom_front"))));
	}

	@Override
	protected Block createBlock(AbstractBlock.Settings settings) {
		return new StandardBlock(settings) {
			@Override
			public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
				if (!world.isClient && player instanceof ServerPlayerEntity) {
					BlockEntity entity = (BlockEntity) world.getBlockEntity(pos);
					RootContainer.open((NetworkMember) player, container -> {
						APanel contentPanel = container.getContentPanel();
						contentPanel.add(new ADarkenedBackground());
						ACenteringPanel panel = templateGui(player);
						contentPanel.add(panel);
						panel.add(entity.bar);
						entity.onInit();
						APanel rotational = new APanel();
						rotational.setBounds(Polygon.rectangle(128, 8));
						ABar bar = new ABar(118, 8, DLang.TORQUE, DLang.TORQUE_PIN, DLang.TORQUE_EXCESS);
						bar.setTransformation(Transformation.translate(10, 0, 0));
						bar.minimumRequired.set(.5f);
						bar.percentage.set(.6f);
						rotational.add(bar);
						ARotatingWheel wheel = new ARotatingWheel();
						wheel.rotationsPerTick.set(1/180f);
						rotational.add(wheel);
						panel.add(new ABeveledBorder(rotational).setTransformation(Transformation.translate(10, 50, 0)));
						panel.add(entity.input);
						panel.add(entity.output);
						return null;
					});
				}
				return ActionResult.CONSUME;
			}
		};
	}

	public class BlockEntity extends FacingBlockEntity implements BlockEntityProvider, BlockEntityClientSerializable, InventoryDelegate {
		public final ItemSlotParticipantInventory inventory = new ItemSlotParticipantInventory();
		public ASlot input, output;
		public final AProgressBar bar = new AProgressBar(AstrarreIcons.FURNACE_PROGRESS_BAR_FULL, AstrarreIcons.FURNACE_PROGRESS_BAR_EMPTY, AProgressBar.Direction.RIGHT);

		public BlockEntity() {
			this.relativePorts.put(Direction.SOUTH, new RotationalInputPort(PortColor.NONE));
			this.relativePorts.put(Direction.EAST, new ItemPort(PortColor.RED));
			this.relativePorts.put(Direction.WEST, new ItemPort(PortColor.BLUE));
			this.bar.setTransformation(Transformation.translate(76.5f, 20, 0));
			this.inventory.add(this.getInput().participant);
			this.inventory.add(this.getOutput().participant);
		}

		@Override
		public void tick() {
			super.tick();
		}

		protected void onInit() {
			if(this.input == null) {
				this.input = new ABlockEntityInventorySlot<>(this, 0);
				this.output = new ABlockEntityInventorySlot<>(this, 1);
				this.input.setTransformation(Transformation.translate(38.5f, 20, 0));
				this.output.setTransformation(Transformation.translate(118.5f, 20, 0));
			}
		}

		@Override
		public void setLocation(World world, BlockPos pos) {
			super.setLocation(world, pos);
			this.input = null;
			this.output = null;
		}

		@Override
		public void setPos(BlockPos pos) {
			super.setPos(pos);
			this.input = null;
			this.output = null;
		}

		@Override
		public boolean install(Direction direction, @Nullable Port port) {
			return false;
		}

		@Override
		public @Nullable Object get(Access<?> access, Direction direction) {
			if (access == FabricParticipants.ITEM_WORLD) {
				Direction relativized = MechanicalLoom.this.relativize(direction, this.getCachedState());
				if (relativized == Direction.DOWN || relativized == Direction.EAST) {
					return new ExtractableParticipant<>(this.getOutput().participant);
				} else if (relativized == Direction.WEST || relativized == Direction.UP) {
					return new InsertableParticipant<>(this.getInput().participant);
				}
			}
			return null;
		}

		public ItemPort getOutput() {
			return (ItemPort) this.relativePorts.get(Direction.EAST);
		}

		public ItemPort getInput() {
			return (ItemPort) this.relativePorts.get(Direction.WEST);
		}

		@Override
		public void fromClientTag(CompoundTag tag) {
			this.fromTag(null, tag);
		}

		@Override
		public CompoundTag toClientTag(CompoundTag tag) {
			return this.toTag(tag);
		}

		@Override
		public Inventory getDelegate() {
			return this.inventory;
		}

		@Override
		public boolean isValid(int slot, ItemStack stack) {
			return slot == 0;
		}
	}
}
