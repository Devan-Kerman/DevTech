package io.github.devtech.api.registry;

import io.github.devtech.Devtech;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.base.ItemPort;
import io.github.devtech.api.port.base.RotationalInputPort;

import net.minecraft.util.registry.Registry;

public interface DPorts {
	Port.Entry ROTATIONAL_INPUT = Registry.register(Port.REGISTRY, Devtech.id("rotational_input"), RotationalInputPort::new);
	Port.Entry ITEM_INPUT = Registry.register(Port.REGISTRY, Devtech.id("item_input"), ItemPort::new);


	static void init() {}
}
