package de.vion.eyetracking;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import de.vion.eyetracking.adapter.HomeAdapter;
import de.vion.eyetracking.cameracalib.CalibrationMainActivity;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.settings.GazeTrackingPreferences;
import de.vion.eyetracking.settings.GazeTrackingPreferencesActivity;
import de.vion.eyetracking.testframework.TestFactory;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;
import de.vion.eyetracking.testmanagement.TestManagementActivity;

/**
 * This class is the Main Activity of the Application. It allows to: 1) Check if
 * OpenCV Manager is installed 2) Choose a test and its settings 3) Check if
 * camera is calibrated and access the camera calibration 4) Check if settings
 * and abbreviation are valid before accessing the test
 * 
 * @author André Pomp
 * 
 */
public class HomeActivity extends Activity {

	private EditText etAbb;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				System.loadLibrary("eyetab_tracker");

				// Check if the camera is calibrated
				if (!GazeTrackingPreferences.isCalibrated(HomeActivity.this)) {
					showNotCalibratedDialog();
					return;
				}
				break;
			default:
				super.onManagerConnected(status);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// Hide the keyboard on start
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Get the EditText for the Text Abbreviation
		this.etAbb = (EditText) findViewById(R.id.activity_home_et_abb);

		// Create the adapter and list-view for the test overview
		HomeAdapter testAdapter = new HomeAdapter(this, R.layout.home_list_item);
		ListView lvTests = (ListView) this
				.findViewById(R.id.activity_home_lv_tests);

		// Set the listener for starting the test activity
		lvTests.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Get the current test type
				TestType testType = (TestType) (arg0.getAdapter().getItem(arg2));
				GenericTestSettings settings = TestFactory
						.getTestSettings(testType);
				if (settings.isValid(HomeActivity.this)) {
					Intent intent = new Intent(HomeActivity.this,
							TestActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.putExtra(TestActivity.INTENT_START, true);
					intent.putExtra(TestActivity.INTENT_TEST_TYPE, testType);
					intent.putExtra(TestActivity.INTENT_TEST_ABB,
							HomeActivity.this.etAbb.getText().toString());
					startActivity(intent);
				}
			}
		});
		// Set the ListView adapter
		lvTests.setAdapter(testAdapter);

		// Create the main directory (only if it does not exist)
		FileManager.createDirectoryStructure();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(GazeTrackingPreferences
				.getDeviceOrientationAsActivityInfo(this));
		// Check if openCV is available
		System.loadLibrary("gnustl_shared");
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
				this.mLoaderCallback);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_actvity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_camera_calib:
			startActivity(new Intent(this, CalibrationMainActivity.class));
			return true;
		case R.id.action_gaze_tracking_settings:
			startActivity(new Intent(this,
					GazeTrackingPreferencesActivity.class));
			return true;
		case R.id.action_general_postproc:
			startActivity(new Intent(this, TestManagementActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showNotCalibratedDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setTitle(getString(R.string.home_activity_dialog_camera_not_calibrated_title));
		alertDialogBuilder
				.setMessage(
						getString(R.string.home_activity_dialog_camera_not_calibrated_message))
				.setCancelable(false)
				.setPositiveButton(
						getString(R.string.home_activity_dialog_camera_not_calibrated_btn_positive),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								finish();
							}
						})
				.setNegativeButton(
						getString(R.string.home_activity_dialog_camera_not_calibrated_btn_negative),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								startActivity(new Intent(HomeActivity.this,
										CalibrationMainActivity.class));
							}
						});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}