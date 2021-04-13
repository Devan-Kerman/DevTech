package io.github.devtech.api.port.base;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.devtech.api.participants.FlywheelParticipant;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.registry.DPorts;

public class RotationalInputPort extends Port {
	public RotationalInputPort(PortColor color) {
		super(color);
	}

	public RotationalInputPort(NBTagView tag) {
		super(tag);
	}

	@Override
	public void write(NBTagView.Builder builder) {
		super.write(builder);
	}

	@Override
	public Entry getEntry() {
		return DPorts.ROTATIONAL_INPUT;
	}
}
