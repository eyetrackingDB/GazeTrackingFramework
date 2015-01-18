package de.vion.eyetracking.testframework.tests.line;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.vion.eyetracking.R;
import de.vion.eyetracking.TestActivity;
import de.vion.eyetracking.misc.Device;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.settings.GazeTrackingPreferences;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestFragment;
import de.vion.eyetracking.testframework.logging.TestLogger;
import de.vion.eyetracking.testframework.tests.line.logging.LogObjectLine;
import de.vion.eyetracking.testframework.tests.line.logging.LogObjectLineCallback;
import de.vion.eyetracking.testframework.tests.line.logging.LogObjectLineTest;
import de.vion.eyetracking.testframework.tests.line.runnable.LineTestRunnable;
import de.vion.eyetracking.testframework.tests.line.settings.LineTestGeneralSettings;
import de.vion.eyetracking.testframework.tests.line.view.LineTestView;

/**
 * The Fragment for the line test
 * 
 * @author André Pomp
 * 
 */
public class LineTestFragment extends GenericTestFragment implements
		LogObjectLineCallback {

	public static LineTestFragment createInstance(TestType type,
			String abbreviation, String subdir, boolean demo) {
		LineTestFragment fragment = new LineTestFragment();
		fragment.setArguments(createArgs(type, abbreviation, subdir, demo));
		return fragment;
	}

	// The current view and its parent
	private View rootView;
	private ViewGroup containerView;
	private LineTestView lineTestview;

	// Get the Settings
	private int numberOfLines;
	private int velocity;
	private int numberOfRepetitions;

	private int currentRepetition = 1;

	// The current control runnable
	private LineTestRunnable lineTestRunnable;

	// Logging
	private LogObjectLineTest logObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!this.demoModus) {
			FileManager.writeRessourceToSD(getActivity(),
					FileManager.getTestDirectoryTestLogging(this.subDir),
					R.raw.readme_line_test);
		}

		// Get the general settings
		this.numberOfLines = ((LineTestGeneralSettings) this.settings)
				.getNumberOfLines(getActivity());
		this.velocity = ((LineTestGeneralSettings) this.settings)
				.getVelocity(getActivity());
		this.numberOfRepetitions = ((LineTestGeneralSettings) this.settings)
				.getNumberOfRepetitions(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.containerView = container;

		this.rootView = inflater.inflate(R.layout.test_fragment_line,
				container, false);
		this.lineTestview = (LineTestView) this.rootView
				.findViewById(R.id.test_fragment_line_view);
		this.lineTestview.setValues(this, this.numberOfLines, this.velocity);

		return this.rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		this.lineTestRunnable = new LineTestRunnable(this, this);
		new Thread(this.lineTestRunnable).start();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (this.lineTestRunnable != null) {
			this.lineTestRunnable.stopRunning();
		}
	}

	public void updatePosition() {
		if (this.lineTestview.getCurrentNumberOfLines() > this.numberOfLines) {
			if (this.numberOfRepetitions == this.currentRepetition) {
				this.lineTestRunnable.stopRunning();
			} else {
				this.lineTestview.resetBallPosition();
				this.currentRepetition++;
			}
		} else {
			this.lineTestview.updateBallPosition();
		}
	}

	@Override
	public void onTestStarted() {
		// Create the logobject
		String deviceModel = Device.getDeviceModel();
		String deviceOrientation = GazeTrackingPreferences
				.getDeviceOrientation(getActivity());
		int[] deviceParams = Device.getDeviceParameters(getActivity());

		// Create a new log object
		this.logObject = new LogObjectLineTest(this.testType.name(),
				deviceModel, deviceOrientation, deviceParams[1],
				deviceParams[0], this.testSubjectAbbreviation,
				System.currentTimeMillis(), this.velocity, this.numberOfLines,
				this.numberOfRepetitions);
	}

	@Override
	public void onPositionUpdated(long time, int centerY, int lineNumber) {
		int[] array = new int[2];
		this.containerView.getLocationOnScreen(array);

		// Add the the offset of the absolute position to the corners
		int absoluteCenterY = centerY + array[1];

		LogObjectLine line = new LogObjectLine(absoluteCenterY, lineNumber,
				time);
		this.logObject.getLines().add(line);
	}

	@Override
	public void onTestFinished() {
		final String fileName = this.testType.name() + "_"
				+ this.testSubjectAbbreviation + "_"
				+ System.currentTimeMillis() + ".json";

		final String directory = FileManager
				.getTestDirectoryTestLogging(this.subDir);
		final JSONObject object = this.logObject.getJSONObject();

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!LineTestFragment.this.demoModus) {
					TestLogger.storeTestData(directory, fileName, object);
				}
				if (getActivity() != null) {
					((TestActivity) getActivity()).onTestFinished();
				}
			}
		}).start();
	}
}