package io.github.devtech.api.participants;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.devtech.api.participants.type.RPM;
import org.jetbrains.annotations.Nullable;

public class GearboxParticipant implements Insertable<RPM> {
	public final Iterable<Insertable<RPM>> recievers;
	public final int ratio, effeciency;

	/**
	 * @param ratio if positive, expands rpm, if negative expands torque
	 * @param effeciency (effeciency-1) / effeciency = true effeciency
	 */
	public GearboxParticipant(Iterable<Insertable<RPM>> recievers, int ratio, int effeciency) {
		this.recievers = recievers;
		this.ratio = ratio;
		this.effeciency = effeciency;
	}

	@Override
	public int insert(@Nullable Transaction transaction, RPM type, int rotationalEnergy) { // rotationalEnergy / rpm = torque (it's assumed that time = 1)
		int applied = type.rpm;
		if(this.ratio < 0) {
			applied /= this.ratio;
		} else {
			applied *= this.ratio;
		}

		int energy = (rotationalEnergy * (this.effeciency - 1)) / this.effeciency, totalInserted = 0;
		RPM rpm = new RPM(applied);
		for (Insertable<RPM> reciever : this.recievers) {
			int inserted = reciever.insert(transaction, rpm, energy);
			energy -= inserted;
			totalInserted += inserted;
			if(energy <= 0) {
				break;
			}
		}

		return totalInserted;
	}
}
