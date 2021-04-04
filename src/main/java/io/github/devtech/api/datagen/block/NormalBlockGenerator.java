package io.github.devtech.api.datagen.block;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.models.JModel;

import net.minecraft.util.Identifier;

public class NormalBlockGenerator implements ResourceGenerator {
	public final Identifier id;

	public NormalBlockGenerator(Identifier id) {
		this.id = id;
	}

	@Override
	public void generate(RuntimeResourcePack pack) {
		if(Devtech.IS_CLIENT) {
			Identifier id = fix(this.id, "block");
			pack.addBlockState(JState.state().add(JState.variant(JState.model(id + ""))), this.id);
			pack.addModel(JModel.model("block/cube_all").textures(JModel.textures().var("all", id + "")), id);
		}
	}

	public static Identifier fix(Identifier identifier, String prefix) {
		return new Identifier(identifier.getNamespace(), prefix + '/' + identifier.getPath());
	}
}
