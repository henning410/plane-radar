package senser;

public class AircraftSentence  {
	String aircraft;

	public AircraftSentence(String aircraft) {
		this.aircraft = aircraft;
	}

	public String getAircraft() {
		return this.aircraft;
	}

	public void setAircraft(String aircraft){
		this.aircraft = aircraft;
	}

	@Override
	public String toString() {
		return aircraft;
	}
}
