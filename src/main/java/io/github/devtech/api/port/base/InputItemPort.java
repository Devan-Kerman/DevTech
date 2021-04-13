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

public class InputItemPort extends InventoryPort implements InventoryDelegate {
	public InputItemPort(PortColor color) {
		super(color);
	}

	public InputItemPort(NBTagView tag) {
		super(tag);
	}

	@Override
	public Entry getEntry() {
		return DPorts.ITEM_INPUT;
	}
}
