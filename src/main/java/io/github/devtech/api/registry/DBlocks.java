package io.github.devtech.api.registry;

import java.util.function.Function;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import io.github.devtech.api.datagen.block.HorizontalBlockStateGenerator;
import io.github.devtech.api.datagen.block.NormalBlockGenerator;
import io.github.devtech.api.datagen.loot.NormalBlockLootTable;
import io.github.devtech.base.SolarCookerBlock;
import io.github.devtech.mixin.ShovelItemAccess;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.recipe.JIngredient;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JResult;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface DBlocks {
	Block TIN_ORE = register(new Block(AbstractBlock.Settings.copy(Blocks.IRON_ORE)), "tin_ore", NormalBlockGenerator::new, NormalBlockLootTable::new);
	Block COPPER_ORE = register(new Block(AbstractBlock.Settings.copy(Blocks.CLAY).strength(0.6F)), "copper_ore", NormalBlockGenerator::new, NormalBlockLootTable::new);
	Block SOLAR_COOKER = register(new SolarCookerBlock(), "solar_cooker", HorizontalBlockStateGenerator::new, NormalBlockLootTable::new);


	@SafeVarargs
	static <T extends Block> T register(T val, String id, Function<Identifier, ResourceGenerator>...funcs) {
		Identifier identifier = Devtech.id(id);
		T i = Registry.register(Registry.BLOCK, identifier, val);
		for (Function<Identifier, ResourceGenerator> func : funcs) {
			Devtech.GENERATORS.add(func.apply(identifier));
		}
		return i;
	}

	static void loadResources(RuntimeResourcePack pack, JLang en_us) {
		en_us.block(TIN_ORE, "Tin Ore");
		en_us.block(COPPER_ORE, "Copper Ore");
		en_us.block(SOLAR_COOKER, "Solar Cooker");
		pack.addRecipe(Devtech.id("tin_ore_smelt"), JRecipe.smelting(JIngredient.ingredient().item(DItems.TIN_ORE), JResult.item(DItems.TIN_INGOT)));
		pack.addRecipe(Devtech.id("copper_ore_smelt"), JRecipe.smelting(JIngredient.ingredient().item(DItems.COPPER_ORE), JResult.item(DItems.COPPER_INGOT)));
	}

	static void init() {
		ShovelItemAccess.getEFFECTIVE_BLOCKS().add(COPPER_ORE);
	}
}
