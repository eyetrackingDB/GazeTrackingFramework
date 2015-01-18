package de.vion.eyetracking.testframework.tests.rectangle.logging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The log object for a single rectangle
 * 
 * @author André Pomp
 * 
 */
public class LogObjectRectangle {

	private static final String KEY_TOP_LEFT_X = "REC_TOP_LEFT_X";
	private static final String KEY_TOP_LEFT_Y = "REC_TOP_LEFT_Y";
	private static final String KEY_BOTTOM_RIGHT_X = "REC_BOTTOM_RIGHT_X";
	private static final String KEY_BOTTOM_RIGHT_Y = "REC_BOTTOM_RIGHT_Y";
	private static final String KEY_ROW = "REC_ROW";
	private static final String KEY_COL = "REC_COL";
	private static final String KEY_START_TIME = "REC_START_TIME";

	private int topLeftX;
	private int topLeftY;
	private int bottomRightX;
	private int bottomRightY;
	private int row;
	private int col;
	private long startTime;

	public LogObjectRectangle(int topLeftX, int topLeftY, int bottomRightX,
			int bottomRightY, int row, int col, long startTime) {
		super();
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.bottomRightX = bottomRightX;
		this.bottomRightY = bottomRightY;
		this.row = row;
		this.col = col;
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put(KEY_ROW, this.row);
			object.put(KEY_COL, this.col);
			object.put(KEY_TOP_LEFT_X, this.topLeftX);
			object.put(KEY_TOP_LEFT_Y, this.topLeftY);
			object.put(KEY_BOTTOM_RIGHT_X, this.bottomRightX);
			object.put(KEY_BOTTOM_RIGHT_Y, this.bottomRightY);
			object.put(KEY_START_TIME, this.startTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
}