package io.github.devtech.base.client.model;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.client.model.AbstractBakedModel;

import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

public class PortItemModel extends AbstractBakedModel {
	private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:item/generated");

	public PortItemModel(SpriteIdentifier particles) {
		super(particles);
		for (Port.Entry entry : Port.REGISTRY) {
			this.textureDependencies.add(entry.bar);
			this.textureDependencies.add(entry.texture);
		}
	}

	@Override
	public void emitItemQuads(Mesh mesh, ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		Port port = Port.SERIALIZER.read(FabricViews.view(stack.getTag()));
		if (port == null) {
			QuadEmitter emitter = context.getEmitter();
			emitter.square(Direction.SOUTH, 0, 0, 1f, 1f, 0);
			emitter.spriteColor(0, -1, -1, -1, -1);
			emitter.emit();
			return;
		}

		QuadEmitter emitter = context.getEmitter();
		emitter.square(Direction.SOUTH, 0, 0, 1f, 1f, 0);
		emitter.spriteBake(0, RESOLVED.get(port.getEntry().texture), MutableQuadView.BAKE_LOCK_UV);
		emitter.spriteColor(0, -1, -1, -1, -1);
		emitter.emit();


		PortColor color = port.getColor();
		if (color != PortColor.NONE) {
			emitter.square(Direction.SOUTH, 0, 0, 1f, 1f, 0);
			emitter.spriteBake(0, RESOLVED.get(port.getEntry().bar), MutableQuadView.BAKE_LOCK_UV);
			emitter.spriteColor(0, color.color, color.color, color.color, color.color);
			emitter.emit();
		}
	}

	@Override
	protected Identifier getItemTransformationParentId() {
		return DEFAULT_BLOCK_MODEL;
	}

	@Override
	protected boolean build(Renderer renderer,
			QuadEmitter emitter,
			ModelLoader loader,
			Function<SpriteIdentifier, Sprite> textureGetter,
			ModelBakeSettings rotationContainer,
			Identifier modelId) {
		return false;
	}
}
