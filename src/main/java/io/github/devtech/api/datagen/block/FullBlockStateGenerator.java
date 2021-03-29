package io.github.devtech.api.datagen.block;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;

import net.minecraft.util.Identifier;

public class FullBlockStateGenerator implements ResourceGenerator {
	public final Identifier id;

	public FullBlockStateGenerator(Identifier id) {
		this.id = id;
	}

	@Override
	public void generate(RuntimeResourcePack pack) {
		if(Devtech.IS_CLIENT) {
			pack.addBlockState(JState.state().add(JState.variant(JState.model("devtech:block/test"))), this.id);
		}
	}

	public static Identifier fix(Identifier identifier, String prefix) {
		return new Identifier(identifier.getNamespace(), prefix + '/' + identifier.getPath());
	}
}
