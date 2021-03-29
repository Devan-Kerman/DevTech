package io.github.devtech.api.access;

import io.github.devtech.api.port.Port;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Direction;

public interface PortAccess {
	Port getPortAbsolute(Direction direction);

	/**
	 * @param direction the *relative* direction of the machine (north = forward)
	 */
	@Nullable
	Port getPort(Direction direction);

	/**
	 * if return true and the port is null, the engine is expected to handle dropping the item
	 * @return true if the port can be installed
	 */
	boolean install(Direction direction, @Nullable Port port);
}
