package de.vion.eyetracking.testframework.tests.screen.logging;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.vion.eyetracking.testframework.logging.GenericTestLogObject;

/**
 * The log object for the second screen test
 * 
 * @author André Pomp
 * 
 */
public class LogObjectScreenTest extends GenericTestLogObject {

	private static final String KEY_TIME_ON_TABLET = "SCREEN_TIME_ON_TABLET";
	private static final String KEY_TIME_BESIDE_TABLET = "SCREEN_TIME_BESIDE_TABLET";
	private static final String KEY_NUMBER_OF_REPETITIONS = "SCREEN_NUMBER_OF_REPETITIONS";
	private static final String KEY_ACTIONS = "SCREEN_ACTIONS";

	private int timeOnTablet;
	private int timeBesideTablet;
	private int numerOfRepetitions;

	private List<LogObjectScreenAction> actions = new ArrayList<LogObjectScreenAction>();

	public LogObjectScreenTest(String testType, String deviceModel,
			String deviceOrientation, int screenWidth, int screenHeight,
			String abbreviation, long startTimestamp, int timeOnTablet,
			int timeBesideTablet, int numerOfRepetitions) {
		super(testType, deviceModel, deviceOrientation, screenWidth,
				screenHeight, abbreviation, startTimestamp);
		this.timeOnTablet = timeOnTablet;
		this.timeBesideTablet = timeBesideTablet;
		this.numerOfRepetitions = numerOfRepetitions;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject object = super.getJSONObject();
		try {
			object.put(KEY_TIME_ON_TABLET, this.timeOnTablet);
			object.put(KEY_TIME_BESIDE_TABLET, this.timeBesideTablet);
			object.put(KEY_NUMBER_OF_REPETITIONS, this.numerOfRepetitions);

			JSONArray array = new JSONArray();
			for (LogObjectScreenAction line : this.actions) {
				array.put(line.toString());
			}
			object.put(KEY_ACTIONS, array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

	public List<LogObjectScreenAction> getActions() {
		return this.actions;
	}
}