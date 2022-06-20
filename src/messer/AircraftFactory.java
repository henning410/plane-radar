package messer;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import senser.AircraftSentence;

public class AircraftFactory {

	public BasicAircraft fromAircraftSentence(AircraftSentence sentence) {
		String icao = null;
		String operator = null;
		Date posTime = null;
		double longitude = 0;
		double latitude = 0;
		double speed = 0;
		double trak = 0;
		int groupCounter = 1;
		JSONObject sentenceAsJSON = new JSONObject();

		// ["3c6dc8", "EWG87U  ", "Germany", 1560500800, 1560500800, 9.6884,   48.8508,
		//   icao      operator              posTime                 longitude latitude
		//   3558.54, false, 197.81, 50.8, 13.98, null, 3695.7, "7716", false, 0]
		//                   speed   trak

		Pattern splitPattern = Pattern.compile("[^(\\[\"\\,)]+");
		Matcher contentMatcher = splitPattern.matcher(sentence.toString());

		//We are just putting the components now into the JSON Object and than later we get them with .getString()
		while(contentMatcher.find()){
			switch(groupCounter){
				case 1:
					sentenceAsJSON.put("icao:", contentMatcher.group());
					break;
				case 2:
					sentenceAsJSON.put("operator:", contentMatcher.group().replaceAll("\\s+", ""));
					break;
				case 4:
					sentenceAsJSON.put("posTime:", contentMatcher.group());
					break;
				case 6:
					sentenceAsJSON.put("longitude:", contentMatcher.group());
					break;
				case 7:
					sentenceAsJSON.put("latitude:", contentMatcher.group());
					break;
				case 10:
					sentenceAsJSON.put("speed:", contentMatcher.group());
					break;
				case 11:
					sentenceAsJSON.put("trak:", contentMatcher.group());
					break;
			}
			groupCounter++;
		}
		//Get the string values from our JSON Object and parse them into the correct type
		icao = sentenceAsJSON.getString("icao:");
		operator = sentenceAsJSON.getString("operator:");
		longitude = Double.parseDouble(sentenceAsJSON.getString("longitude:"));
		latitude = Double.parseDouble(sentenceAsJSON.getString("latitude:"));
		speed = Double.parseDouble(sentenceAsJSON.getString("speed:"));
		trak = Double.parseDouble(sentenceAsJSON.getString("trak:"));
		posTime = new Date (Long.parseLong(sentenceAsJSON.getString("posTime:"))*1000);

		return new BasicAircraft(icao, operator, posTime, new Coordinate(longitude, latitude), speed, trak);
	}
}
