package de.vion.eyetracking.testframework.tests.line.logging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The log object for a single line
 * 
 * @author André Pomp
 * 
 */
public class LogObjectLine {

	private static final String KEY_TOP_CENTER_Y = "LINE_CENTER_Y";
	private static final String KEY_LINE_NUMBER = "LINE_NUMBER";
	private static final String KEY_START_TIME = "LINE_START_TIME";

	private int centerY;
	private int lineNumber;
	private long startTime;

	public LogObjectLine(int centerY, int lineNumber, long startTime) {
		super();
		this.centerY = centerY;
		this.lineNumber = lineNumber;
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put(KEY_LINE_NUMBER, this.lineNumber);
			object.put(KEY_TOP_CENTER_Y, this.centerY);
			object.put(KEY_START_TIME, this.startTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
}