package de.vion.eyetracking.testframework.tests.point.logging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The log object for a single point
 * 
 * @author André Pomp
 * 
 */
public class LogObjectPoint {

	private static final String KEY_CENTER_X = "POINT_CENTER_X";
	private static final String KEY_CENTER_Y = "POINT_CENTER_Y";
	private static final String KEY_START_TIME = "POINT_START_TIME";

	private int centerX;
	private int centerY;
	private long startTime;

	public LogObjectPoint(int centerX, int centerY, long startTime) {
		super();
		this.centerX = centerX;
		this.centerY = centerY;
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put(KEY_CENTER_X, this.centerX);
			object.put(KEY_CENTER_Y, this.centerY);
			object.put(KEY_START_TIME, this.startTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
}