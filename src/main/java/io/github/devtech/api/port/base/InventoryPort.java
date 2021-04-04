package io.github.devtech.api.port.base;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.transfer.v0.fabric.inventory.InventoryDelegate;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.registry.DPorts;
import io.github.devtech.api.registry.DSprites;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.Direction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class InventoryPort extends Port implements InventoryDelegate {
	private final SimpleInventory inventory;

	public InventoryPort(PortColor color) {
		super(color);
		this.inventory = new SimpleInventory(1) {
			@Override
			public boolean isValid(int slot, ItemStack stack) {
				return InventoryPort.this.isValid;
			}
		};
	}

	public InventoryPort(NBTagView tag) {
		super(tag);
		SimpleInventory inv = new SimpleInventory(1);
		inv.readTags((ListTag) tag.getValue("inventory"));
		this.inventory = inv;
	}

	@Override
	public void tick(BlockEntity entity, Direction face) {
		super.tick(entity, face);
	}

	@Override
	public void write(NBTagView.Builder tag) {
		super.write(tag);
		tag.putValue("inventory", (NbtValue) this.inventory.getTags());
	}


	@Override
	@Environment (EnvType.CLIENT)
	public SpriteIdentifier getBarTexture() {
		return DSprites.SMALL_BAR;
	}

	@Override
	public Inventory getInventoryDelegate() {
		return this.inventory;
	}
}
