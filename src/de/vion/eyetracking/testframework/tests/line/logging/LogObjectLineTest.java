package de.vion.eyetracking.testframework.tests.line.logging;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.vion.eyetracking.testframework.logging.GenericTestLogObject;

/**
 * The log object for the line test
 * 
 * @author André Pomp
 * 
 */
public class LogObjectLineTest extends GenericTestLogObject {

	private static final String KEY_VELOCITY = "LINE_TEST_VELOCITY";
	private static final String KEY_NUMBER_OF_LINES = "LINE_TEST_NUMBER_OF_LINES";
	private static final String KEY_NUMBER_OF_REPETITIONS = "LINE_TEST_NUMBER_OF_REPETITIONS";
	private static final String KEY_LINES = "LINE_LINES";

	private int velocity;
	private int numberOfLines;
	private int numberOfRepetitions;
	private List<LogObjectLine> lines = new ArrayList<LogObjectLine>();

	public LogObjectLineTest(String testType, String deviceModel,
			String deviceOrientation, int screenWidth, int screenHeight,
			String abbreviation, long startTimestamp, int velocity,
			int numberOfLines, int numberOfRepetitions) {
		super(testType, deviceModel, deviceOrientation, screenWidth,
				screenHeight, abbreviation, startTimestamp);
		this.velocity = velocity;
		this.numberOfLines = numberOfLines;
		this.numberOfRepetitions = numberOfRepetitions;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject object = super.getJSONObject();
		try {
			object.put(KEY_VELOCITY, this.velocity);
			object.put(KEY_NUMBER_OF_LINES, this.numberOfLines);
			object.put(KEY_NUMBER_OF_REPETITIONS, this.numberOfRepetitions);

			JSONArray array = new JSONArray();
			for (LogObjectLine line : this.lines) {
				array.put(line.toString());
			}
			object.put(KEY_LINES, array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

	public List<LogObjectLine> getLines() {
		return this.lines;
	}
}