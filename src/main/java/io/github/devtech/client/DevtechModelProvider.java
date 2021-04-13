package io.github.devtech.client;

import java.util.HashMap;
import java.util.Map;

import io.github.devtech.Devtech;
import io.github.devtech.client.model.AbstractBakedModel;
import io.github.devtech.client.model.AbstractMachineBakedModel;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;

public class DevtechModelProvider implements ModelResourceProvider {
	public static final Map<Identifier, AbstractBakedModel> MODELS = new HashMap<>();
	private final ResourceManager manager;

	public DevtechModelProvider(ResourceManager manager) {this.manager = manager;}

	@Override
	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) {
		return MODELS.get(resourceId);
	}

	public static void add(AbstractBakedModel model, String...names) {
		for (String name : names) {
			MODELS.put(Devtech.id(name), model);
		}
	}
}
