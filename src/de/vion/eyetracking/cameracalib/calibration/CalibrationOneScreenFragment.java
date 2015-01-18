package de.vion.eyetracking.cameracalib.calibration;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import de.vion.eyetracking.R;
import de.vion.eyetracking.cameracalib.GazeView;
import de.vion.eyetracking.cameracalib.calibration.callback.CalibrationCallback;

/**
 * 
 * The fragment what we use if we only have one screen
 * 
 * @author André Pomp
 * 
 */
public class CalibrationOneScreenFragment extends Fragment implements
		CvCameraViewListener2, OnTouchListener {

	private GazeView gazeView;
	private CalibrationCallback callback;
	private ImageButton btnStop;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.callback = (CalibrationCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement CalibrationCallback");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(
				R.layout.calibration_fragment_one_screen, container, false);
		this.gazeView = (GazeView) rootView
				.findViewById(R.id.calibration_one_screen_fragment_gaze_view);
		this.gazeView.setVisibility(View.VISIBLE);
		this.gazeView.setCvCameraViewListener(this);
		this.gazeView.enableView();
		this.gazeView.setOnTouchListener(this);

		this.btnStop = (ImageButton) rootView
				.findViewById(R.id.calibration_one_screen_fragment_btn_stop);
		this.btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CalibrationOneScreenFragment.this.callback.onFinishedPressed();
			}
		});

		return rootView;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (this.gazeView != null) {
			this.gazeView.disableView();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this.gazeView != null) {
			this.gazeView.disableView();
		}
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.callback.onTouch();
		return false;
	}
}