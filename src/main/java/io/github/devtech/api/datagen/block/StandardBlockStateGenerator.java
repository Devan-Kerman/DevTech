package io.github.devtech.api.datagen.block;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;

import net.minecraft.util.Identifier;

public class StandardBlockStateGenerator implements ResourceGenerator {
	public final Identifier id;
	public final String lit, off;

	public StandardBlockStateGenerator(Identifier id, Identifier lit, Identifier off) {
		this.id = id;
		this.lit = lit.toString();
		this.off = off.getNamespace();
	}

	public StandardBlockStateGenerator(Identifier id) {
		this.id = id;
		String base = id.getNamespace() + ":block/" + id.getPath();
		this.lit = base + "_lit";
		this.off = base + "_off";
	}

	@Override
	public void generate(RuntimeResourcePack pack) {
		if (Devtech.IS_CLIENT) {
			pack.addBlockState(JState.state().add(JState.variant().put("facing=north,lit=false", JState.model(this.off))
					                                      .put("facing=east,lit=false", JState.model(this.off).y(90))
					                                      .put("facing=south,lit=false", JState.model(this.off).y(180))
					                                      .put("facing=west,lit=false", JState.model(this.off).y(270))
					                                      .put("facing=north,lit=true", JState.model(this.lit))
					                                      .put("facing=east,lit=true", JState.model(this.lit).y(90))
					                                      .put("facing=south,lit=true", JState.model(this.lit).y(180))
					                                      .put("facing=west,lit=true", JState.model(this.lit).y(270))), this.id);
		}
	}
}
