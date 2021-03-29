package io.github.devtech.api.port.base;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.registry.DPorts;
import io.github.devtech.api.registry.DSprites;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ItemPort extends Port {
	public final ItemSlotParticipant participant;

	public ItemPort(PortColor color) {
		super(color);
		this.participant = new ItemSlotParticipant();
	}

	public ItemPort(NBTagView tag) {
		super(tag);
		this.participant = ItemSlotParticipant.ITEM_KEY_SERIALIZER.read(tag, "items");
	}

	@Override
	public void tick(BlockEntity entity, Direction face) {
		super.tick(entity, face);
	}

	@Override
	public void write(NBTagView.Builder tag) {
		super.write(tag);
		ItemSlotParticipant.ITEM_KEY_SERIALIZER.save(tag, "items", this.participant);
	}

	@Override
	public Entry getEntry() {
		return DPorts.ITEM_INPUT;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public SpriteIdentifier getTexture() {
		return DSprites.ITEM_INPUT;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public SpriteIdentifier getBarTexture() {
		return DSprites.SMALL_BAR;
	}
}
