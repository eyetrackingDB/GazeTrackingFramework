package de.vion.eyetracking.testframework.tests.word.logging;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The log object for a sequence of words that have the same font size
 * 
 * @author André Pomp
 * 
 */
public class LogObjectWordSequence {

	private static final String KEY_FONT = "SEQ_FONT";
	private static final String KEY_FONT_SIZE = "SEQ_FONT_SIZE";
	private static final String KEY_START_TIME = "SEQ_START_TIME";
	private static final String KEY_WORDS = "SEQ_WORDS";

	private String font;
	private String fontSize;
	private long startTime;
	private List<LogObjectWord> words = new ArrayList<LogObjectWord>();

	public LogObjectWordSequence(String font, String fontSize, long startTime) {
		super();
		this.font = font;
		this.fontSize = fontSize;
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		try {
			object.put(KEY_FONT, this.font);
			object.put(KEY_FONT_SIZE, this.fontSize);
			object.put(KEY_START_TIME, this.startTime);

			JSONArray array = new JSONArray();
			for (LogObjectWord rectangle : this.words) {
				array.put(rectangle.toString());
			}
			object.put(KEY_WORDS, array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}

	public void addWord(LogObjectWord word) {
		this.words.add(word);
	}

	public String toFileName() {
		return this.fontSize;
	}
}