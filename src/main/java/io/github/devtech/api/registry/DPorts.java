package io.github.devtech.api.registry;

import io.github.devtech.Devtech;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.base.InputItemPort;
import io.github.devtech.api.port.base.OutputItemPort;
import io.github.devtech.api.port.base.RotationalInputPort;

import net.minecraft.util.registry.Registry;

public interface DPorts {
	Port.Entry ROTATIONAL_INPUT = Registry.register(Port.REGISTRY, Devtech.id("rotational_input"), RotationalInputPort::new);
	Port.Entry ITEM_INPUT = Registry.register(Port.REGISTRY, Devtech.id("item_input"), InputItemPort::new);
	Port.Entry ITEM_OUTPUT = Registry.register(Port.REGISTRY, Devtech.id("item_output"), OutputItemPort::new);


	static void init() {}
}
