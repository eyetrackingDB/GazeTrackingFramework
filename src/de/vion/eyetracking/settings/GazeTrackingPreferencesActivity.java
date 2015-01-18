package de.vion.eyetracking.settings;

import android.app.Activity;
import android.os.Bundle;

/**
 * 
 * The activtiy for the preferences
 * 
 * @author André Pomp
 * 
 */
public class GazeTrackingPreferencesActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(GazeTrackingPreferences
				.getDeviceOrientationAsActivityInfo(this));

		// Show the fragment once
		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.replace(android.R.id.content,
							new GazeTrackingPreferencesFragment()).commit();
		}
	}
}