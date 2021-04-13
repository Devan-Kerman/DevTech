package io.github.devtech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;

@Mixin (LivingEntity.class)
public interface LivingEntityAccess {
	@Accessor
	boolean isJumping();
}
