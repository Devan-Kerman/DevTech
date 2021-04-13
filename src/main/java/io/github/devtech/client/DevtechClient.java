package io.github.devtech.client;

import io.github.devtech.Devtech;
import io.github.devtech.api.registry.DBlocks;
import io.github.devtech.api.registry.DItems;
import io.github.devtech.api.registry.DSprites;
import io.github.devtech.base.client.model.PortItemModel;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

@Environment (EnvType.CLIENT)
public class DevtechClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(DevtechModelProvider::new);
		BlockRenderLayerMap.INSTANCE.putBlock(DBlocks.SOLAR_COOKER, RenderLayer.getCutoutMipped());
		DevtechModelProvider.add(
				new PortItemModel(DSprites.FURNACE_CHASSIS),
				"item/port");
		FabricModelPredicateProviderRegistry.register(DItems.GHOST_ITEM, Devtech.id("active"), (itemStack, clientWorld, livingEntity) -> {
			if (livingEntity == null) {
				return 0;
			}
			CompoundTag tag = itemStack.getTag();
			return tag == null ? 0 : (tag.getBoolean("active") ? 1 : 0);
		});
	}
}
