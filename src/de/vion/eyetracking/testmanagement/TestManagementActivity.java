package de.vion.eyetracking.testmanagement;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import de.vion.eyetracking.settings.GazeTrackingPreferences;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testmanagement.detail.TestManagementFragmentDetail;
import de.vion.eyetracking.testmanagement.list.TestManagementFragmentList;
import de.vion.eyetracking.testmanagement.list.TestManagementListItemCallback;

/**
 * 
 * The TestManagement Activity that shows the list of tests or the test details
 * 
 * @author André Pomp
 * 
 */
public class TestManagementActivity extends Activity implements
		TestManagementListItemCallback {

	private static final String TAG_FRAGMENT_DETAIL = "TAG_FRAGMENT_DETAIL";
	private static final String TAG_FRAGMENT_LIST = "TAG_FRAGMENT_LIST";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the correct orientation
		setRequestedOrientation(GazeTrackingPreferences
				.getDeviceOrientationAsActivityInfo(this));

		// Init the test management storage
		TestManagementStorage.initStorage();

		// If the activity is started for the first time, we add the list
		// fragment
		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.replace(android.R.id.content,
							new TestManagementFragmentList(), TAG_FRAGMENT_LIST)
					.commit();
		}
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = getFragmentManager().findFragmentByTag(
				TAG_FRAGMENT_DETAIL);
		if (fragment != null) {
			// if the detail fragment was active we want to show the list
			// fragment again
			getFragmentManager()
					.beginTransaction()
					.replace(android.R.id.content,
							new TestManagementFragmentList(), TAG_FRAGMENT_LIST)
					.commit();
			return;
		}
		// if we get here the list fragment was active
		super.onBackPressed();
	}

	@Override
	public void onTestItemClicked(TestType testtype, String directory) {
		Fragment fragment = TestManagementFragmentDetail.getInstance(testtype,
				directory);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment, TAG_FRAGMENT_DETAIL)
				.commit();
	}
}