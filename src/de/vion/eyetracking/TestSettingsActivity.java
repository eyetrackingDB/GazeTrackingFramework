package de.vion.eyetracking;

import android.app.Activity;
import android.os.Bundle;
import de.vion.eyetracking.settings.GazeTrackingPreferences;
import de.vion.eyetracking.testframework.TestType;

/**
 * 
 * The Activity that allows us to choose the settings for a test
 * 
 * @author André Pomp
 * 
 */
public class TestSettingsActivity extends Activity {

	public static final String INTENT_TESTTYPE = "INTENT_TESTTYPE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(GazeTrackingPreferences
				.getDeviceOrientationAsActivityInfo(this));

		TestType type = (TestType) getIntent().getSerializableExtra(
				INTENT_TESTTYPE);

		// Show the fragment once
		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.replace(android.R.id.content,
							TestSettingsFragment.getInstance(type)).commit();
		}
	}
}