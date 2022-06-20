package acamo;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.ArrayList;
import messer.*;

import javax.swing.plaf.basic.BasicButtonUI;

//TODO: create hash map and complete all operations
public class ActiveAircrafts implements Observer  {
	private HashMap<String, BasicAircraft> activeAircrafts;    	// store the basic aircraft and use its Icao as key
												// replace K and V with the correct class names

	public ActiveAircrafts() {
		this.activeAircrafts = new HashMap<String, BasicAircraft>();
	}

	public synchronized void store(String icao, BasicAircraft ac) {
		this.activeAircrafts.put(icao, ac);
	}

	public synchronized void clear() {
		this.activeAircrafts.clear();
	}

	public synchronized BasicAircraft retrieve(String icao) {
		return activeAircrafts.get(icao);
	}

	public synchronized ArrayList<BasicAircraft> values() {
		return new ArrayList<>(activeAircrafts.values());
	}

	public String toString() {
		return activeAircrafts.toString();
	}

	@Override
	// TODO: store arg in hashmap using the method above
	public void update(Observable o, Object arg) {
		this.store(((BasicAircraft) arg).getIcao(), ((BasicAircraft) arg));
	}
}