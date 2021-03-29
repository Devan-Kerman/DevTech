package io.github.devtech.api.participants.type;

public final class RPM {
	public final int rpm;
	public RPM(int rpm) {
		this.rpm = rpm;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RPM)) {
			return false;
		}

		RPM energy = (RPM) o;

		return this.rpm == energy.rpm;
	}

	@Override
	public int hashCode() {
		return this.rpm;
	}
}
