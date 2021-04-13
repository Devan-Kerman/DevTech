package io.github.devtech.api.registry;

import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.devtech.Devtech;

public interface DTextures {
	// todo document need for .png
	Sprite.Sized BAR = gui(0, 0, 256, 8);
	Sprite.Sized PIN = gui(0, 8, 3, 8);
	Sprite.Sized WHEEL_SMALL = gui(3, 8, 8, 8);
	Sprite.Sized SUN_OFF = gui(11, 8, 8, 8);
	Sprite.Sized SUN_ON = gui(19, 8, 8, 8);


	static Sprite.Sized gui(int offX, int offY, int width, int height) {
		return Sprite.of(Devtech.id2("textures/gui/textures.png")).cutout(offX/256f, offY/256f, width/256f, height/256f).sized(width, height);
	}
}
