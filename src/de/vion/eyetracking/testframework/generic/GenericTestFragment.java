package de.vion.eyetracking.testframework.generic;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import de.vion.eyetracking.testframework.TestFactory;
import de.vion.eyetracking.testframework.TestType;

/**
 * 
 * The Fragment that must be used by each new implemented test as super class.
 * It handles the flags for the immersive mode
 * 
 * @author André Pomp
 * 
 */
public abstract class GenericTestFragment extends Fragment {

	protected static final String BUNDLE_ARGS_TESTYPE = "BUNDLE_ARGS_TESTYPE";
	protected static final String BUNDLE_ARGS_ABBREVIATION = "BUNDLE_ARGS_ABBREVIATION";
	protected static final String BUNDLE_ARGS_SUBDIR = "BUNDLE_ARGS_SUBDIR";
	protected static final String BUNDLE_ARGS_DEMO = "BUNDLE_ARGS_DEMO";

	// Values from the Intent
	protected TestType testType;
	protected String testSubjectAbbreviation = "";
	protected String subDir;
	protected boolean demoModus;

	public GenericTestSettings settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.testType = (TestType) getArguments().getSerializable(
				BUNDLE_ARGS_TESTYPE);
		this.testSubjectAbbreviation = getArguments().getString(
				BUNDLE_ARGS_ABBREVIATION);
		this.subDir = getArguments().getString(BUNDLE_ARGS_SUBDIR);
		this.demoModus = getArguments().getBoolean(BUNDLE_ARGS_DEMO);
		this.settings = TestFactory.getTestSettings(this.testType);

		if (!this.demoModus) {
			// If we are not in the demo mode we create a copy of the
			// preferences in the directory
			this.settings.copySharedPreferences(getActivity(), this.subDir);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Check if we use immersive mode
		if (this.settings.isImmersiveMode(getActivity())) {
			// Hide navigation and actionbar
			final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

			final View decorView = getActivity().getWindow().getDecorView();
			decorView.setSystemUiVisibility(flags);
		}
	}

	protected static Bundle createArgs(TestType type, String abbreviation,
			String subdir, boolean demo) {
		Bundle args = new Bundle();
		args.putSerializable(BUNDLE_ARGS_TESTYPE, type);
		args.putString(BUNDLE_ARGS_ABBREVIATION, abbreviation);
		args.putString(BUNDLE_ARGS_SUBDIR, subdir);
		args.putBoolean(BUNDLE_ARGS_DEMO, demo);
		return args;
	}
}
