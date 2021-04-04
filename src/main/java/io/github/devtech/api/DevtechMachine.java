package io.github.devtech.api;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.statik.ABeveledRectangle;
import io.github.astrarre.gui.v0.api.base.statik.ADarkenedBackground;
import io.github.astrarre.gui.v0.api.base.widgets.ALabel;
import io.github.astrarre.gui.v0.fabric.adapter.slot.APlayerSlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.devtech.Devtech;
import io.github.devtech.api.access.PortAccess;
import io.github.devtech.api.datagen.ResourceGenerator;
import io.github.devtech.api.datagen.item.BlockItemGenerator;
import io.github.devtech.api.port.Port;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.util.NbtType;

public abstract class DevtechMachine {
	public final Block block;
	public final Item item;
	public final BlockEntityType<?> blockEntityType;
	protected final List<Configuration> configs = new ArrayList<>();
	protected final Identifier id;

	public DevtechMachine(String id) {
		this(Devtech.id(id));
	}

	public DevtechMachine(Identifier id) {
		this.id = id;
		this.block = Registry.register(Registry.BLOCK, id, this.createBlock(this.createBlockSettings()));
		this.blockEntityType = Registry.register(Registry.BLOCK_ENTITY_TYPE,
				id,
				BlockEntityType.Builder.create(() -> this.createBlockEntity(null), this.block).build(null));
		this.item = Registry.register(Registry.ITEM, id, this.createItem(this.block, this.createItemSettings(this.block)));
	}

	protected Block createBlock(AbstractBlock.Settings settings) {
		return new DefaultBlock(settings);
	}

	protected AbstractBlock.Settings createBlockSettings() {
		return AbstractBlock.Settings.copy(Blocks.STONE);
	}

	protected abstract BlockEntity createBlockEntity(@Nullable BlockView world);

	protected Item createItem(Block block, Item.Settings settings) {
		return new BlockItem(block, settings);
	}

	protected Item.Settings createItemSettings(Block block) {
		return new Item.Settings().maxCount(1);
	}

	public ResourceGenerator[] getGenerators() {
		return new ResourceGenerator[] {new BlockItemGenerator(this.id)};
	}

	@Environment (EnvType.CLIENT)
	public void clientInit() {}

	public static class Configuration {
		public final List<Port.Type> portTypes;

		public Configuration(List<Port.Type> types) {this.portTypes = types;}

		public void tick(DefaultBlockEntity blockEntity, List<Port> ports) {}

		public SidedInventory getInventory(DefaultBlockEntity be, List<Port> ports, List<Direction> absoluteDirections) {return null;}

		protected ACenteringPanel defaultGui(RootContainer container, PlayerEntity player, List<ASlot> playerSlot, List<ASlot> prioritySlots) {
			ACenteringPanel center = new ACenteringPanel(175, 165);
			container.getContentPanel().add(new ADarkenedBackground());
			center.add(new ABeveledRectangle(center));
			center.add(new ALabel(player.inventory.getName(), 0x404040, false).setTransformation(Transformation.translate(6, 71, 0)));
			List<ASlot> hotbar = new ArrayList<>();
			for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
				for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
					ASlot slot = new APlayerSlot(player.inventory, inventoryColumn + inventoryRow * 9 + 9);
					slot.setTransformation(Transformation.translate(6 + inventoryColumn * 18, 82 + inventoryRow * 18, 0));
					slot.linkAll(container, prioritySlots);
					center.add(slot);
					hotbar.add(slot);
				}
			}
			playerSlot.addAll(hotbar);

			for (int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
				ASlot slot = new APlayerSlot(player.inventory, hotbarIndex);
				slot.setTransformation(Transformation.translate(6 + hotbarIndex * 18, 140, 0));
				slot.linkAll(container, prioritySlots);
				center.add(slot);
				slot.linkAll(container, hotbar);
				for (ASlot hotbarSlot : hotbar) {
					hotbarSlot.link(container, slot);
				}
				playerSlot.add(slot);
			}
			container.getContentPanel().add(center);
			return center;
		}

		public RootContainer openGui(DefaultBlockEntity blockEntity, PlayerEntity player) {
			return null;
		}
	}

	public class DefaultBlock extends Block implements BlockEntityProvider, InventoryProvider {
		public DefaultBlock(Settings settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity(BlockView world) {
			return DevtechMachine.this.createBlockEntity(world);
		}

		@Override
		public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
			DefaultBlockEntity entity = (DefaultBlockEntity) world.getBlockEntity(pos);
			Configuration active = entity.activeConfig;
			if (active == null) {
				return null;
			}
			List<Direction> absoluteDirections = new ArrayList<>();
			for (Port port : entity.sortedPorts) {
				for (Map.Entry<Direction, Port> entry : entity.relativePorts.entrySet()) {
					Direction direction = entry.getKey();
					Port p = entry.getValue();
					if (p == port) {
						absoluteDirections.add(entity.derelativize(direction));
						break;
					}
				}
			}
			return active.getInventory(entity, entity.sortedPorts, absoluteDirections);
		}

		@Override
		public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
			if (!world.isClient) {
				DefaultBlockEntity entity = (DefaultBlockEntity) world.getBlockEntity(pos);
				Configuration active = entity.activeConfig;
				if (active != null) {
					RootContainer container = active.openGui(entity, player);
					if(container != null) {
						entity.openContainers.put(container, Unit.INSTANCE);
						container.addCloseListener(() -> entity.openContainers.remove(container));
					}
				}
				return ActionResult.CONSUME;
			}
			return ActionResult.CONSUME;
		}
	}

	public abstract class DefaultBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable, PortAccess {
		protected final WeakHashMap<RootContainer, Unit> openContainers = new WeakHashMap<>();
		// cannot have 2 of the same port type
		protected final Map<Direction, Port> relativePorts = new EnumMap<>(Direction.class);
		protected boolean recomputeConfig = true;
		public List<Port> sortedPorts;
		protected Configuration activeConfig;

		public DefaultBlockEntity() {
			super(DevtechMachine.this.blockEntityType);
		}

		@Override
		public void tick() {
			if (this.recomputeConfig) {
				for (Configuration configuration : DevtechMachine.this.configs) {
					boolean matches = this.relativePorts.values().stream().map(Port::getType).filter(configuration.portTypes::contains)
							                  .count() == configuration.portTypes.size();
					if (matches && this.activeConfig != configuration) {
						this.sortedPorts = null;
						this.activeConfig = configuration;
					}
				}
			}

			if (this.activeConfig == null) {
				return;
			}

			if (this.sortedPorts == null) {
				List<Port.Type> types = this.activeConfig.portTypes;
				List<Port> sorted = new ArrayList<>();
				types.forEach(t -> sorted.add(null));
				for (Port value : this.relativePorts.values()) {
					sorted.set(types.indexOf(value.getType()), value);
				}
				this.sortedPorts = sorted;
			}

			if(this.world == null || this.world.isClient) return;
			this.relativePorts.forEach((direction, port) -> port.tick(this, this.derelativize(direction)));
			this.activeConfig.tick(this, this.sortedPorts);
		}

		@Override
		public void markRemoved() {
			super.markRemoved();
		}

		protected Direction derelativize(Direction relative) {
			return relative;
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
		public void setLocation(World world, BlockPos pos) {
			super.setLocation(world, pos);
			this.onReposition();
		}

		@Override
		public void fromTag(BlockState state, CompoundTag tag) {
			super.fromTag(state, tag);
			for (Direction direction : Devtech.DIRECTIONS) {
				String key = "port_" + direction.getName();
				if (tag.getType(key) != NbtType.END) {
					this.relativePorts.put(direction, Port.SERIALIZER.read(FabricViews.view(tag), key));
				}
			}
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			NBTagView.Builder builder = (NBTagView.Builder) tag;
			for (Direction direction : Devtech.DIRECTIONS) {
				Port port = this.relativePorts.get(direction);
				if (port != null) {
					Port.SERIALIZER.save(builder, "port_" + direction.getName(), port);
				}
			}
			return super.toTag(tag);
		}

		@Override
		public void setPos(BlockPos pos) {
			super.setPos(pos);
			this.onReposition();
		}

		public void closeScreens() {
			this.openContainers.keySet().stream().map(RootContainer::getViewer).filter(Objects::nonNull).map(NetworkMember::to)
					.forEach(ServerPlayerEntity::closeScreenHandler);
			this.openContainers.clear();
		}

		protected void onReposition() {}

		@Override
		public Port getPortAbsolute(Direction direction) {
			return this.getPort(direction);
		}

		@Override
		public @Nullable Port getPort(Direction direction) {
			return this.relativePorts.get(direction);
		}

		@Override
		public boolean setPort(Direction direction, @Nullable Port port) {
			this.recomputeConfig = true;
			this.activeConfig = null;
			this.closeScreens();
			Port old = this.relativePorts.put(direction, port);
			if(old != null) old.invalidate();
			return true;
		}
	}

}
