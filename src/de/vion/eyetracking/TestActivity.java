package de.vion.eyetracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import de.vion.eyetracking.callback.TestFragmentInitialCallback;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerFactory;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerSettingsAbstract;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.misc.Toaster;
import de.vion.eyetracking.settings.GazeTrackingPreferences;
import de.vion.eyetracking.testframework.TestFactory;
import de.vion.eyetracking.testframework.TestType;

/**
 * 
 * The Activity that is called if we start a Test.
 * 
 * @author André Pomp
 * 
 */
public class TestActivity extends Activity implements
		TestFragmentInitialCallback {

	public static final String INTENT_START = "start";
	public static final String INTENT_TEST_TYPE = "test_type";
	public static final String INTENT_TEST_ABB = "test_abb";

	private enum TestState {
		MODE_NONE, MODE_ALIGN, MODE_DEMO, MODE_TEST
	}

	// The current state of this activity
	private TestState state = TestState.MODE_NONE;

	// Test specific values
	private TestType type;
	private String testSubjectAbb;
	private String subdir = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Fix orientation based on settings
		setRequestedOrientation(GazeTrackingPreferences
				.getDeviceOrientationAsActivityInfo(this));

		// Check if we directly finish the activity because we want to stop
		if (!getIntent().getExtras().getBoolean(INTENT_START)) {
			if (this.state == TestState.MODE_DEMO) {
				chooseFragment(TestState.MODE_NONE);
			} else {
				finish();
			}

			return;
		}

		// Keep the screen on (will not work for live view test)
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Get the intent values
		this.type = (TestType) getIntent().getExtras().getSerializable(
				INTENT_TEST_TYPE);
		this.testSubjectAbb = getIntent().getExtras()
				.getString(INTENT_TEST_ABB);

		// Set the fragment based on the current state
		chooseFragment(this.state);
	}

	@Override
	protected void onPause() {
		super.onPause();
		switch (this.state) {
		case MODE_NONE:
		case MODE_ALIGN:
			// do nothing
			break;
		case MODE_DEMO:
			if (this.type != TestType.LIVE_VIEW_TEST) {
				// stop the demo mode => otherwise results are violated
				sendBroadcast(new Intent(
						RecordService.RECEIVER_INTENT_ACTIVITY_STOP));
				chooseFragment(TestState.MODE_NONE);
			}
			break;
		case MODE_TEST:
			if (this.type != TestType.LIVE_VIEW_TEST) {
				// stop the test => otherwise results are violated
				sendBroadcast(new Intent(
						RecordService.RECEIVER_INTENT_ACTIVITY_STOP));
			}
			break;
		}
	}

	private void chooseFragment(TestState state) {
		this.state = state;
		switch (this.state) {
		case MODE_NONE:
			getFragmentManager()
					.beginTransaction()
					.replace(
							android.R.id.content,
							TestFragmentInitial
									.getInstance(this.testSubjectAbb)).commit();
			break;
		case MODE_ALIGN:
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new TestFragmentAlign())
					.commit();
			break;
		case MODE_DEMO:
			startRecordService(true);
			break;
		case MODE_TEST:
			if (isAbbrevValid()) {
				startRecordService(false);
			} else {
				this.state = TestState.MODE_NONE;
				Toaster.makeToast(this,
						R.string.test_activity_toast_error_invalid_abbreviation);
			}
			break;
		}
	}

	private void startRecordService(final boolean demoModus) {
		// Only create the directory if we are not in demo modus
		if (!demoModus) {
			this.subdir = FileManager.createTestDirectory(this.type,
					this.testSubjectAbb);
			FileManager.writeRessourceToSD(this, this.subdir, R.raw.readme);
			GazeTrackerSettingsAbstract settings = GazeTrackerFactory
					.createGazeTrackerSettings();
			settings.storeSettings(this.subdir, this);
		}

		Intent intent = new Intent(this, RecordService.class);
		intent.putExtra(RecordService.INTENT_ARG_TEST_TYPE, this.type);
		intent.putExtra(RecordService.INTENT_ARG_SUB_DIR, this.subdir);
		intent.putExtra(RecordService.INTENT_ARG_DEMO_MODUS, demoModus);

		startService(intent);
		// Starting the service takes some time => delay the starting a bit
		final ProgressDialog loadingTestDialog = ProgressDialog
				.show(TestActivity.this,
						getString(R.string.test_activity_dialog_progress_title),
						getString(R.string.test_activity_dialog_progress_message),
						true);
		loadingTestDialog.setCancelable(false);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				loadingTestDialog.dismiss();
				getFragmentManager()
						.beginTransaction()
						.replace(
								android.R.id.content,
								TestFactory.getTestFragment(
										TestActivity.this.type,
										TestActivity.this.testSubjectAbb,
										TestActivity.this.subdir, demoModus))
						.commit();
			}
		}, 2000);
	}

	@Override
	public void onBackPressed() {
		switch (this.state) {
		case MODE_NONE:
			// Go to the previous activity
			super.onBackPressed();
			break;
		case MODE_ALIGN:
			chooseFragment(TestState.MODE_NONE);
			break;
		case MODE_DEMO:
			sendBroadcast(new Intent(
					RecordService.RECEIVER_INTENT_ACTIVITY_STOP));
			chooseFragment(TestState.MODE_NONE);
			break;
		case MODE_TEST:
			// Show the stop dialog
			showStopDialog();
			break;
		}
	}

	public void onTestFinished() {
		switch (this.state) {
		case MODE_DEMO:
			sendBroadcast(new Intent(
					RecordService.RECEIVER_INTENT_ACTIVITY_STOP));
			chooseFragment(TestState.MODE_NONE);
			break;
		case MODE_TEST:
			// Show the stop dialog
			sendBroadcast(new Intent(
					RecordService.RECEIVER_INTENT_ACTIVITY_STOP));
			break;
		default:
			// nothing
		}
	}

	private void showStopDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setTitle(getString(R.string.test_activity_dialog_close_title));
		alertDialogBuilder
				.setMessage(
						getString(R.string.test_activity_dialog_close_message))
				.setCancelable(false)
				.setPositiveButton(
						getString(R.string.test_activity_dialog_close_btn_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								sendBroadcast(new Intent(
										RecordService.RECEIVER_INTENT_ACTIVITY_STOP));
							}
						})
				.setNegativeButton(
						(getString(R.string.test_activity_dialog_close_btn_no)),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override
	public void onStartClicked() {
		chooseFragment(TestState.MODE_TEST);
	}

	@Override
	public void onShowDemoClicked() {
		chooseFragment(TestState.MODE_DEMO);
	}

	@Override
	public void onAlignFaceClicked() {
		chooseFragment(TestState.MODE_ALIGN);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// Get the intent values
		if (!intent.getExtras().getBoolean(INTENT_START)) {
			finish();
		} else {
			chooseFragment(TestState.MODE_NONE);
		}
	}

	private boolean isAbbrevValid() {
		if (this.testSubjectAbb == null || this.testSubjectAbb.isEmpty()) {
			return false;
		}
		return true;
	}

}