package io.github.devtech.client.model;


import java.util.Random;
import java.util.function.Supplier;

import io.github.devtech.Devtech;
import io.github.devtech.api.access.PortAccess;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.registry.DSprites;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

public abstract class AbstractMachineBakedModel extends AbstractBakedModel {
	public AbstractMachineBakedModel(SpriteIdentifier particles) {
		super(particles);
		this.textureDependencies.addAll(DSprites.PORT_SPRITES);
	}

	@Override
	public void emitBlockQuads(Mesh mesh, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		super.emitBlockQuads(mesh, blockView, state, pos, randomSupplier, context);
		BlockEntity block = blockView.getBlockEntity(pos);
		if(block instanceof PortAccess) {
			PortAccess machine = ((PortAccess) block);
			for (Direction direction : Devtech.DIRECTIONS) {
				Port port = machine.getPortAbsolute(direction);
				if(port != null) {
					QuadEmitter emitter = context.getEmitter();
					emitter.square(direction, 0, 0, 1f, 1f, 0);
					emitter.material(TRANSPARENT.get());
					emitter.spriteBake(0, RESOLVED.get(port.getEntry().texture), MutableQuadView.BAKE_LOCK_UV);
					emitter.spriteColor(0, -1, -1, -1, -1);
					emitter.emit();
					PortColor color = port.getColor();
					if(color != PortColor.NONE) {
						emitter.square(direction, 0, 0, 1f, 1f, 0);
						emitter.material(TRANSPARENT.get());
						emitter.spriteBake(0, RESOLVED.get(port.getEntry().bar), MutableQuadView.BAKE_LOCK_UV);
						emitter.spriteColor(0, color.color, color.color, color.color, color.color);
						emitter.emit();
					}
				}
			}
		}
	}
}
