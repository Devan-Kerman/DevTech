package io.github.devtech.api.registry;

import java.util.Arrays;

import io.github.devtech.Devtech;
import io.github.devtech.PrimitiveAlloyFurnace;
import io.github.devtech.api.DevtechMachine;

public interface DMachines {
	//MechanicalLoom TEST = process(new MechanicalLoom());
	PrimitiveAlloyFurnace PAT = process(new PrimitiveAlloyFurnace(Devtech.id("primitive_alloy_furnace")));

	static void init() {}

	static <T extends DevtechMachine> T process(T t) {
		Devtech.GENERATORS.addAll(Arrays.asList(t.getGenerators()));
		if(Devtech.IS_CLIENT) {
			t.clientInit();
		}
		return t;
	}
}
