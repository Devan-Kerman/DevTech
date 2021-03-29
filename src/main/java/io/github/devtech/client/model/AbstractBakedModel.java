package io.github.devtech.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import io.github.devtech.Devtech;
import io.github.devtech.api.util.Lazy;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

public abstract class AbstractBakedModel implements UnbakedModel {
	public static final Lazy<Renderer> RENDERER = Lazy.of(RendererAccess.INSTANCE::getRenderer);
	protected static final Map<SpriteIdentifier, Sprite> RESOLVED = new ConcurrentHashMap<>();
	private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");
	protected final Set<SpriteIdentifier> textureDependencies = new HashSet<>();
	protected final SpriteIdentifier particles;
	private ModelTransformation itemTransformation;

	protected AbstractBakedModel(SpriteIdentifier particles) {
		this.particles = particles;
		this.textureDependencies.add(particles);
	}


	protected abstract void build(Renderer renderer, QuadEmitter emitter, ModelLoader loader,
			Function<SpriteIdentifier, Sprite> textureGetter,
			ModelBakeSettings rotationContainer,
			Identifier modelId);

	@Nullable
	@Override
	public BakedModel bake(ModelLoader loader,
			Function<SpriteIdentifier, Sprite> textureGetter,
			ModelBakeSettings rotationContainer,
			Identifier modelId) {
		JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(DEFAULT_BLOCK_MODEL);
		this.itemTransformation = defaultBlockModel.getTransformations();
		for (SpriteIdentifier s : this.textureDependencies) {
			RESOLVED.computeIfAbsent(s, textureGetter);
		}
		MeshBuilder builder = RENDERER.get().meshBuilder();
		this.build(RENDERER.get(), builder.getEmitter(), loader, textureGetter, rotationContainer, modelId);
		Mesh mesh = builder.build();
		return new Baked(mesh);
	}

	public void emitBlockQuads(Mesh mesh, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		context.meshConsumer().accept(mesh);
	}

	public void emitItemQuads(Mesh mesh, ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		context.meshConsumer().accept(mesh);
	}

	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return null;
	}

	public boolean useAmbientOcclusion() {
		return true;
	}
	public boolean hasDepth() {
		return false;
	}
	public boolean isSideLit() {
		return true;
	}
	public boolean isBuiltin() {
		return false;
	}
	public Sprite getParticleSprite() {
		return RESOLVED.get(this.particles);
	}
	public ModelTransformation getItemTransformation() {
		return this.itemTransformation;
	}
	public ModelOverrideList getOverrides() {
		return ModelOverrideList.EMPTY;
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.singleton(DEFAULT_BLOCK_MODEL);
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter,
			Set<Pair<String, String>> unresolvedTextureReferences) {
		return this.textureDependencies;
	}

	public static void buildCube(ModelBakeSettings rotations, QuadEmitter emitter, Function<SpriteIdentifier, Sprite> textureGetter, CubeData data) {
		for(Direction direction : Devtech.DIRECTIONS) {
			Direction transformed = Direction.transform(rotations.getRotation().getMatrix(), direction);
			emitter.square(transformed, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
			emitter.spriteBake(0, RESOLVED.computeIfAbsent(data.identifiers.get(direction), textureGetter), MutableQuadView.BAKE_LOCK_UV);
			emitter.spriteColor(0, -1, -1, -1, -1);
			emitter.emit();
		}
	}

	public final class Baked implements BakedModel, FabricBakedModel {
		private final Mesh mesh;

		public Baked(Mesh mesh) {
			this.mesh = mesh;
		}

		@Override
		public boolean isVanillaAdapter() {
			return false;
		}

		@Override
		public void emitBlockQuads(BlockRenderView blockView,
				BlockState state,
				BlockPos pos,
				Supplier<Random> randomSupplier,
				RenderContext context) {
			AbstractBakedModel.this.emitBlockQuads(this.mesh, blockView, state, pos, randomSupplier, context);
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
			AbstractBakedModel.this.emitItemQuads(this.mesh, stack, randomSupplier, context);
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
			return Collections.emptyList();
		}

		@Override
		public boolean useAmbientOcclusion() {
			return AbstractBakedModel.this.useAmbientOcclusion();
		}

		@Override
		public boolean hasDepth() {
			return AbstractBakedModel.this.hasDepth();
		}

		@Override
		public boolean isSideLit() {
			return AbstractBakedModel.this.isSideLit();
		}

		@Override
		public boolean isBuiltin() {
			return AbstractBakedModel.this.isBuiltin();
		}

		@Override
		public Sprite getSprite() {
			return AbstractBakedModel.this.getParticleSprite();
		}

		@Override
		public ModelTransformation getTransformation() {
			return AbstractBakedModel.this.getItemTransformation();
		}

		@Override
		public ModelOverrideList getOverrides() {
			return AbstractBakedModel.this.getOverrides();
		}
	}
}
