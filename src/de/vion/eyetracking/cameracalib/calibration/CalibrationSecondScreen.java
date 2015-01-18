package de.vion.eyetracking.cameracalib.calibration;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import de.vion.eyetracking.R;
import de.vion.eyetracking.cameracalib.GazeView;
import de.vion.eyetracking.cameracalib.calibration.callback.CalibrationCallback;

/**
 * 
 * The fragment what we use for the second screen
 * 
 * @author André Pomp
 * 
 */
public class CalibrationSecondScreen extends Presentation implements
		CvCameraViewListener2 {

	private GazeView gazeView;
	private CalibrationCallback callback;

	public CalibrationSecondScreen(Context outerContext, Display display,
			CalibrationCallback callback) {
		super(outerContext, display);
		this.callback = callback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calibration_second_screen);

		this.gazeView = (GazeView) findViewById(R.id.calibration_two_screen_fragment_screen2_gaze_view);
		this.gazeView.setVisibility(View.VISIBLE);
		this.gazeView.setCvCameraViewListener(this);
		this.gazeView.enableView();

	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// delegrate the init to the activity
		this.callback.onInit(width, height);
	}

	@Override
	public void onCameraViewStopped() {
		// do nothing
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat rgbaFrame = inputFrame.rgba();
		Mat grayFrame = inputFrame.gray();

		this.callback.onFrameReceived(rgbaFrame, grayFrame);

		return rgbaFrame;
	}

	public void disableView() {
		if (this.gazeView != null) {
			this.gazeView.disableView();
		}
	}
}
