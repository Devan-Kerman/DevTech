package io.github.devtech.base.gui;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ABlockEntityInventorySlot;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.devtech.api.DevtechMachine;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.registry.DDrawables;

import net.minecraft.inventory.Inventory;

public class APortSlot<B extends DevtechMachine.DefaultBlockEntity & Inventory> extends ABlockEntityInventorySlot<B> {
	public final PortColor color;

	public APortSlot(B entity, int index, PortColor color) {
		this(DDrawables.PORT_SLOT, entity, index, color);
	}

	protected APortSlot(DrawableRegistry.Entry id, B entity, int index, PortColor color) {
		super(id, entity, index);
		this.color = color;
	}

	public APortSlot(DrawableRegistry.Entry id, NBTagView input) {
		super(id, input);
		this.color = PortColor.forName(input.getString("color"));
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		super.write0(container, output);
		output.putString("color", this.color.name());
	}

	@Override
	protected void renderBackground(Graphics3d graphics, float tickDelta) {
		super.renderBackground(graphics, tickDelta);
		if(this.color != PortColor.NONE) {
			int color = this.color.color & 0x44ffffff;
			graphics.fillRect(18, 1, color);
			graphics.fillRect(0, 1, 1, 17, color);
		}
	}
}
