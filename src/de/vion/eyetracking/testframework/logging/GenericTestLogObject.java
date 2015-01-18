package de.vion.eyetracking.testframework.logging;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
/**
 * The LogObject that is important for each test. It contains the
 * device orientation, model and much more. 
 * 
 * @author André Pomp
 * 
 */
public class GenericTestLogObject {

	private static final String KEY_TEST_TYPE = "GENERAL_TEST_TYPE";
	private static final String KEY_DEVICE_MODEL = "GENERAL_DEVICE_MODEL";
	private static final String KEY_DEVICE_ORIENTATION = "GENERAL_DEVICE_ORIENTATION";
	private static final String KEY_SCREEN_WIDTH = "GENERAL_SCREEN_WIDTH";
	private static final String KEY_SCREEN_HEIGHT = "GENERAL_SCREEN_HEIGHT";
	private static final String KEY_ABBREVIATON = "GENERAL_ABBREVIATION";
	private static final String KEY_START_TIME = "GENERAL_START_TIME";

	private String testType;
	private String deviceModel;
	private String deviceOrientation;
	private int screenWidth;
	private int screenHeight;
	private String abbreviation;
	private long startTimestamp;

	public GenericTestLogObject(String testType, String deviceModel,
			String deviceOrientation, int screenWidth, int screenHeight,
			String abbreviation, long startTimestamp) {
		super();
		this.testType = testType;
		this.deviceModel = deviceModel;
		this.deviceOrientation = deviceOrientation;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.abbreviation = abbreviation;
		this.startTimestamp = startTimestamp;
	}

	public JSONObject getJSONObject() {
		JSONObject object = new JSONObject();
		try {
			object.put(KEY_TEST_TYPE, this.testType);
			object.put(KEY_DEVICE_MODEL, this.deviceModel);
			object.put(KEY_DEVICE_ORIENTATION, this.deviceOrientation);
			object.put(KEY_SCREEN_WIDTH, this.screenWidth);
			object.put(KEY_SCREEN_HEIGHT, this.screenHeight);
			object.put(KEY_ABBREVIATON, this.abbreviation);
			object.put(KEY_START_TIME, this.startTimestamp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

	public void parseJSONObject(String jsonString) throws JSONException {
		JSONObject object = new JSONObject(jsonString);
		this.testType = object.getString(KEY_TEST_TYPE);
		this.deviceModel = object.getString(KEY_DEVICE_MODEL);
		this.deviceOrientation = object.getString(KEY_DEVICE_ORIENTATION);
		this.screenWidth = object.getInt(KEY_SCREEN_WIDTH);
		this.screenHeight = object.getInt(KEY_SCREEN_HEIGHT);
		this.abbreviation = object.getString(KEY_ABBREVIATON);
		this.startTimestamp = object.getLong(KEY_START_TIME);
	}
}