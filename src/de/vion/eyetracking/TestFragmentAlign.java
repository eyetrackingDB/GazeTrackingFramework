package de.vion.eyetracking;

import android.app.Fragment;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * 
 * The Fragment that shows a preprinted face for finding the correct distance
 * between user and tablet
 * 
 * @author André Pomp
 * 
 */
@SuppressWarnings("deprecation")
public class TestFragmentAlign extends Fragment implements
		SurfaceHolder.Callback {

	private Camera camera;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.test_fragment_align,
				container, false);

		SurfaceView surfaceView = (SurfaceView) rootView
				.findViewById(R.id.test_fragment_align_sv);
		surfaceView.getHolder().addCallback(this);

		return rootView;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			int cameraID = findFrontFacingCamera();

			// Set the preview
			this.camera = Camera.open(cameraID);

			// Change the rotation
			// Found at:
			// http://stackoverflow.com/questions/5309029/android-camera-rotate
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(cameraID, info);

			int rotation = getActivity().getWindowManager().getDefaultDisplay()
					.getRotation();
			int degrees = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}

			// Compensate the mirroring
			int result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
			this.camera.setDisplayOrientation(result);

			// Start the preview
			this.camera.setPreviewDisplay(holder);
			this.camera.startPreview();
		} catch (RuntimeException e) {
			return;
		} catch (Exception e) {
			return;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (this.camera != null) {
			this.camera.stopPreview();
			this.camera.release();
			this.camera = null;
		}
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