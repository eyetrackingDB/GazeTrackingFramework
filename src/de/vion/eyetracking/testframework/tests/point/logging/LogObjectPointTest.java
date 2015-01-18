package de.vion.eyetracking.testframework.tests.point.logging;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.vion.eyetracking.testframework.logging.GenericTestLogObject;

/**
 * The log object for the point test
 * 
 * @author André Pomp
 * 
 */
public class LogObjectPointTest extends GenericTestLogObject {

	private static final String KEY_TIME_PER_POINT = "POINT_TEST_TIME_PER_POINT";
	private static final String KEY_NUMBER_OF_ROWS = "POINT_TEST_NUMBER_OF_ROWS";
	private static final String KEY_NUMBER_OF_COLS = "POINT_TEST_NUMBER_OF_COLS";
	private static final String KEY_NUMBER_OF_REPETITIONS = "POINT_NUMBER_OF_REPETITIONS";
	private static final String KEY_POINTS = "POINTS";

	private int timePerPoint;
	private int numberOfRows;
	private int numberOfCols;
	private int numberOfRepetitions;
	private List<LogObjectPoint> points = new ArrayList<LogObjectPoint>();

	public LogObjectPointTest(String testType, String deviceModel,
			String deviceOrientation, int screenWidth, int screenHeight,
			String abbreviation, long startTimestamp, int timePerPoint,
			int numberOfRows, int numberOfCols, int numberOfRepetitions) {
		super(testType, deviceModel, deviceOrientation, screenWidth,
				screenHeight, abbreviation, startTimestamp);
		this.timePerPoint = timePerPoint;
		this.numberOfRows = numberOfRows;
		this.numberOfCols = numberOfCols;
		this.numberOfRepetitions = numberOfRepetitions;
	}

	public void addPoint(LogObjectPoint point) {
		this.points.add(point);
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject object = super.getJSONObject();
		try {
			object.put(KEY_TIME_PER_POINT, this.timePerPoint);
			object.put(KEY_NUMBER_OF_ROWS, this.numberOfRows);
			object.put(KEY_NUMBER_OF_COLS, this.numberOfCols);
			object.put(KEY_NUMBER_OF_REPETITIONS, this.numberOfRepetitions);

			JSONArray array = new JSONArray();
			for (LogObjectPoint point : this.points) {
				array.put(point.toString());
			}
			object.put(KEY_POINTS, array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}