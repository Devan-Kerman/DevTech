package io.github.devtech.api.registry;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.util.v0.api.Id;
import io.github.devtech.Devtech;
import io.github.devtech.base.gui.ABar;
import io.github.devtech.base.gui.APortSlot;
import io.github.devtech.base.gui.ARotatingWheel;

public interface DDrawables {
	DrawableRegistry.Entry PORT_SLOT = DrawableRegistry.registerForward(Devtech.id2("port_slot"), APortSlot::new);
	DrawableRegistry.Entry BAR = DrawableRegistry.registerForward(Devtech.id2("bar"), ABar::new);
	DrawableRegistry.Entry ROTATING_WHEEL = DrawableRegistry.registerForward(Devtech.id2("rot_whel"), ARotatingWheel::new);

	static void init() {}
}
