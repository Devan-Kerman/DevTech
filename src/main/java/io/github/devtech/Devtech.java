package io.github.devtech;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.util.v0.api.Id;
import io.github.devtech.api.registry.DLang;
import io.github.devtech.api.registry.DMachines;
import io.github.devtech.api.registry.DPorts;
import io.github.devtech.api.registry.DBlocks;
import io.github.devtech.api.registry.DItems;
import io.github.devtech.api.registry.DTiles;
import io.github.devtech.api.datagen.ResourceGenerator;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Devtech implements ModInitializer {
	public static final Direction[] DIRECTIONS = Direction.values();
	public static final List<ResourceGenerator> GENERATORS = new ArrayList<>();
	public static final FabricLoader FLOADER = FabricLoader.getInstance();
	public static final boolean IS_CLIENT = FLOADER.getEnvironmentType() == EnvType.CLIENT;
	public static final boolean IS_SERVER = FLOADER.getEnvironmentType() == EnvType.SERVER;
	public static final boolean IS_DEV = FLOADER.isDevelopmentEnvironment();

	public static final String MODID = "devtech";

	private static RuntimeResourcePack resourcePack;

	@Override
	public void onInitialize() {
		DItems.init();
		DBlocks.init();
		DTiles.init();
		DPorts.init();
		DMachines.init();

		RRPCallback.EVENT.register(resources -> {
			if(IS_DEV || resourcePack == null) {
				resourcePack = RuntimeResourcePack.create(MODID+":runtime_resource_pack");
				JLang americanEnglish = new JLang();

				DItems.loadResources(resourcePack, americanEnglish);
				americanEnglish.getLang().putAll(DLang.EN_US);

				resourcePack.addLang(id("en_us"), americanEnglish);
				for (ResourceGenerator gen : GENERATORS) {
					gen.generate(resourcePack);
				}
			}
			resources.add(0, resourcePack);
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}

	public static Id id2(String path) {
		return Id.create(MODID, path);
	}

}
