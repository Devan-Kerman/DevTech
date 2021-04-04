package io.github.devtech.api.registry;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tag.TagRegistry;

public interface DTags {
	Tag<Item> COPPER_INGOTS = TagRegistry.item(new Identifier("c:copper_ingots"));
	Tag<Item> TIN_INGOTS = TagRegistry.item(new Identifier("c:tin_ingots"));
	Tag<Item> BRONZE_INGOTS = TagRegistry.item(new Identifier("c:bronze_ingots"));

	static void init() {}
}
