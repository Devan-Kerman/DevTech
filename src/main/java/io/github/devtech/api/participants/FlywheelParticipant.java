package io.github.devtech.api.participants;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import io.github.devtech.api.participants.type.RPT;
import org.jetbrains.annotations.Nullable;

public class FlywheelParticipant implements Insertable<RPT> {
	public int maxRPT, momentOfInertia;
	public final Key.Int currentRPTSquared = new IntKeyImpl(0);

	public FlywheelParticipant(int maxRPT, int momentOfInertia) {
		this.maxRPT = maxRPT;
		this.momentOfInertia = momentOfInertia;
	}

	@Override
	public int insert(@Nullable Transaction transaction, RPT type, int power) { // assume time = 1
		int moment = this.getMomentOfInertia(transaction);
		int currentRPTSquared = this.currentRPTSquared.get(transaction);
		int toConsumeRPTSquared = Math.min(power * 2 / moment, this.getMaxRPTSquared(transaction) - currentRPTSquared);
		int toConsumeEnergy = toConsumeRPTSquared * moment / 2;
		if(toConsumeEnergy > 0) {
			this.currentRPTSquared.set(transaction, toConsumeRPTSquared + currentRPTSquared);
			return toConsumeEnergy;
		}
		return 0;
	}

	@Override
	public boolean isFull(@Nullable Transaction transaction) {
		return this.currentRPTSquared.get(transaction) == this.maxRPT;
	}
	public int getEnergy(Transaction transaction) { return this.currentRPTSquared.get(transaction) * this.getMomentOfInertia(transaction) / 2; }
	public int getMaxRPT(Transaction transaction) {
		return this.maxRPT;
	}
	public int getMaxRPTSquared(Transaction transaction) {
		return this.maxRPT * this.maxRPT;
	}
	public int getMomentOfInertia(Transaction transaction) {
		return this.momentOfInertia;
	}
}
