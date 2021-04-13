package io.github.devtech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;

@Mixin(Entity.class)
public class EntityMixin_NoClipImpl {
	@Shadow public boolean noClip;

	@Inject(method = "wouldPoseNotCollide", at = @At("HEAD"), cancellable = true)
	public void wouldPoseNotCollide(EntityPose pose, CallbackInfoReturnable<Boolean> cir) {
		if(this.noClip) {
			cir.setReturnValue(true);
		}
	}

}
