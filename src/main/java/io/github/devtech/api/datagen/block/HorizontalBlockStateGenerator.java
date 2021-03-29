package io.github.devtech.api.datagen.block;

import io.github.devtech.Devtech;
import io.github.devtech.api.datagen.ResourceGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class HorizontalBlockStateGenerator implements ResourceGenerator {
	protected final Identifier id;
	protected final String model;

	public HorizontalBlockStateGenerator(Identifier id) {
		this(id, id.getNamespace() + ":block/" + id.getPath());
	}

	public HorizontalBlockStateGenerator(Identifier id, String model) {
		this.id = id;
		this.model = model;
	}

	@Override
	public void generate(RuntimeResourcePack pack) {
		if (Devtech.IS_CLIENT) {
			pack.addBlockState(JState.state().add(JState.variant().put("facing", Direction.NORTH, JState.model(this.model))
					                                      .put("facing", Direction.EAST, JState.model(this.model).y(90))
					                                      .put("facing", Direction.SOUTH, JState.model(this.model).y(180))
					                                      .put("facing", Direction.WEST, JState.model(this.model).y(270))), this.id);
		}
	}
}
