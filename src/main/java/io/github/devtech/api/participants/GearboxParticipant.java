package io.github.devtech.api.participants;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.devtech.api.participants.type.RPT;
import org.jetbrains.annotations.Nullable;

public class GearboxParticipant implements Insertable<RPT> {
	public final Iterable<Insertable<RPT>> recievers;
	public final int ratio, effeciency;

	/**
	 * @param ratio if positive, expands rpm, if negative expands torque
	 * @param effeciency (effeciency-1) / effeciency = true effeciency
	 */
	public GearboxParticipant(Iterable<Insertable<RPT>> recievers, int ratio, int effeciency) {
		this.recievers = recievers;
		this.ratio = ratio;
		this.effeciency = effeciency;
	}

	@Override
	public int insert(@Nullable Transaction transaction, RPT type, int rotationalEnergy) { // rotationalEnergy / rpm = torque (it's assumed that time = 1)
		int applied = type.rpt;
		if(this.ratio < 0) {
			applied /= this.ratio;
		} else {
			applied *= this.ratio;
		}

		int energy = (rotationalEnergy * (this.effeciency - 1)) / this.effeciency, totalInserted = 0;
		RPT RPT = new RPT(applied);
		for (Insertable<RPT> reciever : this.recievers) {
			int inserted = reciever.insert(transaction, RPT, energy);
			energy -= inserted;
			totalInserted += inserted;
			if(energy <= 0) {
				break;
			}
		}

		return totalInserted;
	}
}
