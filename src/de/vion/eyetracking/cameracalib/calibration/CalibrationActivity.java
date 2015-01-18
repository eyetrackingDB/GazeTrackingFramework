package de.vion.eyetracking.cameracalib.calibration;

import org.opencv.core.Mat;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.WindowManager;
import de.vion.eyetracking.R;
import de.vion.eyetracking.cameracalib.calibration.callback.CalibrationAsyncTaskCallback;
import de.vion.eyetracking.cameracalib.calibration.callback.CalibrationCallback;
import de.vion.eyetracking.cameracalib.calibration.opencv.CameraCalibrator;
import de.vion.eyetracking.misc.Toaster;

/**
 * 
 * The Activity for the camera calibration. It also handles the usage of two
 * displays.
 * 
 * @author André Pomp
 * 
 */
public class CalibrationActivity extends FragmentActivity implements
		DisplayListener, CalibrationCallback, CalibrationAsyncTaskCallback {

	// The display manager
	private DisplayManager displayManager;

	// The camera calibrator
	private CameraCalibrator calibrator;

	// The current fragment
	private Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Register the display listener and the manager
		this.displayManager = (DisplayManager) this
				.getSystemService(Context.DISPLAY_SERVICE);
		this.displayManager.registerDisplayListener(this, null);

		// Check if a presentation display is available
		Display[] presentationDisplays = this.displayManager
				.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

		if (presentationDisplays.length > 0) {
			this.fragment = new CalibrationTwoScreenFragment();
		} else {
			this.fragment = new CalibrationOneScreenFragment();
		}
		// Add the fragment
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, this.fragment).commit();
	}

	@Override
	public void onBackPressed() {
		finishCalibration();
		super.onBackPressed();
	}

	@Override
	public void onDisplayAdded(int displayId) {
		finishCalibration();
	}

	@Override
	public void onDisplayChanged(int displayId) {
		// do nothing
	}

	@Override
	public void onDisplayRemoved(int displayId) {
		finishCalibration();
	}

	@Override
	public void onInit(int width, int height) {
		this.calibrator = new CameraCalibrator(width, height);
	}

	@Override
	public void onFrameReceived(Mat colorFrame, Mat greyFrame) {
		this.calibrator.processFrame(greyFrame, colorFrame);
	}

	@Override
	public void onTouch() {
		this.calibrator.addCorners();
	}

	@Override
	public void onFinishedPressed() {
		finishCalibration();
	}

	private void finishCalibration() {
		// Remove the current fragment (it gets shut down)
		getSupportFragmentManager().beginTransaction().remove(this.fragment)
				.commit();

		// Calculate the calibration values
		calculateCalibrationValues();
	}

	private void calculateCalibrationValues() {
		// unregister the display listener
		this.displayManager.unregisterDisplayListener(this);

		if (this.calibrator.getCornersBufferSize() < 2) {
			Toaster.makeToast(
					this,
					getString(R.string.activity_tutorial_taost_error_too_less_samples));
			finish();
			return;
		}

		// Execute the calculation
		CalibrationAsyncTask task = new CalibrationAsyncTask(this,
				this.calibrator, this);
		task.execute();
	}

	@Override
	public void onTaskFinished() {
		finish();
	}
}