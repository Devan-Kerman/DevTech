package io.github.devtech.base.gui;

import com.google.common.collect.Iterators;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.devtech.Devtech;
import io.github.devtech.api.registry.DTextures;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ABar extends ADrawable implements Interactable {
	public static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Devtech.id2(""), ABar::new);
	public final SyncedProperty<Float> percentage = this.createClientSyncedProperty(NBTType.FLOAT, 0f);
	/**
	 * the 'minimum required' "juice" for this recipe, -1 for none
	 */
	public final SyncedProperty<Float> minimumRequired = this.createClientSyncedProperty(NBTType.FLOAT, -1f);
	public final float width, height;
	public final Text fill, pin, red;
	@Environment (EnvType.CLIENT) public boolean hoverFirst, hoverNext, hoverPin;

	public ABar(float width, float height, Text fill, Text pin, Text excess) {
		this(ENTRY, width, height, fill, pin, excess);
	}

	protected ABar(DrawableRegistry.@Nullable Entry id, float width, float height, Text fill, @Nullable Text pin, @Nullable Text excess) {
		super(id);
		this.width = width;
		this.height = height;
		this.setBounds(Polygon.rectangle(width, height));
		this.fill = fill;
		this.pin = pin;
		this.red = excess;
	}

	protected ABar(DrawableRegistry.Entry entry, NBTagView view) {
		super(entry);
		this.width = view.getFloat("width");
		this.height = view.getFloat("height");
		this.fill = Text.Serializer.fromJson(view.getString("fill"));
		if (Iterators.contains(view.iterator(), "pin")) {
			this.pin = Text.Serializer.fromJson(view.getString("pin"));
		} else this.pin = null;
		if (Iterators.contains(view.iterator(), "red")) {
			this.red = Text.Serializer.fromJson(view.getString("red"));
		} else this.red = null;
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

		float progress = this.percentage.get(), min = this.minimumRequired.get();
		if (min != -1) {
			float minProgress = progress - min;
			if (minProgress > 0) {
				progress = min;
				try (Close ignored = graphics.applyTransformation(Transformation.translate(progress * this.width, 0, 0))) {
					graphics.drawSprite(DTextures.BAR.cutout(0, 0, this.width / 256, this.height / 8).cutout(progress, 0, minProgress, 1));
					graphics.fillRect(minProgress * this.width, this.height, 0xaaff9999);
					if (this.hoverNext) {
						graphics.fillRect(minProgress * this.width, this.height, 0xaaffffff);
						if(this.red != null) {
							graphics.drawOrderedTooltip(renderer.wrapLines(this.red, (int) this.width));
						}
					}
				}
			}
		}

		graphics.drawSprite(DTextures.BAR.cutout(0, 0, this.width / 256, this.height / 8).cutout(0, 0, progress, 1));
		if (this.hoverFirst) {
			graphics.fillRect(progress * this.width, this.height, 0xaaffffff);
			if(this.fill != null) {
				graphics.drawOrderedTooltip(renderer.wrapLines(this.fill, (int) this.width));
			}
		}

		if (min != -1) {
			try (Close ignored = graphics.applyTransformation(Transformation.translate(min * this.width - 1.5f, 0, 0))) {
				graphics.drawSprite(DTextures.PIN);
				if (this.hoverPin) {
					graphics.fillRect(3, 8, 0xaaffffff);
					if(this.pin != null) {
						graphics.drawOrderedTooltip(renderer.wrapLines(this.pin, (int) this.width));
					}
				}
			}
		}
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		output.putFloat("width", this.width);
		output.putFloat("height", this.height);
		output.putString("fill", Text.Serializer.toJson(this.fill));
		if (this.pin != null) {
			output.putString("pin", Text.Serializer.toJson(this.pin));
		}
		if (this.pin != null) {
			output.putString("red", Text.Serializer.toJson(this.red));
		}
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	@Override
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		this.hoverFirst = false;
		this.hoverNext = false;
		this.hoverPin = false;
		float pinLocation = this.width * this.minimumRequired.get();
		if (pinLocation > 0 && Math.abs(pinLocation - mouseX) < 1.5f) {
			this.hoverPin = true;
		} else if (mouseX < pinLocation) {
			this.hoverFirst = true;
		} else {
			this.hoverNext = true;
		}
	}

	@Override
	public void onLoseHover(RootContainer container) {
		this.hoverFirst = false;
		this.hoverNext = false;
		this.hoverPin = false;
	}
}
