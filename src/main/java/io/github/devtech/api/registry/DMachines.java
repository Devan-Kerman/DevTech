package io.github.devtech.api.registry;

import java.util.Arrays;

import io.github.devtech.Devtech;
import io.github.devtech.api.DevtechMachine;
import io.github.devtech.base.MechanicalLoom;

public interface DMachines {
	MechanicalLoom TEST = process(new MechanicalLoom());

	static void init() {}

	static <T extends DevtechMachine> T process(T t) {
		Devtech.GENERATORS.addAll(Arrays.asList(t.getGenerators()));
		if(Devtech.IS_CLIENT) {
			t.clientInit();
		}
		return t;
	}
}
