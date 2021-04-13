package io.github.devtech.api.registry;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import io.github.devtech.api.datagen.item.BlockItemGenerator;
import io.github.devtech.api.datagen.item.StandardItemModelGenerator;
import io.github.devtech.api.port.PortItem;
import io.github.devtech.base.item.AirJumpItem;
import io.github.devtech.base.item.GhostItem;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.recipe.JIngredient;
import net.devtech.arrp.json.recipe.JIngredients;
import net.devtech.arrp.json.recipe.JKeys;
import net.devtech.arrp.json.recipe.JPattern;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JResult;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface DItems {
	Item WOOD_ASH = register(item(null), "wood_ash", StandardItemModelGenerator::new);
	Item CLOTH_WINDMILL = register(item(null), "cloth_windmill", StandardItemModelGenerator::new);
	Item TIN_ORE = ofBlock(DBlocks.TIN_ORE, BlockItemGenerator::new);
	Item TIN_INGOT = register(item(null), "tin_ingot", StandardItemModelGenerator::new);
	Item COPPER_ORE = ofBlock(DBlocks.COPPER_ORE, BlockItemGenerator::new);
	Item COPPER_INGOT = register(item(null), "copper_ingot", StandardItemModelGenerator::new);
	Item BRONZE_INGOT = register(item(null), "bronze_ingot", StandardItemModelGenerator::new);
	Item BRONZE_NUGGET = register(item(null), "bronze_nugget", StandardItemModelGenerator::new);
	Item SPECULUM_INGOT = register(item(null), "speculum_ingot", StandardItemModelGenerator::new);
	Item MIRROR = register(item(null), "mirror", StandardItemModelGenerator::new);
	Item SOLAR_COOKER = ofBlock(DBlocks.SOLAR_COOKER, StandardItemModelGenerator::new);
	Item PORT = register(new PortItem(), "port");
	Item GHOST_ITEM = register(new GhostItem(), "ghost_item");
	Item AIR_JUMP_ITEM = register(new AirJumpItem(), "air_jump_item", StandardItemModelGenerator::new);

	// static initializer at home
	static void loadResources(RuntimeResourcePack pack, JLang en_us) {
		en_us.item(WOOD_ASH, "Wood Ash");
		en_us.item(CLOTH_WINDMILL, "Cloth Wind Mill");
		en_us.item(TIN_INGOT, "Tin Ingot");
		en_us.item(COPPER_INGOT, "Copper Ingot");
		en_us.item(BRONZE_INGOT, "Bronze Ingot");
		en_us.item(BRONZE_NUGGET, "Bronze Nugget");
		en_us.item(SPECULUM_INGOT, "Speculum Ingot");
		en_us.item(MIRROR, "Mirror");
		en_us.item(GHOST_ITEM, "Eldrich Phantom Core");
		en_us.item(AIR_JUMP_ITEM, "Mass Of Evaporated Water In A bottle");
		pack.addRecipe(Devtech.id("mirror_speculum"), JRecipe.shapeless(JIngredients.ingredients()
				                                                                .add(JIngredient.ingredient().item(SPECULUM_INGOT))
				                                                                .add(JIngredient.ingredient().item(SPECULUM_INGOT))
				                                                                .add(JIngredient.ingredient().item(Items.GLASS_PANE))
				                                                                .add(JIngredient.ingredient().item(Items.SAND)),
				JResult.item(MIRROR)));
		pack.addRecipe(Devtech.id("mirror_bronze"), JRecipe.shapeless(JIngredients.ingredients()
				                                                              .add(JIngredient.ingredient().item(SPECULUM_INGOT))
				                                                              .add(JIngredient.ingredient().item(BRONZE_INGOT))
				                                                              .add(JIngredient.ingredient().item(Items.GLASS_PANE))
				                                                              .add(JIngredient.ingredient().item(Items.SAND)),
				JResult.item(MIRROR)));
		pack.addRecipe(Devtech.id("mirror_iron"), JRecipe.shapeless(JIngredients.ingredients()
				                                                            .add(JIngredient.ingredient().item(SPECULUM_INGOT))
				                                                            .add(JIngredient.ingredient().item(Items.IRON_INGOT))
				                                                            .add(JIngredient.ingredient().item(Items.GLASS_PANE))
				                                                            .add(JIngredient.ingredient().item(Items.SAND)),
				JResult.itemStack(MIRROR, 3)));
		pack.addRecipe(Devtech.id("solar_cooker"), JRecipe.shaped(JPattern.pattern("NMN", "MFM", "BMB"),
				JKeys.keys()
						.key("N", JIngredient.ingredient().item(BRONZE_NUGGET))
						.key("M", JIngredient.ingredient().item(MIRROR))
						.key("F", JIngredient.ingredient().item(Items.FURNACE_MINECART))
						.key("B", JIngredient.ingredient().item(BRONZE_INGOT)),
				JResult.item(SOLAR_COOKER)));
		pack.addRecipe(Devtech.id("bronze_nuggets"),
				JRecipe.shapeless(JIngredients.ingredients().add(JIngredient.ingredient().item(BRONZE_INGOT)), JResult.itemStack(BRONZE_NUGGET, 9)));
	}

	static void init() {}

	static <T extends Item> T register(T item, String name, Function<Identifier, ResourceGenerator>...generator) {
		Identifier identifier = Devtech.id(name);
		T i = Registry.register(Registry.ITEM, identifier, item);
		for (Function<Identifier, ResourceGenerator> function : generator) {
			Devtech.GENERATORS.add(function.apply(identifier));
		}
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

	static BlockItem ofBlock(Block block, Function<Identifier, ResourceGenerator>...generators) {
		Identifier identifier = Registry.BLOCK.getId(block);
		BlockItem item = Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings()));
		for (Function<Identifier, ResourceGenerator> generator : generators) {
			Devtech.GENERATORS.add(generator.apply(identifier));
		}
		return item;
	}
}
