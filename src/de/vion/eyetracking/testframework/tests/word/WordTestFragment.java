package de.vion.eyetracking.testframework.tests.word;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.vion.eyetracking.R;
import de.vion.eyetracking.misc.Device;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.settings.GazeTrackingPreferences;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestFragment;
import de.vion.eyetracking.testframework.logging.TestLogger;
import de.vion.eyetracking.testframework.tests.word.logging.LogObjectWord;
import de.vion.eyetracking.testframework.tests.word.logging.LogObjectWordCallback;
import de.vion.eyetracking.testframework.tests.word.logging.LogObjectWordSequence;
import de.vion.eyetracking.testframework.tests.word.logging.LogObjectWordTest;
import de.vion.eyetracking.testframework.tests.word.runnable.WordTestRunnable;
import de.vion.eyetracking.testframework.tests.word.settings.WordTestGeneralSettings;
import de.vion.eyetracking.testframework.tests.word.settings.WordTestSingleSettings;
import de.vion.eyetracking.testframework.tests.word.view.WordTextView;

/**
 * The Fragment for the word test. It can use the sequential as well as the
 * single rectangle test
 * 
 * @author André Pomp
 * 
 */
public class WordTestFragment extends GenericTestFragment implements
		LogObjectWordCallback {

	public static WordTestFragment createInstance(TestType type,
			String abbreviation, String subdir, boolean demo) {
		WordTestFragment fragment = new WordTestFragment();
		fragment.setArguments(createArgs(type, abbreviation, subdir, demo));
		return fragment;
	}

	private List<Float> listOfFontSizes = new ArrayList<Float>();
	private int timePerWord;
	boolean loopSequence = false;

	private View rootView;
	private WordTextView textView;

	private WordTestRunnable runnable;
	private String[] allWords;
	private String text;

	private LogObjectWordTest logObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!this.demoModus) {
			FileManager.writeRessourceToSD(getActivity(),
					FileManager.getTestDirectoryTestLogging(this.subDir),
					R.raw.readme_word_test);
		}

		// Get the settings
		this.timePerWord = ((WordTestGeneralSettings) this.settings)
				.getTimePerWord(getActivity());

		try {
			InputStream in_s = new BufferedInputStream(new FileInputStream(
					((WordTestGeneralSettings) this.settings)
							.getTextFilePath(getActivity())));

			byte[] b = new byte[in_s.available()];
			in_s.read(b);
			this.text = new String(b);
			this.allWords = this.text.split(" "); // split text into single
													// words
			in_s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create the list of font sizes
		if (this.testType == TestType.WORD_SINGLE_TEST) {
			this.loopSequence = true;
			float fontSize = ((WordTestSingleSettings) this.settings)
					.getFontSize(getActivity());
			this.listOfFontSizes.add(fontSize);
		} else if (this.testType == TestType.WORD_SEQUENCE_TEST) {
			this.loopSequence = false;
			this.listOfFontSizes.add(20f);
			this.listOfFontSizes.add(24f);
			this.listOfFontSizes.add(28f);
			this.listOfFontSizes.add(32f);
			this.listOfFontSizes.add(36f);
			this.listOfFontSizes.add(40f);
			this.listOfFontSizes.add(44f);
		}

		// Shuffle the list randomly
		Collections
				.shuffle(this.listOfFontSizes, new Random(System.nanoTime()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.rootView = inflater.inflate(R.layout.test_fragment_word,
				container, false);
		this.textView = (WordTextView) this.rootView
				.findViewById(R.id.test_fragment_word_tv);
		this.textView.setCallback(this);

		return this.rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		this.runnable = new WordTestRunnable(this,
				WordTestFragment.this.allWords.length, this.timePerWord,
				this.listOfFontSizes, this.loopSequence, this);
		new Thread(this.runnable).start();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (this.runnable != null) {
			this.runnable.stopRunning();
		}
	}

	public void updateText(int coloredWordPos, float fontSize) {
		String newTest = "";

		int startIndex = 0;
		int stopIndex = 0;
		int currentIndex = 0;
		String currentWord = "";
		for (int i = 0; i < this.allWords.length; i++) {
			if (coloredWordPos == i) {
				currentWord = this.allWords[i];
				startIndex = currentIndex;
				stopIndex = startIndex + this.allWords[i].length();
				newTest += "<font color='#EE0000'>" + this.allWords[i]
						+ " </font>";
			} else {
				newTest += this.allWords[i] + " ";
				currentIndex = newTest.length(); // this would be the index
													// of the new word
			}
		}

		this.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		this.textView.setText(Html.fromHtml(newTest));
		this.textView.setIndex(currentWord, startIndex, stopIndex);
	}

	@Override
	public void onTestStarted() {
		// Create the logobject
		String deviceModel = Device.getDeviceModel();
		String deviceOrientation = GazeTrackingPreferences
				.getDeviceOrientation(getActivity());
		int[] deviceParams = Device.getDeviceParameters(getActivity());

		// Create a new log object
		this.logObject = new LogObjectWordTest(this.testType.name(),
				deviceModel, deviceOrientation, deviceParams[1],
				deviceParams[0], this.testSubjectAbbreviation,
				System.currentTimeMillis(), this.timePerWord,
				this.allWords.length);
	}

	@Override
	public void onSequenceStarted(String fontSize, long time) {
		LogObjectWordSequence sequence = new LogObjectWordSequence(
				"SANS_SERIF_BOLD", fontSize, time);
		this.logObject.setSequence(sequence);
	}

	@Override
	public void onWordPositionCalculated(String currentWord, int topLeftX,
			int topLeftY, int bottomRightX, int bottomRightY, long currentTime) {
		// Get the absolute current position on the screen of the view
		int[] array = new int[2];
		this.textView.getLocationOnScreen(array);

		// Add the the offset of the absolute position to the corners
		int absoluteTopLeftX = topLeftX + array[0];
		int absoluteTopLeftY = topLeftY + array[1];
		int absoluteBottomRightX = bottomRightX + array[0];
		int absoluteBottomRightY = bottomRightY + array[1];

		LogObjectWord word = new LogObjectWord(currentWord, absoluteTopLeftX,
				absoluteTopLeftY, absoluteBottomRightX, absoluteBottomRightY,
				currentTime);
		this.logObject.getSequence().addWord(word);
	}

	@Override
	public void onSequenceFinished() {
		final String fileName = this.testType.name() + "_"
				+ this.logObject.getSequence().toFileName() + "_"
				+ this.testSubjectAbbreviation + "_"
				+ System.currentTimeMillis() + ".json";

		final String directory = FileManager
				.getTestDirectoryTestLogging(this.subDir);
		final JSONObject object = this.logObject.getJSONObject();

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!WordTestFragment.this.demoModus) {
					TestLogger.storeTestData(directory, fileName, object);
				}
			}
		}).start();
	}
}