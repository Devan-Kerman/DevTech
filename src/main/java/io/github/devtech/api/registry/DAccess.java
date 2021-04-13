package io.github.devtech.api.registry;

import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.devtech.api.participants.type.RPT;

public interface DAccess {
	WorldAccess<Participant<RPT>> ROTATIONAL_ENERGY_ACCESS = new WorldAccess<>();
}
