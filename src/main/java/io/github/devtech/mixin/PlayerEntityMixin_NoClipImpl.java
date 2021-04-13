package io.github.devtech.mixin;

import io.github.devtech.base.item.GhostItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin_NoClipImpl extends LivingEntity {
	@Override
	@Shadow public abstract boolean isSpectator();

	protected PlayerEntityMixin_NoClipImpl(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	private boolean wasSpectator;

	@Redirect (method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z", ordinal = 0))
	public boolean reset(PlayerEntity entity) {
		if(this.world.isClient) {
			boolean isSpectator = this.isSpectator();
			// if spectatorship didn't change, respect old noClip option
			if (this.wasSpectator == isSpectator) {
				return this.noClip;
			}
			this.wasSpectator = isSpectator;
			return isSpectator;
		} else if(GhostItem.GHOST_ITEM.grants((PlayerEntity) (Object) this, GhostItem.NO_CLIP)) {
			return true;
		} else {
			return this.isSpectator();
		}
	}
}
