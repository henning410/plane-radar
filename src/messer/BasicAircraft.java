package messer;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BasicAircraft {
	private String icao;
	private String operator;
	private Date posTime;
	private Coordinate coordinate;
	private Double speed;
	private Double trak;

	public BasicAircraft(String icao, String operator, Date posTime, Coordinate coordinate, Double speed, Double trak){
		this.icao = icao;
		this.operator = operator;
		this.posTime = posTime;
		this.coordinate = coordinate;
		this.speed = speed;
		this.trak = trak;
	}

	public String getIcao() {
		return this.icao;
	}
	public String getOperator() {
		return this.operator;
	}
	public  Date getPosTime(){
		return this.posTime;
	}
	public Coordinate getCoordinate() {
		return this.coordinate;
	}
	public Double getSpeed(){
		return this.speed;
	}
	public Double getTrak() {
		return this.trak;
	}

	//TODO: Lab 4-6 return attribute names and values for table
	public static ArrayList<String> getAttributesNames()  {
		ArrayList<String> attributes = new ArrayList<String>();

		attributes.add("icao");
		attributes.add("operator");
		attributes.add("posTime");
		attributes.add("coordinate");
		attributes.add("speed");
		attributes.add("trak");

		return attributes;
	}

	public static ArrayList<Object> getAttributesValues(BasicAircraft ac)  {
		ArrayList<Object> attributes = new ArrayList<Object>();
		
		// get values of one plane
		attributes.add(ac.getIcao());
		attributes.add(ac.getOperator());
		attributes.add(ac.getPosTime());
		attributes.add(ac.getCoordinate());
		attributes.add(ac.getSpeed());
		attributes.add(ac.getTrak());

		return attributes;
	}

	@Override
	public String toString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

		return "[icao=" + this.icao + ", operator=" + this.operator + ", posTime: " + this.posTime + ", coordinate= " + this.coordinate + ", speed=" + this.speed + ", trak=" + this.trak + "]";
	}
}
