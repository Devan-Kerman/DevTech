package io.github.devtech.api.registry;

import java.util.Arrays;

import io.github.devtech.Devtech;
import io.github.devtech.base.Crusher;
import io.github.devtech.base.PrimitiveAlloyFurnace;
import io.github.devtech.api.DevtechMachine;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;

public interface DMachines {
	//MechanicalLoom TEST = process(new MechanicalLoom());
	PrimitiveAlloyFurnace PAT = process(new PrimitiveAlloyFurnace(Devtech.id("primitive_alloy_furnace")));
	Crusher CRUSHER = process(new Crusher(Devtech.id("crusher")));

	static void init() {}

	static void loadResources(RuntimeResourcePack pack, JLang en_us) {
		en_us.block(PAT.block, "Primitive Alloy Furnace");
	}

	static <T extends DevtechMachine> T process(T t) {
		Devtech.GENERATORS.addAll(Arrays.asList(t.getGenerators()));
		if(Devtech.IS_CLIENT) {
			t.clientInit();
		}
		return t;
	}
}
