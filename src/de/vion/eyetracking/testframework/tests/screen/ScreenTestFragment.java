package de.vion.eyetracking.testframework.tests.screen;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.vion.eyetracking.R;
import de.vion.eyetracking.TestActivity;
import de.vion.eyetracking.misc.Device;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.settings.GazeTrackingPreferences;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestFragment;
import de.vion.eyetracking.testframework.logging.TestLogger;
import de.vion.eyetracking.testframework.tests.screen.logging.LogObjectScreenAction;
import de.vion.eyetracking.testframework.tests.screen.logging.LogObjectScreenCallback;
import de.vion.eyetracking.testframework.tests.screen.logging.LogObjectScreenTest;
import de.vion.eyetracking.testframework.tests.screen.runnable.ScreenTestRunnable;
import de.vion.eyetracking.testframework.tests.screen.settings.ScreenTestGeneralSettings;

/**
 * The Fragment for the second screen test.
 * 
 * @author André Pomp
 * 
 */
public class ScreenTestFragment extends GenericTestFragment implements
		LogObjectScreenCallback {

	public static ScreenTestFragment createInstance(TestType type,
			String abbreviation, String subdir, boolean demo) {
		ScreenTestFragment fragment = new ScreenTestFragment();
		fragment.setArguments(createArgs(type, abbreviation, subdir, demo));
		return fragment;
	}

	// The current view and its parent
	private View rootView;
	private TextView textView;

	// Get the Settings
	private int timeOnTablet;
	private int timeBesideTablet;
	private int numberOfRepetitions;

	// The current control runnable
	private ScreenTestRunnable screenTestRunnable;

	// Logging
	private LogObjectScreenTest logObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!this.demoModus) {
			FileManager.writeRessourceToSD(getActivity(),
					FileManager.getTestDirectoryTestLogging(this.subDir),
					R.raw.readme_screen_test);
		}

		// Get the general settings
		this.timeOnTablet = ((ScreenTestGeneralSettings) this.settings)
				.getTimeOnTablet(getActivity());
		this.timeBesideTablet = ((ScreenTestGeneralSettings) this.settings)
				.getTimeBesideTablet(getActivity());
		this.numberOfRepetitions = ((ScreenTestGeneralSettings) this.settings)
				.getNumberOfRepetitions(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.rootView = inflater.inflate(R.layout.test_fragment_screen,
				container, false);
		this.textView = (TextView) this.rootView
				.findViewById(R.id.test_fragment_screen_tv);

		return this.rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		this.screenTestRunnable = new ScreenTestRunnable(this.timeOnTablet,
				this.timeBesideTablet, this.numberOfRepetitions, this, this);
		new Thread(this.screenTestRunnable).start();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (this.screenTestRunnable != null) {
			this.screenTestRunnable.stopRunning();
		}
	}

	public void updateAction(String action) {
		this.textView.setText(action);
	}

	@Override
	public void onTestStarted() {
		// Create the logobject
		String deviceModel = Device.getDeviceModel();
		String deviceOrientation = GazeTrackingPreferences
				.getDeviceOrientation(getActivity());
		int[] deviceParams = Device.getDeviceParameters(getActivity());

		// Create a new log object
		this.logObject = new LogObjectScreenTest(this.testType.name(),
				deviceModel, deviceOrientation, deviceParams[1],
				deviceParams[0], this.testSubjectAbbreviation,
				System.currentTimeMillis(), this.timeOnTablet,
				this.timeBesideTablet, this.numberOfRepetitions);
	}

	@Override
	public void onActionChanged(long time, String action) {
		LogObjectScreenAction logAction = new LogObjectScreenAction(action,
				time);
		this.logObject.getActions().add(logAction);
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
				if (!ScreenTestFragment.this.demoModus) {
					TestLogger.storeTestData(directory, fileName, object);
				}
				if (getActivity() != null) {
					((TestActivity) getActivity()).onTestFinished();
				}
			}
		}).start();
	}
}