package de.vion.eyetracking.testframework.tests.point;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
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
import de.vion.eyetracking.testframework.tests.point.logging.LogObjectPoint;
import de.vion.eyetracking.testframework.tests.point.logging.LogObjectPointCallback;
import de.vion.eyetracking.testframework.tests.point.logging.LogObjectPointTest;
import de.vion.eyetracking.testframework.tests.point.runnable.PointTestRunnable;
import de.vion.eyetracking.testframework.tests.point.settings.PointTestSettings;
import de.vion.eyetracking.testframework.tests.point.view.PointTestView;
import de.vion.eyetracking.testframework.tests.point.view.PointTestViewCreatedCallback;

/**
 * The Fragment for the point test
 * 
 * @author André Pomp
 * 
 */
public class PointTestFragment extends GenericTestFragment implements
		LogObjectPointCallback, PointTestViewCreatedCallback {

	public static PointTestFragment createInstance(TestType type,
			String abbreviation, String subdir, boolean demo) {
		PointTestFragment fragment = new PointTestFragment();
		fragment.setArguments(createArgs(type, abbreviation, subdir, demo));
		return fragment;
	}

	// The current view and its parent
	private View rootView;
	private ViewGroup containerView;
	private PointTestView pointTestView;

	// Get the Settings
	private int timePerPoint = 0;
	private int numberOfRows = 0;
	private int numberOfCols = 0;
	private int numberOfRepetitions = 0;

	// Handler for handling the current sequence timing
	private Handler handler = new Handler();

	// Logging
	private LogObjectPointTest logObject;

	private PointTestRunnable pointTestRunnable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!this.demoModus) {
			FileManager.writeRessourceToSD(getActivity(),
					FileManager.getTestDirectoryTestLogging(this.subDir),
					R.raw.readme_point_test);
		}

		// Get the general settings
		this.timePerPoint = ((PointTestSettings) this.settings)
				.getTimePerPoint(getActivity());
		this.numberOfRows = ((PointTestSettings) this.settings)
				.getNumberOfRows(getActivity());
		this.numberOfCols = ((PointTestSettings) this.settings)
				.getNumberOfColumns(getActivity());
		this.numberOfRepetitions = ((PointTestSettings) this.settings)
				.getNumberOfRepetitions(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.containerView = container;

		this.rootView = inflater.inflate(R.layout.test_fragment_point,
				container, false);
		this.pointTestView = (PointTestView) this.rootView
				.findViewById(R.id.test_fragment_point_view);
		this.pointTestView.setPoints(this.numberOfRows, this.numberOfCols);
		this.pointTestView.setCallback(this);

		return this.rootView;
	}

	@Override
	public void onStop() {
		super.onStop();
		if (this.pointTestRunnable != null) {
			this.pointTestRunnable.stopRunning();
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
		this.logObject = new LogObjectPointTest(this.testType.name(),
				deviceModel, deviceOrientation, deviceParams[1],
				deviceParams[0], this.testSubjectAbbreviation,
				System.currentTimeMillis(), this.timePerPoint,
				this.numberOfRows, this.numberOfCols, this.numberOfRepetitions);
	}

	@Override
	public void onPointStarted(int centerX, int centerY, long startTime) {
		int[] array = new int[2];
		this.containerView.getLocationOnScreen(array);

		// Add the the offset of the absolute position to the corners
		int absoluteTopLeftX = centerX + array[0];
		int absoluteTopLeftY = centerY + array[1];

		LogObjectPoint point = new LogObjectPoint(absoluteTopLeftX,
				absoluteTopLeftY, startTime);
		this.logObject.addPoint(point);

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
				if (!PointTestFragment.this.demoModus) {
					TestLogger.storeTestData(directory, fileName, object);
				}
				if (getActivity() != null) {
					((TestActivity) getActivity()).onTestFinished();
				}

			}
		}).start();
	}

	@Override
	public void pointsCreated() {
		super.onStart();
		this.pointTestRunnable = new PointTestRunnable(this.pointTestView,
				this, this.handler, this.timePerPoint, this.numberOfRows
						* this.numberOfCols, this.numberOfRepetitions);
		new Thread(this.pointTestRunnable).start();
	}
}