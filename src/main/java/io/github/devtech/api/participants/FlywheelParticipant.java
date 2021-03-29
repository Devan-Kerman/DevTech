package io.github.devtech.api.participants;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import io.github.devtech.api.participants.type.RPM;
import org.jetbrains.annotations.Nullable;

public class FlywheelParticipant implements Insertable<RPM> {
	public int maxRPM, momentOfInertia;
	public final Key.Int currentRPM = new IntKeyImpl(0);

	public FlywheelParticipant(int maxRPM, int momentOfInertia) {
		this.maxRPM = maxRPM;
		this.momentOfInertia = momentOfInertia;
	}

	@Override
	public int insert(@Nullable Transaction transaction, RPM type, int power) { // assume time = 1
		int moment = this.getMomentOfInertia(transaction);
		int currentRPM = this.currentRPM.get(transaction);
		int toConsumeRPM = Math.min(power / moment, this.getMaxRPM(transaction) - currentRPM);
		int toConsumeEnergy = toConsumeRPM * moment;
		if(toConsumeEnergy > 0) {
			this.currentRPM.set(transaction, toConsumeRPM + currentRPM);
			return toConsumeEnergy;
		}
		return 0;
	}

	@Override
	public boolean isFull(@Nullable Transaction transaction) {
		return this.currentRPM.get(transaction) == this.maxRPM;
	}

	public int getMaxRPM(Transaction transaction) {
		return this.maxRPM;
	}

	public int getMomentOfInertia(Transaction transaction) {
		return this.momentOfInertia;
	}
}
