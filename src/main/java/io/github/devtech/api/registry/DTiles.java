package io.github.devtech.api.registry;

import java.util.function.Supplier;

import io.github.devtech.Devtech;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public interface DTiles {
	static <E extends BlockEntity> BlockEntityType<E> register(Supplier<E> creator, String id, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, Devtech.id(id), BlockEntityType.Builder.create(creator, blocks).build(null));
	}

	static void init() {
	}
}
