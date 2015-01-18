package de.vion.eyetracking.windowmanager;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import de.vion.eyetracking.R;
import de.vion.eyetracking.callback.WindowManagerCallback;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerAbstract;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerFactory;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerSettingsAbstract;
import de.vion.eyetracking.gazedetection.live.LiveProcessingDrawView;
import de.vion.eyetracking.gazedetection.live.LiveProcessingView;

/**
 * 
 * Manages the window for the live gaze tracking
 * 
 * @author André Pomp
 * 
 */
public class LiveGazeWindowManager implements CvCameraViewListener2 {

	private static final int GAZE_LIVE_VIEW_WINDOW_SIZE = 1;
	private static final int GAZE_LIVE_DRAW_WINDOW_SIZE = LayoutParams.MATCH_PARENT;

	private WindowManager windowManager;
	private Context context;

	private LiveProcessingView gazeLiveView;
	private LiveProcessingDrawView gazeDrawView;
	private WindowManagerCallback callback;

	private GazeTrackerAbstract tracker;
	private GazeTrackerSettingsAbstract settings;
	private Mat colorFrame;

	public LiveGazeWindowManager(Context context, WindowManagerCallback callback) {
		super();
		this.context = context;
		this.callback = callback;
		this.windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
	}

	public void initViews() {
		// Setup the surface view
		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.gazeLiveView = (LiveProcessingView) inflater.inflate(
				R.layout.view_gaze_live, null);
		this.gazeLiveView.setVisibility(View.VISIBLE);
		this.gazeLiveView.setCvCameraViewListener(this);
		this.gazeLiveView.enableView();

		final WindowManager.LayoutParams cameraViewParams = new WindowManager.LayoutParams(
				GAZE_LIVE_VIEW_WINDOW_SIZE, GAZE_LIVE_VIEW_WINDOW_SIZE,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		cameraViewParams.gravity = Gravity.TOP | Gravity.LEFT;
		cameraViewParams.x = 0;
		cameraViewParams.y = 0;

		this.gazeDrawView = new LiveProcessingDrawView(this.context);
		this.gazeDrawView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		final WindowManager.LayoutParams gazeViewParams = new WindowManager.LayoutParams(
				GAZE_LIVE_DRAW_WINDOW_SIZE, GAZE_LIVE_DRAW_WINDOW_SIZE,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSLUCENT);
		gazeViewParams.gravity = Gravity.TOP | Gravity.LEFT;
		gazeViewParams.x = 0;
		gazeViewParams.y = 0;

		// Add the view
		this.windowManager.addView(this.gazeDrawView, gazeViewParams);
		this.windowManager.addView(this.gazeLiveView, cameraViewParams);
	}

	// Init the GazeTracker

	public void startGazeTracker() {
		this.tracker = GazeTrackerFactory.createGazeTracker(this.context);
		this.settings = GazeTrackerFactory.createGazeTrackerSettings();
		this.settings.loadSettingsFromPreferences(this.context);
		this.tracker.init(this.settings);
	}

	public void stopGazeTracker() {
		if (this.gazeLiveView != null) {
			this.gazeLiveView.disableView();
		}
		this.tracker.release();
		this.windowManager.removeView(this.gazeLiveView);
		this.windowManager.removeView(this.gazeDrawView);
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		this.colorFrame = new Mat(height, width, CvType.CV_8UC3);
		this.callback.onMainWindowFinished();
	}

	@Override
	public void onCameraViewStopped() {
		this.colorFrame.release();
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		this.colorFrame = inputFrame.rgba();
		int[] result = this.tracker.detect(this.colorFrame);
		if (result != null) {
			// 0 and 1 are the scaled points (x,y) (scaled for camera frame)
			// 2 and 3 are the unscaled points (x,y) ((unscaled for display)
			this.gazeDrawView.changePoint(result[2], result[3]);
			this.gazeDrawView.postInvalidate();
		}
		// do not return the edited frame, as we cannot see the result
		return inputFrame.rgba();
	}
}