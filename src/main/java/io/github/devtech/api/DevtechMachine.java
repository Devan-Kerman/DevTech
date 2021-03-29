package io.github.devtech.api;

import java.util.EnumMap;
import java.util.Map;

import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.statik.ABeveledRectangle;
import io.github.astrarre.gui.v0.fabric.adapter.slot.APlayerSlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;

public abstract class DevtechMachine {
	public final Block block;
	public final Item item;
	public final BlockEntityType<?> blockEntityType;
	protected final Identifier id;

	public DevtechMachine(String id) {
		this(Devtech.id(id));
	}

	public DevtechMachine(Identifier id) {
		this.id = id;
		this.block = Registry.register(Registry.BLOCK, id, this.createBlock(this.createBlockSettings()));
		this.blockEntityType = Registry.register(
				Registry.BLOCK_ENTITY_TYPE,
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

	public class DefaultBlock extends Block implements BlockEntityProvider {
		public DefaultBlock(Settings settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity(BlockView world) {
			return DevtechMachine.this.createBlockEntity(world);
		}
	}

	public abstract class DefaultBlockEntity extends BlockEntity implements PortAccess, Tickable {
		protected final Map<Direction, Port> relativePorts = new EnumMap<>(Direction.class);

		public DefaultBlockEntity() {
			super(DevtechMachine.this.blockEntityType);
		}

		@Override
		public Port getPortAbsolute(Direction direction) {
			return this.getPort(direction);
		}

		@Override
		public @Nullable Port getPort(Direction direction) {
			return this.relativePorts.get(direction);
		}

		@Override
		public boolean install(Direction direction, @Nullable Port port) {
			this.relativePorts.put(direction, port);
			return true;
		}

		@Override
		public void tick() {
			this.relativePorts.forEach((direction, port) -> port.tick(this, direction));
		}

		@Override
		public void fromTag(BlockState state, CompoundTag tag) {
			super.fromTag(state, tag);
			for (Direction direction : Devtech.DIRECTIONS) {
				String key = "port_" + direction.getName();
				if(tag.getType(key) != NbtType.END) {
					this.relativePorts.put(direction, Port.SERIALIZER.read(FabricViews.view(tag), key));
				}
			}
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			NBTagView.Builder builder = (NBTagView.Builder) tag;
			for (Direction direction : Devtech.DIRECTIONS) {
				Port port = this.getPort(direction);
				if (port != null) {
					Port.SERIALIZER.save(builder, "port_"+direction.getName(), port);
				}
			}
			return super.toTag(tag);
		}
	}

	public static ACenteringPanel templateGui(PlayerEntity player) {
		ACenteringPanel center = new ACenteringPanel(175, 165);
		center.add(new ABeveledRectangle(center));
		for(int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
			for(int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
				ASlot slot = new APlayerSlot(player.inventory, inventoryColumn + inventoryRow * 9 + 9);
				slot.setTransformation(Transformation.translate(6 + inventoryColumn * 18, 82 + inventoryRow * 18, 0));
				center.add(slot);
			}
		}

		for(int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
			ASlot slot = new APlayerSlot(player.inventory, hotbarIndex);
			slot.setTransformation(Transformation.translate(6 + hotbarIndex * 18, 140, 0));
			center.add(slot);
		}
		return center;
	}
}
