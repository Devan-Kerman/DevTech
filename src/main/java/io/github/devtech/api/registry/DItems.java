package io.github.devtech.api.registry;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import io.github.devtech.api.datagen.item.StandardItemModelGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.lang.JLang;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface DItems {
	Item WOOD_ASH = register(item(null), "wood_ash", StandardItemModelGenerator::new);

	// static initializer at home
	static void loadResources(RuntimeResourcePack pack, JLang americanEnglish) {
		americanEnglish.item(WOOD_ASH, "Wood Ash");
		pack.addBlockState(JState.state(JState.variant()
				                                .put("facing=down", JState.model("devtech:block/test"))
				                                .put("facing=north", JState.model("devtech:block/test"))
				                                .put("facing=south", JState.model("devtech:block/test"))
				                                .put("facing=west", JState.model("devtech:block/test"))
				                                .put("facing=east", JState.model("devtech:block/test"))
				                                .put("facing=up", JState.model("devtech:block/test"))), Devtech.id("test"));
	}

	static void init() {}

	static <T extends Item> T register(T item, String name, Function<Identifier, ResourceGenerator> generator) {
		Identifier identifier = Devtech.id(name);
		T i = Registry.register(Registry.ITEM, identifier, item);
		Devtech.GENERATORS.add(generator.apply(identifier));
		return i;
	}

	static BlockItem block(Block block) {
		return new BlockItem(block, new Item.Settings());
	}

	static Item item(@Nullable Consumer<Item.Settings> consumer) {
		Item.Settings settings = new Item.Settings();
		if (consumer != null) {
			consumer.accept(settings);
		}
		return new Item(settings);
	}
}
