package io.github.devtech.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.widgets.AInfo;
import io.github.astrarre.gui.v0.api.base.widgets.ALabel;
import io.github.astrarre.gui.v0.api.base.widgets.AProgressBar;
import io.github.astrarre.gui.v0.api.base.widgets.AToggleable;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ABlockEntityInventorySlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.transfer.v0.fabric.inventory.InventoryDelegate;
import io.github.devtech.Devtech;
import io.github.devtech.api.DevtechMachine;
import io.github.devtech.api.registry.DLang;
import io.github.devtech.api.registry.DTextures;
import io.github.devtech.api.registry.DTiles;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.util.NbtType;

public class SolarCookerBlock extends Block implements BlockEntityProvider {

	public SolarCookerBlock() {
		super(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque());
		this.setDefaultState(this.getStateManager().getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(HorizontalFacingBlock.FACING);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && player instanceof ServerPlayerEntity) {
			Tile tile = (Tile) world.getBlockEntity(pos);
			RootContainer.open((NetworkMember) player, cont -> {
				List<ASlot> playerSlots = new ArrayList<>(36);
				ASlot inputA = new ABlockEntityInventorySlot<>(tile, 0);
				ASlot output = new ABlockEntityInventorySlot<>(tile, 1);
				List<ASlot> prioritySlots = ImmutableList.of(inputA, output);
				ACenteringPanel panel = DevtechMachine.Configuration.defaultGui(cont, player, playerSlots, prioritySlots);
				AInfo info = new AInfo(new ALabel(DLang.SOLAR_COOKER, 0x404040, false), Collections.singletonList(DLang.SOLAR_COOKER_DESC), 190);
				info.setTransformation(Transformation.translate(5, 5, 0));
				panel.add(info);
				inputA.linkAll(cont, playerSlots);
				panel.add(inputA.setTransformation(Transformation.translate(51.5f, 35, 0)));
				output.linkAll(cont, playerSlots);
				panel.add(output.setTransformation(Transformation.translate(106.5f, 35, 0)));
				panel.add(tile.initGuiComponents());
				panel.add(new AInfo(tile.daytimeComponent, Collections.singletonList(DLang.SOLAR_COOKER_DESC), 100));
				return null;
			});

		}
		return ActionResult.CONSUME;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new Tile();
	}

	public static final class Tile extends BlockEntity implements InventoryDelegate, Tickable {
		private final SimpleInventory inventory = new SimpleInventory(2);
		public AProgressBar progress;
		public AToggleable daytimeComponent;

		public Tile() {
			super(DTiles.SOLAR_COOKER);

		}

		@Override
		public Inventory getInventoryDelegate() {
			return this.inventory;
		}

		@Override
		public boolean isValid(int slot, ItemStack stack) {
			return slot == 0;
		}

		@Override
		public void fromTag(BlockState state, CompoundTag tag) {
			super.fromTag(state, tag);
			this.inventory.readTags(tag.getList("inventory", NbtType.COMPOUND));
			if (this.world != null && !this.world.isClient) {
				this.initGuiComponents().progress.set(tag.getFloat("progress"));
			}
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			tag.put("inventory", this.inventory.getTags());
			if (this.world != null && !this.world.isClient) {
				tag.putFloat("progress", this.initGuiComponents().progress.get());
			}
			return super.toTag(tag);
		}

		public AProgressBar initGuiComponents() {
			if (this.progress == null && !this.world.isClient) {
				this.progress = new AProgressBar(AstrarreIcons.FURNACE_PROGRESS_BAR_FULL,
						AstrarreIcons.FURNACE_PROGRESS_BAR_EMPTY,
						AProgressBar.Direction.RIGHT);
				this.progress.setTransformation(Transformation.translate(76.5f, 37, 0));
				this.daytimeComponent = new AToggleable(DTextures.SUN_ON, DTextures.SUN_OFF);
				this.daytimeComponent.setTransformation(Transformation.translate(30, 39, 0));
			}
			return this.progress;
		}

		@Override
		public void tick() {
			if (!this.world.isClient) {
				this.initGuiComponents();
				int lightLevel = this.getDaylight();
				if (this.daytimeComponent.enabled.get() ^ (lightLevel != 0)) {
					this.daytimeComponent.enabled.set(lightLevel != 0);
					this.markDirty();
				}
				if (lightLevel == 0) {
					if (this.progress.progress.get() > 0) {
						this.progress.progress.set(Math.max(0, this.progress.progress.get() - 0.01f));
						this.markDirty();
					}
				} else {
					Recipe<?> recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, this.world).orElse(null);
					if (this.canAcceptRecipeOutput(recipe)) {
						float speed = lightLevel / 15f;
						this.progress.progress.set(this.progress.progress.get() + 0.005f * speed);
						if (this.progress.progress.get() >= 1f) {
							this.progress.progress.set(0f);
							ItemStack recipeOutput = recipe.getOutput();
							ItemStack currentOutput = this.inventory.getStack(1);
							this.inventory.getStack(0).decrement(1);
							if (currentOutput.isEmpty()) {
								this.inventory.setStack(1, recipeOutput.copy());
							} else {
								currentOutput.increment(recipeOutput.getCount());
							}
						}
						this.markDirty();
					} else {
						if(this.progress.progress.get() != 0f) {
							this.progress.progress.set(0f);
							this.markDirty();
						}
					}
				}
			}
		}

		public int getDaylight() {
			int lightLevel = this.world.getLightLevel(LightType.SKY, this.pos);
			if(this.world.isNight()) lightLevel = 0;
			return lightLevel;
		}

		protected boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe) {
			if (recipe != null) {
				ItemStack recipeOutput = recipe.getOutput();
				if (recipeOutput.isEmpty()) {
					return false;
				} else {
					ItemStack currentOutput = this.inventory.getStack(1);
					if (currentOutput.isEmpty()) {
						return true;
					} else if (!currentOutput.isItemEqualIgnoreDamage(recipeOutput)) {
						return false;
					} else if (currentOutput.getCount() < this.getMaxCountPerStack() && currentOutput.getCount() < currentOutput.getMaxCount()) {
						return true;
					} else {
						return currentOutput.getCount() < recipeOutput.getMaxCount();
					}
				}
			} else {
				return false;
			}
		}
	}
}
