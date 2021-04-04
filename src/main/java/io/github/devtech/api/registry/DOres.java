package io.github.devtech.api.registry;

import io.github.devtech.Devtech;

import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;

public interface DOres {
	RuleTest ANDESITE = new BlockMatchRuleTest(Blocks.ANDESITE);
	RuleTest CLAY = new BlockMatchRuleTest(Blocks.CLAY);
	ConfiguredFeature<?, ?> TIN_ORE = Feature.ORE.configure(new OreFeatureConfig(ANDESITE, DBlocks.TIN_ORE.getDefaultState(), 9))
			                                  .decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(30, 0, 64))).spreadHorizontally()
			                                  .repeat(20);
	ConfiguredFeature<?, ?> COPPER_ORE = Feature.ORE.configure(new OreFeatureConfig(CLAY, DBlocks.COPPER_ORE.getDefaultState(), 9))
			                               .decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(30, 0, 64))).spreadHorizontally()
			                               .repeat(20);

	static void init() {
		registerOw(TIN_ORE, "tin_ore_andesite");
		registerOw(COPPER_ORE, "copper_ore_clay");
	}

	static void registerOw(ConfiguredFeature<?, ?> feature, String id) {
		RegistryKey<ConfiguredFeature<?, ?>> tinOre = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, Devtech.id(id));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, tinOre.getValue(), feature);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, tinOre);
	}
}
