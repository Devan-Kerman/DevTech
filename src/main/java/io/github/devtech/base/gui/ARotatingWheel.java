package io.github.devtech.base.gui;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.rendering.v0.api.Graphics2d;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.devtech.Devtech;
import io.github.devtech.api.registry.DLang;
import io.github.devtech.api.registry.DTextures;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class ARotatingWheel extends ADrawable implements Interactable, Tickable {
	private static final DrawableRegistry.Entry AROTATING_WHEEL = DrawableRegistry.registerForward(Devtech.id2("rot_whel"), ARotatingWheel::new);
	public final SyncedProperty<Float> rotationsPerTick = this.createClientSyncedProperty(NBTType.FLOAT, 0f);
	@Environment(EnvType.CLIENT)
	private float rotation;
	@Environment(EnvType.CLIENT)
	private boolean hover;
	public ARotatingWheel() {
		super(AROTATING_WHEEL);
		this.setBounds(Polygon.rectangle(8, 8));
	}

	public ARotatingWheel(DrawableRegistry.Entry entry, NBTagView view) {
		super(entry);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		try(Close ignored3 = graphics.applyTransformation(Transformation.translate(4, 4, 0))) {
			try(Close ignored = graphics.applyTransformation(Transformation.rotate(0, 0, this.rotation))) {
				try(Close ignored2 = graphics.applyTransformation(Transformation.translate(-4, -4, 0))) {
					graphics.drawSprite(DTextures.WHEEL_SMALL);
				}
			}
		}
		if(this.hover) {
			graphics.fillRect(8, 8, 0xaaffffff);
			graphics.drawOrderedTooltip(Graphics2d.wrap(DLang.rpt(this.rotationsPerTick.get()), 150));
		}
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {}

	@Override
	public void tick(RootContainer container) {
		this.rotation += this.rotationsPerTick.get() * 360;
		this.rotation %= 360;
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	@Override
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		this.hover = true;
	}

	@Override
	public void onLoseHover(RootContainer container) {
		this.hover = false;
	}
}
