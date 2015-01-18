package de.vion.eyetracking.testframework.tests.word.logging;

import org.json.JSONException;
import org.json.JSONObject;

import de.vion.eyetracking.testframework.logging.GenericTestLogObject;

/**
 * The log object for the word test
 * 
 * @author André Pomp
 * 
 */
public class LogObjectWordTest extends GenericTestLogObject {

	private static final String KEY_TIME_PER_WORD = "WORD_TEST_TIME_WORD";
	private static final String KEY_NUMBER_OF_WORDS = "WORD_NUMBER_OF_WORDS";
	private static final String KEY_SEQUENCE = "WORD_TEST_SEQUENCE";

	private int timePerWord;
	private int numberOfWords;
	private LogObjectWordSequence sequence;

	public LogObjectWordTest(String testType, String deviceModel,
			String deviceOrientation, int screenWidth, int screenHeight,
			String abbreviation, long startTimestamp, int timePerWord,
			int numberOfWords) {
		super(testType, deviceModel, deviceOrientation, screenWidth,
				screenHeight, abbreviation, startTimestamp);
		this.timePerWord = timePerWord;
		this.numberOfWords = numberOfWords;
	}

	public void setSequence(LogObjectWordSequence sequence) {
		this.sequence = sequence;
	}

	public LogObjectWordSequence getSequence() {
		return this.sequence;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject object = super.getJSONObject();
		try {
			object.put(KEY_TIME_PER_WORD, this.timePerWord);
			object.put(KEY_NUMBER_OF_WORDS, this.numberOfWords);
			object.put(KEY_SEQUENCE, new JSONObject(this.sequence.toString()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}