package de.vion.eyetracking.cameracalib.calibration;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import de.vion.eyetracking.R;
import de.vion.eyetracking.cameracalib.calibration.callback.CalibrationCallback;

/**
 * 
 * The fragment what we use if we have two screens
 * 
 * @author André Pomp
 * 
 */
public class CalibrationTwoScreenFragment extends Fragment implements
		OnTouchListener {

	private CalibrationSecondScreen secondScreen;
	private CalibrationCallback callback;
	private Button btnStop;

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
		// Init the first screen
		View rootViewScreen1 = inflater.inflate(
				R.layout.calibration_fragment_two_screen, container, false);
		this.btnStop = (Button) rootViewScreen1
				.findViewById(R.id.calibration_two_screen_fragment_screen1_btn_stop);
		this.btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CalibrationTwoScreenFragment.this.callback.onFinishedPressed();
			}
		});
		rootViewScreen1.setOnTouchListener(this);

		// Get the display manager and choose a display
		DisplayManager displayManager = (DisplayManager) getActivity()
				.getSystemService(Context.DISPLAY_SERVICE);
		Display[] presentationDisplays = displayManager
				.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
		if (presentationDisplays.length > 0) {
			this.secondScreen = new CalibrationSecondScreen(getActivity(),
					presentationDisplays[0], this.callback);
			this.secondScreen.show();
		}
		return rootViewScreen1;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (this.secondScreen != null) {
			this.secondScreen.disableView();
			this.secondScreen.dismiss();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this.secondScreen != null) {
			this.secondScreen.disableView();
			this.secondScreen.dismiss();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.callback.onTouch();
		return false;
	}
}