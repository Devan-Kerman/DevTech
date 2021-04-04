package io.github.devtech.api.registry;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import io.github.devtech.api.datagen.item.BlockItemGenerator;
import io.github.devtech.api.datagen.item.StandardItemModelGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.recipe.JIngredient;
import net.devtech.arrp.json.recipe.JIngredients;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JResult;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface DItems {
	Item WOOD_ASH = register(item(null), "wood_ash", StandardItemModelGenerator::new);
	Item CLOTH_WINDMILL = register(item(null), "cloth_windmill", StandardItemModelGenerator::new);
	Item TIN_ORE = ofBlock(DBlocks.TIN_ORE);
	Item TIN_INGOT = register(item(null), "tin_ingot", StandardItemModelGenerator::new);
	Item COPPER_ORE = ofBlock(DBlocks.COPPER_ORE);
	Item COPPER_INGOT = register(item(null), "copper_ingot", StandardItemModelGenerator::new);
	Item BRONZE_INGOT = register(item(null), "bronze_ingot", StandardItemModelGenerator::new);
	Item BRONZE_NUGGET = register(item(null), "bronze_nugget", StandardItemModelGenerator::new);

	// static initializer at home
	static void loadResources(RuntimeResourcePack pack, JLang en_us) {
		en_us.item(WOOD_ASH, "Wood Ash");
		en_us.item(CLOTH_WINDMILL, "Cloth Wind Mill");
		en_us.item(TIN_INGOT, "Tin Ingot");
		en_us.item(COPPER_INGOT, "Copper Ingot");
		en_us.item(BRONZE_INGOT, "Bronze Ingot");
		en_us.item(BRONZE_NUGGET, "Bronze Nugget");
		pack.addRecipe(Devtech.id("bronze_nuggets"), JRecipe.shapeless(JIngredients.ingredients().add(JIngredient.ingredient().item(BRONZE_INGOT)),
				JResult.itemStack(BRONZE_NUGGET, 9)));
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

	static BlockItem ofBlock(Block block) {
		Identifier identifier = Registry.BLOCK.getId(block);
		BlockItem item = Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings()));
		Devtech.GENERATORS.add(new BlockItemGenerator(identifier));
		return item;
	}
}
