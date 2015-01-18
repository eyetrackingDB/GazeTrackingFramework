package de.vion.eyetracking.gazedetection.gazetracker;

import android.content.Context;
import android.content.pm.ActivityInfo;

/**
 * 
 * The abstract settings that we find important for all gaze tracking approaches
 * 
 * @author André Pomp
 * 
 */
public abstract class GazeTrackerSettingsAbstract {

	// Device parameters that never change
	protected static final String DEVICE_MODEL = "DEVICE_MODEL";

	// Device parameters that depend on the orientation/setting
	protected static final String ORIENTATION = "ORIENTATION";
	protected static final String SCREEN_WIDTH_PX = "SCREEN_WIDTH_PX";
	protected static final String SCREEN_HEIGHT_PX = "SCREEN_HEIGHT_PX";
	protected static final String SCREEN_WIDTH_MM = "SCREEN_WIDTH_MM";
	protected static final String SCREEN_HEIGHT_MM = "SCREEN_HEIGHT_MM";

	// Camera parameters that on orientation/setting
	protected static final String CAMERA_RES_WIDTH = "CAMERA_RES_WIDTH";
	protected static final String CAMERA_RES_HEIGHT = "CAMERA_RES_HEIGHT";

	// Device parameters that never change
	protected String deviceModel = "NONE";

	// Device parameters that change and depend on the orientation
	protected int[] deviceSettings = new int[4];
	protected String orientation = "NONE";

	// Camera parameters that depend on orientation
	protected int cameraResWidth = Integer.MIN_VALUE;
	protected int cameraResHeight = Integer.MIN_VALUE;

	public GazeTrackerSettingsAbstract() {
		super();
	}

	/**
	 * Stores the setting at the specified location
	 * 
	 * @param subdir
	 * @param context
	 */
	public abstract void storeSettings(String subdir, Context context);

	/**
	 * Loads the settings from the preferences
	 * 
	 * @param context
	 */
	public abstract void loadSettingsFromPreferences(Context context);

	/**
	 * Loads the settings from the file
	 * 
	 * @param subdir
	 */
	public abstract void loadSettingsFromFile(String subdir);

	public String getOrientation() {
		return this.orientation;
	}

	public String getDeviceModel() {
		return this.deviceModel;
	}

	public int[] getDeviceSettings() {
		return this.deviceSettings;
	}

	public int getCameraResWidth() {
		return this.cameraResWidth;
	}

	public int getCameraResHeight() {
		return this.cameraResHeight;
	}

	/**
	 * Converts the values depenend on the orrientation
	 * 
	 * @param currentValues
	 * @param orientation
	 * @return
	 */
	protected static int[] convertCameraResolution(int[] currentValues,
			int orientation) {
		switch (orientation) {
		case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
		case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
			int[] result = new int[2];
			result[0] = currentValues[1];
			result[1] = currentValues[0];
			return result;
		}
		return currentValues;
	}

	/**
	 * Converts the camera offset accordingly to the orientation
	 * 
	 * @param cameraOffsetX
	 * @param cameraOffsetY
	 * @param orientation
	 * @param deviceWidthInCurrentOrientation
	 * @param deviceHeightInCurrentOrientation
	 * @return
	 */
	protected static int[] convertCameraOffset(int cameraOffsetX,
			int cameraOffsetY, int orientation,
			int deviceWidthInCurrentOrientation,
			int deviceHeightInCurrentOrientation) {

		int[] result = new int[2];

		switch (orientation) {
		case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
			result[0] = cameraOffsetX;
			result[1] = cameraOffsetY;
			break;
		case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
			result[0] = deviceWidthInCurrentOrientation + (cameraOffsetX * -1);
			result[1] = (cameraOffsetY + deviceHeightInCurrentOrientation) * -1;
			break;
		case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
			result[0] = deviceWidthInCurrentOrientation + cameraOffsetY;
			result[1] = cameraOffsetX * -1;
			break;
		case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
			result[0] = cameraOffsetY * -1;
			result[1] = (cameraOffsetX * -1 + deviceHeightInCurrentOrientation)
					* -1;
			break;
		}
		return result;
	}

	/**
	 * Converts the intrisic parameters to the current orientation
	 * 
	 * @param currentValues
	 * @param orientation
	 * @return
	 */
	protected static float[] convertInstrinctParameters(float[] currentValues,
			int orientation) {
		switch (orientation) {
		case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
		case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
			float[] result = new float[4];
			result[0] = currentValues[1];
			result[1] = currentValues[0];
			result[2] = currentValues[3];
			result[3] = currentValues[2];
			return result;
		}
		return currentValues;
	}
}
