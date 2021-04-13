package io.github.devtech.api.participants.type;

public final class RPT {
	public final int rpt;

	public RPT(int rpt) {
		this.rpt = rpt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RPT)) {
			return false;
		}

		RPT energy = (RPT) o;

		return this.rpt == energy.rpt;
	}

	@Override
	public int hashCode() {
		return this.rpt;
	}
}
