package io.github.devtech.api.datagen.item;

import static net.devtech.arrp.json.models.JModel.model;
import static net.devtech.arrp.json.models.JModel.textures;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;

import net.minecraft.util.Identifier;

public class StandardItemModelGenerator implements ResourceGenerator {
	protected final Identifier item;
	protected final Identifier texture;

	public StandardItemModelGenerator(Identifier item) {
		this(item, ResourceGenerator.prefixPath(item, "item/"));
	}

	public StandardItemModelGenerator(Identifier item, Identifier texture) {
		this.item = item;
		this.texture = texture;
	}

	@Override
	public void generate(RuntimeResourcePack pack) {
		if (Devtech.IS_CLIENT) {
			pack.addModel(model("item/generated").textures(textures().layer0(this.texture.toString())), ResourceGenerator.prefixPath(this.item, "item/"));
		}
	}
}
