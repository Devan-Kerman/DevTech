package io.github.devtech.api.registry;

import java.util.function.Function;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.devtech.Devtech;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.base.InputItemPort;
import io.github.devtech.api.port.base.OutputItemPort;
import io.github.devtech.api.port.base.RotationalInputPort;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.registry.Registry;

public interface DPorts {
	Port.Entry ROTATIONAL_INPUT = register("rotational_input", RotationalInputPort::new, false);
	Port.Entry ITEM_INPUT = register("item_input", InputItemPort::new, false);
	Port.Entry ITEM_OUTPUT = register("item_output", OutputItemPort::new, false);

	static Port.Entry register(String name, Function<NBTagView, Port> create, boolean isBig) {
		Port.Entry entry = new Port.Entry(create);
		if(Devtech.IS_CLIENT) {
			entry.bar = isBig ? DSprites.BIG_BAR : DSprites.SMALL_BAR;
			entry.texture = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Devtech.id("block/ports/"+name));
		}
		Registry.register(Port.REGISTRY, Devtech.id(name), entry);
		return entry;
	}


	static void init() {}
}
