package de.vion.eyetracking.windowmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import de.vion.eyetracking.callback.WindowManagerCallback;
import de.vion.eyetracking.recording.CameraRecorder;

/**
 * 
 * Manages the window for the camera recording
 * 
 * @author André Pomp
 * 
 */
@SuppressWarnings("deprecation")
public class CameraRecorderWindowManager implements SurfaceHolder.Callback {

	private static final int WINDOW_SIZE = 1;

	private WindowManager windowManager;
	private Context context;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private WindowManagerCallback callback;

	// Camera
	private int frontCameraIndex = -1;
	private CameraRecorder cameraRecorder;

	public CameraRecorderWindowManager(Context context,
			WindowManagerCallback callback, String subdir) {
		super();
		this.context = context;
		this.callback = callback;
		this.windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		this.frontCameraIndex = findFrontFacingCamera();
		this.cameraRecorder = new CameraRecorder(subdir);
	}

	@SuppressLint("RtlHardcoded")
	public void initViews() {
		// Setup the surface view
		this.surfaceView = new SurfaceView(this.context);
		this.surfaceHolder = this.surfaceView.getHolder();
		this.surfaceHolder.addCallback(this);

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WINDOW_SIZE, WINDOW_SIZE,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		// Add the view
		this.windowManager.addView(this.surfaceView, params);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// callback that the surface was created
		this.callback.onMainWindowFinished();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// do nothing
	}

	public void startRecording() {
		this.cameraRecorder.startRecording(this.surfaceHolder,
				this.frontCameraIndex);
	}

	public void stopRecording() {
		// Stop the video recording
		if (this.cameraRecorder != null) {
			this.cameraRecorder.stopRecording();
			this.cameraRecorder = null;
		}
		this.windowManager.removeView(this.surfaceView);
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}
}