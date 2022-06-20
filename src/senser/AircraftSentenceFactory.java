package senser;

import java.util.ArrayList;

public class AircraftSentenceFactory  {

	public ArrayList<AircraftSentence> fromAircraftJson(String jsonAircraftList)  {
		ArrayList<AircraftSentence> aircraftList = new ArrayList<>();
		String[] jsonAircraftSplitted = jsonAircraftList.split("\\],\\[");  //Splitting Aircraftlist

		//Adding splitted parts to ArrayList
		for (String sentence : jsonAircraftSplitted){
			aircraftList.add(new AircraftSentence(sentence));
		}
		return aircraftList;
	}
}
