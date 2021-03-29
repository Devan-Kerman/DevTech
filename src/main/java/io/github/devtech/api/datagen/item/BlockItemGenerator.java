package io.github.devtech.api.datagen.item;

import static net.devtech.arrp.json.models.JModel.model;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;

import net.minecraft.util.Identifier;

/**
 * generates a default item model for a block
 */
public class BlockItemGenerator implements ResourceGenerator {
	protected final Identifier data;

	public BlockItemGenerator(Identifier blockIdentifier) {
		this.data = blockIdentifier;
	}

	@Override
	public void generate(RuntimeResourcePack pack) {
		if (Devtech.IS_CLIENT) {
			pack.addModel(model(ResourceGenerator.prefixPath(this.data, "block").toString()), ResourceGenerator.prefixPath(this.data, "item"));
		}
	}
}
