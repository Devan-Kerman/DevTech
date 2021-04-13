package io.github.devtech.api.port.base;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.registry.DPorts;
import io.github.devtech.api.registry.DSprites;

import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class OutputItemPort extends InventoryPort {
	public OutputItemPort(PortColor color) {
		super(color);
	}

	public OutputItemPort(NBTagView tag) {
		super(tag);
	}

	@Override
	public Entry getEntry() {
		return DPorts.ITEM_OUTPUT;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return false;
	}
}
