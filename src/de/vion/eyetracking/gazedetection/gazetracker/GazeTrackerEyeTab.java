package de.vion.eyetracking.gazedetection.gazetracker;

import java.io.File;

import org.opencv.core.Mat;

import android.content.Context;
import de.vion.eyetracking.R;

/**
 * 
 * The Implementation for the EyeTab GazeTracking Approach
 * 
 * @author André Pomp
 * 
 */
public class GazeTrackerEyeTab extends GazeTrackerAbstract {

	private static final String EYE_CENTER_ALGO_GRADIENTS = "EYE_CENTER_ALGO_GRADIENTS";
	private static final String EYE_CENTER_ALGO_ISOPHOTES = "EYE_CENTER_ALGO_ISOPHOTES";
	private static final String EYE_CENTER_ALGO_COMBINED = "EYE_CENTER_ALGO_COMBINED";
	private static final String GAZE_TRACKING_ALGO_APPROX = "GAZE_TRACKING_ALGO_APPROX";
	private static final String GAZE_TRACKING_ALGO_GEO = "GAZE_TRACKING_ALGO_GEO";

	// The haar file address
	private File haarFileBothEyes;
	private File haarFileLeftEye;
	private File haarFileRightEye;

	public GazeTrackerEyeTab(Context context) {
		super(context);
		this.nativeObjectAddress = nativeCreateObject();
	}

	@Override
	public void init(GazeTrackerSettingsAbstract settings) {
		// load the haar file
		loadHaarFiles();

		GazeTrackerSettingsEyeTab eyeTabSettings = (GazeTrackerSettingsEyeTab) settings;

		String orientation = eyeTabSettings.getOrientation();
		int screenSizeX_PX = eyeTabSettings.getDeviceSettings()[1];
		int screenSizeY_PX = eyeTabSettings.getDeviceSettings()[0];
		int screenSizeX_MM = eyeTabSettings.getDeviceSettings()[3];
		int screenSizeY_MM = eyeTabSettings.getDeviceSettings()[2];
		int cameraOffsetX = eyeTabSettings.getCameraOffsetX();
		int cameraOffsetY = eyeTabSettings.getCameraOffsetY();
		int cameraResWidth = eyeTabSettings.getCameraResWidth();
		int cameraResHeight = eyeTabSettings.getCameraResHeight();
		float focalLenX = eyeTabSettings.getIntrinsicParam()[0];
		float focalLenY = eyeTabSettings.getIntrinsicParam()[1];
		float focalLenZ = (eyeTabSettings.getIntrinsicParam()[0] + eyeTabSettings
				.getIntrinsicParam()[1]) / 2.0f;
		float prinPointX = eyeTabSettings.getIntrinsicParam()[2];
		float prinPointY = eyeTabSettings.getIntrinsicParam()[3];

		// Init the native object
		nativeInitObject(this.nativeObjectAddress,
				this.haarFileBothEyes.getAbsolutePath(),
				this.haarFileLeftEye.getAbsolutePath(),
				this.haarFileRightEye.getAbsolutePath(), orientation,
				screenSizeX_PX, screenSizeY_PX, screenSizeX_MM, screenSizeY_MM,
				cameraOffsetX, cameraOffsetY, cameraResWidth, cameraResHeight,
				focalLenX, focalLenY, focalLenZ, prinPointX, prinPointY,
				EYE_CENTER_ALGO_GRADIENTS, GAZE_TRACKING_ALGO_APPROX, 50);
	}

	private void loadHaarFiles() {
		// Load the haar file
		File haarDir = this.context.getDir("haar_file", Context.MODE_PRIVATE);

		this.haarFileBothEyes = loadHaarFile(haarDir,
				R.raw.haarcascade_mcs_eyepair_big,
				"haarcascade_mcs_eyepair_big.xml");
		this.haarFileLeftEye = loadHaarFile(haarDir,
				R.raw.haarcascade_mcs_lefteye, "haarcascade_mcs_lefteye.xml");
		this.haarFileRightEye = loadHaarFile(haarDir,
				R.raw.haarcascade_mcs_righteye, "haarcascade_mcs_righteye.xml");

		haarDir.delete();
	}

	@Override
	public int[] detect(Mat colorFrame) {
		return nativeDetect(this.nativeObjectAddress,
				colorFrame.getNativeObjAddr());
	}

	@Override
	public void release() {
		nativeDestroyObject(this.nativeObjectAddress);
		this.nativeObjectAddress = 0;
	}

	private static native long nativeCreateObject();

	private static native void nativeInitObject(long nativeObjectAddress,
			String haarFileBigEyes, String haarFileLeftEye,
			String haarFileRightEye, String orientation, int screenSizeX_PX,
			int screenSizeY_PX, int screenSizeX_MM, int screenSizeY_MM,
			int cameraOffsetX, int cameraOffsetY, int cameraResWidth,
			int cameraResHeight, double focalLenX, double focalLenY,
			double focalLenZ, double prinPointX, double prinPointY,
			String eyeCenterDetectAlgo, String gazeTrackingAlgo,
			int fastSizeWidth);

	private static native int[] nativeDetect(long nativeObjectAddress,
			long colorFrame);

	private static native void nativeDestroyObject(long nativeObjectAddress);
}