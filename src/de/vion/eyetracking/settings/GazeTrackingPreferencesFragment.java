package de.vion.eyetracking.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import de.vion.eyetracking.R;

/**
 * 
 * The fragment for the preferences
 * 
 * @author André Pomp
 * 
 */
public class GazeTrackingPreferencesFragment extends PreferenceFragment {

	// TODO Reset Calibration Value of Camera Resolution is changed

	// FILENAME
	public static final String FILE_NAME = "GENERAL_SETTINGS";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager manager = getPreferenceManager();

		// use the name of the enum as preference type (this name is unique)
		manager.setSharedPreferencesName(FILE_NAME);

		addPreferencesFromResource(R.xml.general_preferences);
	}
}