package de.vion.eyetracking.testframework.tests.rectangle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
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
import de.vion.eyetracking.testframework.tests.rectangle.item.RectangleProfile;
import de.vion.eyetracking.testframework.tests.rectangle.logging.LogObjectRectangle;
import de.vion.eyetracking.testframework.tests.rectangle.logging.LogObjectRectangleCallback;
import de.vion.eyetracking.testframework.tests.rectangle.logging.LogObjectRectangleSequence;
import de.vion.eyetracking.testframework.tests.rectangle.logging.LogObjectRectangleTest;
import de.vion.eyetracking.testframework.tests.rectangle.runnable.RectangleControlRunnable;
import de.vion.eyetracking.testframework.tests.rectangle.settings.RectangleTestGeneralSettings;
import de.vion.eyetracking.testframework.tests.rectangle.settings.RectangleTestSequenceSettings;
import de.vion.eyetracking.testframework.tests.rectangle.settings.RectangleTestSingleSettings;

/**
 * The Fragment for the rectangle test. It can use the sequential as well as the
 * single rectangle test
 * 
 * @author André Pomp
 * 
 */
public class RectangleTestFragment extends GenericTestFragment implements
		LogObjectRectangleCallback {

	public static RectangleTestFragment createInstance(TestType type,
			String abbreviation, String subdir, boolean demo) {
		RectangleTestFragment fragment = new RectangleTestFragment();
		fragment.setArguments(createArgs(type, abbreviation, subdir, demo));
		return fragment;
	}

	// The current view and its parent
	private ViewGroup containerView;

	// The current profiles
	private List<RectangleProfile> profiles = new ArrayList<RectangleProfile>();

	// Get the Settings
	private int timePerRectangle = 0;
	private int pauseBetweenRectangles = 0;
	private boolean playMusic = false;
	private boolean playTone = true;

	// The media player and tone generator for playing the current tone and
	// music
	private MediaPlayer mediaPlayer;
	private File musicFile;
	private ToneGenerator toneGenerator;

	// Handler for handling the current sequence timing
	private Handler handler = new Handler();

	// The current control runnable
	private RectangleControlRunnable controlRunnable;

	// Values that depend on the test type
	private int timePerSequence = 0;
	private boolean loopSequence = false;

	// Logging
	private LogObjectRectangleTest logObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!this.demoModus) {
			FileManager.writeRessourceToSD(getActivity(),
					FileManager.getTestDirectoryTestLogging(this.subDir),
					R.raw.readme_rectangle_test);
		}

		// Get the general settings
		this.timePerRectangle = ((RectangleTestGeneralSettings) this.settings)
				.getTimePerRectangle(getActivity());
		this.playTone = ((RectangleTestGeneralSettings) this.settings)
				.isPlayTone(getActivity());
		this.pauseBetweenRectangles = ((RectangleTestGeneralSettings) this.settings)
				.getPauseBetweenRectangles(getActivity());
		this.playMusic = ((RectangleTestGeneralSettings) this.settings)
				.isPlayMusic(getActivity());
		this.musicFile = new File(
				((RectangleTestGeneralSettings) this.settings)
						.getMusicFilePath(getActivity()));

		// Get the test specific settings
		if (this.testType == TestType.RECTANGLE_SINGLE_TEST) {
			// Add one profile depending on the settings
			int rows = ((RectangleTestSingleSettings) this.settings)
					.getNumberOfRows(getActivity());
			int cols = ((RectangleTestSingleSettings) this.settings)
					.getNumberOfColumns(getActivity());
			this.profiles.add(new RectangleProfile(rows, cols));

			// In the single test we always want to loop
			this.loopSequence = true;
			// The time of a sequence depends on the number of rows/cols as well
			// as the pause and time
			this.timePerSequence = (this.timePerRectangle + this.pauseBetweenRectangles)
					* rows * cols;
		} else if (this.testType == TestType.RECTANGLE_SEQUENCE_TEST) {
			// Add serveral profiles
			this.profiles.add(new RectangleProfile(1, 2));
			this.profiles.add(new RectangleProfile(2, 1));
			this.profiles.add(new RectangleProfile(2, 2));
			this.profiles.add(new RectangleProfile(3, 3));
			this.profiles.add(new RectangleProfile(4, 4));

			// Sequence settings
			this.loopSequence = false;
			this.timePerSequence = ((RectangleTestSequenceSettings) this.settings)
					.getTimePerSequence(getActivity());
		}

		// Shuffle the profiles
		Collections.shuffle(this.profiles, new Random(System.nanoTime()));

		// Get the tone generator and the media player
		this.toneGenerator = new ToneGenerator(
				AudioManager.STREAM_NOTIFICATION, 100);
		this.mediaPlayer = new MediaPlayer();

		// Only load the file if we want to play music
		if (this.playMusic) {
			this.mediaPlayer.setLooping(true);
			try {
				this.mediaPlayer
						.setDataSource(this.musicFile.getAbsolutePath());
				this.mediaPlayer.prepare();
				this.mediaPlayer.setDisplay(null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.containerView = container;

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		this.controlRunnable = new RectangleControlRunnable(this.containerView,
				getActivity(), this.handler, this.profiles, this.loopSequence,
				this.timePerSequence, this.timePerRectangle,
				this.pauseBetweenRectangles, this.toneGenerator, this.playTone,
				this.mediaPlayer, this.playMusic, this);
		this.handler.post(this.controlRunnable);
	}

	@Override
	public void onStop() {
		super.onStop();
		this.handler.removeCallbacksAndMessages(null);
		this.controlRunnable.stopRunnable();
	}

	@Override
	public void onTestStarted() {
		// Create the logobject
		String deviceModel = Device.getDeviceModel();
		String deviceOrientation = GazeTrackingPreferences
				.getDeviceOrientation(getActivity());
		int[] deviceParams = Device.getDeviceParameters(getActivity());

		// Create a new log object
		this.logObject = new LogObjectRectangleTest(this.testType.name(),
				deviceModel, deviceOrientation, deviceParams[1],
				deviceParams[0], this.testSubjectAbbreviation,
				System.currentTimeMillis(), this.timePerSequence,
				this.timePerRectangle, this.pauseBetweenRectangles,
				this.playMusic, this.playTone);
	}

	@Override
	public void onSequenceStarted(int numberOfRows, int numberOfCols,
			long startTime) {
		LogObjectRectangleSequence sequence = new LogObjectRectangleSequence(
				numberOfRows, numberOfCols, startTime);
		this.logObject.setSequence(sequence);
	}

	@Override
	public void onRectangleStarted(int topLeftX, int topLeftY,
			int bottomRightX, int bottomRightY, int row, int col, long startTime) {
		// Get the absolute current position on the screen of the view
		int[] array = new int[2];
		this.containerView.getLocationOnScreen(array);

		// Add the the offset of the absolute position to the corners
		int absoluteTopLeftX = topLeftX + array[0];
		int absoluteTopLeftY = topLeftY + array[1];
		int absoluteBottomRightX = bottomRightX + array[0];
		int absoluteBottomRightY = bottomRightY + array[1];

		LogObjectRectangle rectangle = new LogObjectRectangle(absoluteTopLeftX,
				absoluteTopLeftY, absoluteBottomRightX, absoluteBottomRightY,
				row, col, startTime);
		this.logObject.getSequence().addRectangle(rectangle);
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
				if (!RectangleTestFragment.this.demoModus) {
					TestLogger.storeTestData(directory, fileName, object);
				}
			}
		}).start();
	}
}