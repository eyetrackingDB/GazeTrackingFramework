package de.vion.eyetracking;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import de.vion.eyetracking.testframework.TestType;

/**
 * 
 * The Fragment for showing the test specific settings
 * 
 * @author André Pomp
 * 
 */
public class TestSettingsFragment extends PreferenceFragment {

	private static final String ARGS_TESTTYPE = "ARGS_TESTTYPE";

	public static TestSettingsFragment getInstance(TestType testType) {
		TestSettingsFragment fragment = new TestSettingsFragment();

		Bundle args = new Bundle();
		args.putSerializable(ARGS_TESTTYPE, testType);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TestType type = (TestType) getArguments().get(ARGS_TESTTYPE);

		PreferenceManager manager = getPreferenceManager();

		// use the name of the enum as preference type (this name is unique)
		manager.setSharedPreferencesName(type.name());

		// Add the general preferences which are the same for all settings
		addPreferencesFromResource(R.xml.generic_preferences);

		// Add the preferences which are only for this test
		for (int settings : type.getSettingsResources()) {
			addPreferencesFromResource(settings);
		}
	}
}