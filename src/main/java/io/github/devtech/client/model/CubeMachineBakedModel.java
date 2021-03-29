package io.github.devtech.client.model;

import java.util.function.Function;

import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

public class CubeMachineBakedModel extends AbstractMachineBakedModel {
	public final CubeData data;

	public CubeMachineBakedModel(SpriteIdentifier particles, CubeData data) {
		super(particles);
		this.data = data;
		this.textureDependencies.addAll(data.identifiers.values());
	}

	@Override
	protected void build(Renderer renderer,
			QuadEmitter emitter,
			ModelLoader loader,
			Function<SpriteIdentifier, Sprite> textureGetter,
			ModelBakeSettings rotationContainer,
			Identifier modelId) {
		AbstractBakedModel.buildCube(rotationContainer, emitter, textureGetter, this.data);
	}
}
