package de.vion.eyetracking.testframework.tests.screen.logging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The log object for a single screen action
 * 
 * @author André Pomp
 * 
 */
public class LogObjectScreenAction {

	private static final String KEY_START_TIME = "SCREEN_START_TIME";
	private static final String KEY_ACTION = "SCREEN_ACTION";

	private String action;
	private long startTime;

	public LogObjectScreenAction(String action, long startTime) {
		super();
		this.action = action;
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put(KEY_ACTION, this.action);
			object.put(KEY_START_TIME, this.startTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
}