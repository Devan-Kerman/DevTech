package io.github.devtech.api.datagen.item;

import static net.devtech.arrp.json.models.JModel.model;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;

import net.minecraft.util.Identifier;

public class DevtechBlockItemGenerator extends BlockItemGenerator {
	public DevtechBlockItemGenerator(Identifier blockIdentifier) {
		super(blockIdentifier);
	}

	@Override
	public void generate(RuntimeResourcePack pack) {
		if (Devtech.IS_CLIENT) {
			pack.addModel(model(ResourceGenerator.prefixPath(this.data, "block/").toString() + "_off"), ResourceGenerator.prefixPath(this.data, "item/"));
		}
	}
}
