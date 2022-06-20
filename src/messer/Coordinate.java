package messer;

public class Coordinate {
	private double latitude;
	private double longitude;

	public Coordinate(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude(){
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public String toString() {
		return this.latitude + " / " + this.longitude;
	}
}