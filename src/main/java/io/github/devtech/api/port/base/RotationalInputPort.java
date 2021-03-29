package io.github.devtech.api.port.base;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.devtech.api.participants.FlywheelParticipant;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.registry.DPorts;
import io.github.devtech.api.registry.DSprites;

import net.minecraft.client.util.SpriteIdentifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class RotationalInputPort extends Port {
	public final FlywheelParticipant participant = new FlywheelParticipant(this.maxRPM(), this.rotationalInertia());

	public RotationalInputPort(PortColor color) {
		super(color);
	}

	public RotationalInputPort(NBTagView tag) {
		super(tag);
		this.participant.currentRPM.set(Transaction.GLOBAL, tag.getInt("rpm"));
	}

	@Override
	public void write(NBTagView.Builder builder) {
		super.write(builder);
		builder.putInt("rpm", this.participant.currentRPM.get(Transaction.GLOBAL));
	}

	@Override
	public Entry getEntry() {
		return DPorts.ROTATIONAL_INPUT;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public SpriteIdentifier getTexture() {
		return DSprites.ROTATIONAL_INPUT;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public SpriteIdentifier getBarTexture() {
		return DSprites.SMALL_BAR;
	}

	protected int maxRPM() {
		return 100;
	}

	protected int rotationalInertia() {
		return 10;
	}
}
