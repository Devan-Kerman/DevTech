package io.github.devtech.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.Block;
import net.minecraft.item.ShovelItem;

@Mixin (ShovelItem.class)
public interface ShovelItemAccess {
	@Accessor
	static Set<Block> getEFFECTIVE_BLOCKS() { throw new UnsupportedOperationException(); }
}
