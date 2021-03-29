package io.github.devtech.api.datagen;

import net.devtech.arrp.api.RuntimeResourcePack;

import net.minecraft.util.Identifier;

public interface ResourceGenerator {

	/**
	 * @param pack the resource pack to put the resource in
	 */
	void generate(RuntimeResourcePack pack);

	static Identifier prefixPath(Identifier identifier, String prefix) {
		return new Identifier(identifier.getNamespace(), prefix + identifier.getPath());
	}

	default ResourceGenerator andThen(ResourceGenerator generator) {
		return pack -> {
			this.generate(pack);
			generator.generate(pack);
		};
	}
}
