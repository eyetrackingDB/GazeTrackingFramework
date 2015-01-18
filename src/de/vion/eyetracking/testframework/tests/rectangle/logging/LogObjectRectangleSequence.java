package de.vion.eyetracking.testframework.tests.rectangle.logging;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The log object for a sequence of rectangles
 * 
 * @author André Pomp
 * 
 */
public class LogObjectRectangleSequence {

	private static final String KEY_NUMBER_OF_ROWS = "SEQ_NUMBER_OF_ROWS";
	private static final String KEY_NUMBER_OF_COLS = "SEQ_NUMBER_OF_COLS";
	private static final String KEY_SEQ_START_TIME = "SEQ_START_TIME";
	private static final String KEY_RECTANGLES = "SEQ_RECTANGLES";

	private int numberOfRows;
	private int numberOfCols;
	private long startTime;
	private List<LogObjectRectangle> rectangles = new ArrayList<LogObjectRectangle>();

	public LogObjectRectangleSequence(int numberOfRows, int numberOfCols,
			long startTime) {
		super();
		this.numberOfRows = numberOfRows;
		this.numberOfCols = numberOfCols;
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put(KEY_NUMBER_OF_ROWS, this.numberOfRows);
			object.put(KEY_NUMBER_OF_COLS, this.numberOfCols);
			object.put(KEY_SEQ_START_TIME, this.startTime);

			JSONArray array = new JSONArray();
			for (LogObjectRectangle rectangle : this.rectangles) {
				array.put(rectangle.toString());
			}
			object.put(KEY_RECTANGLES, array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}

	public void addRectangle(LogObjectRectangle rectangle) {
		this.rectangles.add(rectangle);
	}

	public String toFileName() {
		return this.numberOfRows + "x" + this.numberOfCols;
	}
}