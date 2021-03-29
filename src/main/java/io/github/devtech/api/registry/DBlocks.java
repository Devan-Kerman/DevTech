package io.github.devtech.api.registry;

import java.util.function.Function;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface DBlocks {
	static <T extends Block> T register(T val, String id, Function<Identifier, ResourceGenerator> func) {
		Identifier identifier = Devtech.id(id);
		T i = Registry.register(Registry.BLOCK, identifier, val);
		if (func != null) {
			Devtech.GENERATORS.add(func.apply(identifier));
		}
		return i;
	}

	static void init() {}
}
