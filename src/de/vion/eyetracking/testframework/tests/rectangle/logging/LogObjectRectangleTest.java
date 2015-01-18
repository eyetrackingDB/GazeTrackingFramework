package de.vion.eyetracking.testframework.tests.rectangle.logging;

import org.json.JSONException;
import org.json.JSONObject;

import de.vion.eyetracking.testframework.logging.GenericTestLogObject;

/**
 * The log object for the rectangle test
 * 
 * @author André Pomp
 * 
 */
public class LogObjectRectangleTest extends GenericTestLogObject {

	private static final String KEY_TIME_PER_SEQUENCE = "RECTANGLE_TEST_TIME_PER_SEQUENCE";
	private static final String KEY_TIME_PER_RECTANGLE = "RECTANGLE_TEST_TIME_PER_RECTANGLE";
	private static final String KEY_PAUSE_BETWEEN_RECTANGLE = "RECTANGLE_TEST_TIME_PAUSE_BETWEEN_RECTANGLE";
	private static final String KEY_PLAY_MUSIC = "RECTANGLE_TEST_PLAY_MUSIC";
	private static final String KEY_PLAY_TONE = "RECTANGLE_TEST_PLAY_TONE";
	private static final String KEY_SEQUENCE = "RECTANGLE_TEST_SEQUENCE";

	private int timePerSequence;
	private int timePerRectangle;
	private int pauseBetweenRectangles;
	private boolean playMusic;
	private boolean playTone;
	private LogObjectRectangleSequence sequence;

	public LogObjectRectangleTest(String testType, String deviceModel,
			String deviceOrientation, int screenWidth, int screenHeight,
			String abbreviation, long startTimestamp, int timePerSequence,
			int timePerRectangle, int pauseBetweenRectangles,
			boolean playMusic, boolean playTone) {
		super(testType, deviceModel, deviceOrientation, screenWidth,
				screenHeight, abbreviation, startTimestamp);
		this.timePerSequence = timePerSequence;
		this.timePerRectangle = timePerRectangle;
		this.pauseBetweenRectangles = pauseBetweenRectangles;
		this.playMusic = playMusic;
		this.playTone = playTone;
	}

	public void setSequence(LogObjectRectangleSequence sequence) {
		this.sequence = sequence;
	}

	public LogObjectRectangleSequence getSequence() {
		return this.sequence;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject object = super.getJSONObject();
		try {
			object.put(KEY_TIME_PER_SEQUENCE, this.timePerSequence);
			object.put(KEY_TIME_PER_RECTANGLE, this.timePerRectangle);
			object.put(KEY_PAUSE_BETWEEN_RECTANGLE, this.pauseBetweenRectangles);
			object.put(KEY_PLAY_MUSIC, this.playMusic);
			object.put(KEY_PLAY_TONE, this.playTone);
			object.put(KEY_SEQUENCE, new JSONObject(this.sequence.toString()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}