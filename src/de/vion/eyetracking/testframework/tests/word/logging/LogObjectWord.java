package de.vion.eyetracking.testframework.tests.word.logging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The log object for a single word
 * 
 * @author André Pomp
 * 
 */
public class LogObjectWord {

	private static final String KEY_WORD = "WORD";
	private static final String KEY_TOP_LEFT_X = "WORD_TOP_LEFT_X";
	private static final String KEY_TOP_LEFT_Y = "WORD_TOP_LEFT_Y";
	private static final String KEY_BOTTOM_RIGHT_X = "WORD_BOTTOM_RIGHT_X";
	private static final String KEY_BOTTOM_RIGHT_Y = "WORD_BOTTOM_RIGHT_Y";
	private static final String KEY_START_TIME = "WORD_START_TIME";

	private String word;
	private int topLeftX;
	private int topLeftY;
	private int bottomRightX;
	private int bottomRightY;
	private long startTime;

	public LogObjectWord(String word, int topLeftX, int topLeftY,
			int bottomRightX, int bottomRightY, long startTime) {
		super();
		this.word = word;
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.bottomRightX = bottomRightX;
		this.bottomRightY = bottomRightY;
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put(KEY_WORD, this.word);
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
