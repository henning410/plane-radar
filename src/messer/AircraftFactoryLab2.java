package messer;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import senser.AircraftSentence;

public class AircraftFactoryLab2 {

	public BasicAircraft fromAircraftSentence(AircraftSentence sentence) {
		String icao = null;
		String operator = null;
		Date posTime = null;
		double longitude = 0;
		double latitude = 0;
		double speed = 0;
		double trak = 0;

		// ["3c6dc8", "EWG87U  ", "Germany", 1560500800, 1560500800, 9.6884,   48.8508,
		//   icao      operator              posTime                 longitude latitude
		//   3558.54, false, 197.81, 50.8, 13.98, null, 3695.7, "7716", false, 0]
		//                   speed   trak

		Pattern splitPattern = Pattern.compile("[^(\\[\"\\,)]+");
		Matcher contentMatcher = splitPattern.matcher(sentence.toString());
		int groupCounter = 1;
		while(contentMatcher.find()){
			switch(groupCounter){
				case 1:
					icao = contentMatcher.group();
					break;
				case 2:
					operator = contentMatcher.group().replaceAll("\\s+", "");
					break;
				case 4:
					posTime = new Date(Long.parseLong(contentMatcher.group())*1000); //This took a while to find out what timeformat is given. I never heard of Unix timestamp :)
					break;
				case 6:
					longitude = Double.parseDouble(contentMatcher.group());
					break;
				case 7:
					latitude = Double.parseDouble(contentMatcher.group());
					break;
				case 10:
					speed = Double.parseDouble(contentMatcher.group());
					break;
				case 11:
					trak = Double.parseDouble(contentMatcher.group());
					break;
			}
			groupCounter++;
		}
		BasicAircraft msg = new BasicAircraft(icao, operator, posTime, new Coordinate(latitude, longitude), speed, trak);
		
		return msg;
	}
}
