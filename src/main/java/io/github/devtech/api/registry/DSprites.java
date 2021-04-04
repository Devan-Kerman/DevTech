package io.github.devtech.api.registry;

import java.util.HashSet;
import java.util.Set;

import io.github.devtech.Devtech;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface DSprites {
	SpriteIdentifier WOODEN_CHASSIS = of("block/tier1/wooden_chassis");
	SpriteIdentifier FURNACE_CHASSIS = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/furnace_side"));

	static SpriteIdentifier of(String id) {
		return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Devtech.id(id));
	}

	Set<SpriteIdentifier> PORT_SPRITES = new HashSet<>();
	SpriteIdentifier BIG_BAR = add("block/ports/big_bar");
	SpriteIdentifier FLUID_FULL = add("block/ports/fluid_full");
	SpriteIdentifier FLUID_INPUT = add("block/ports/fluid_input");
	SpriteIdentifier FLUID_OUTPUT = add("block/ports/fluid_output");
	SpriteIdentifier GAS_FULL = add("block/ports/gas_full");
	SpriteIdentifier GAS_INPUT = add("block/ports/gas_input");
	SpriteIdentifier GAS_OUTPUT = add("block/ports/gas_output");
	SpriteIdentifier ITEM_INPUT = add("block/ports/item_input");
	SpriteIdentifier ITEM_OUTPUT = add("block/ports/item_output");
	SpriteIdentifier SMALL_BAR = add("block/ports/small_bar");
	SpriteIdentifier THERMAL_COLD = add("block/ports/thermal_cold");
	SpriteIdentifier THERMAL_HOT = add("block/ports/thermal_hot");
	SpriteIdentifier THERMAL_INPUT = add("block/ports/thermal_input");
	SpriteIdentifier THERMAL_OUTPUT = add("block/ports/thermal_output");
	SpriteIdentifier ROTATIONAL_INPUT = add("block/ports/rotational_input");
	SpriteIdentifier ROTATIONAL_OUTPUT = add("block/ports/rotational_output");

	static SpriteIdentifier add(String id) {
		SpriteIdentifier identifier = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Devtech.id(id));
		PORT_SPRITES.add(identifier);
		return identifier;
	}
}
