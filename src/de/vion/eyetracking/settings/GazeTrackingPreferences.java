package de.vion.eyetracking.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import de.vion.eyetracking.R;

/**
 * 
 * Helper Class for accessing the preferences of the application
 * 
 * @author André Pomp
 * 
 */
public class GazeTrackingPreferences {

	// General application settings that are set automatically
	private static final String PREF_START_TUTORIAL = "pref_start_tutorial";
	private static final String PREF_IS_CALIBRATED = "pref_is_calibrated";

	// Settings from the GeneralSettings Activity
	private static final String PREF_DEVICE_ORIENTATION = "pref_general_pref_device_orientation";
	private static final String PREF_CAMERA_OFFSET_X = "pref_general_pref_camera_offset_x";
	private static final String PREF_CAMERA_OFFSET_Y = "pref_general_pref_camera_offset_y";
	private static final String PREF_CAMERA_RESOLUTION = "pref_general_pref_camera_resolution";

	// Camera parameters (intrinsic) - determined via calibration - calculated
	// in landscape mode
	private static final String PREF_CAMERA_FOCAL_LEN_X = "pref_camera_focal_len_x";
	private static final String PREF_CAMERA_FOCAL_LEN_Y = "pref_camera_focal_len_y";
	private static final String PREF_CAMERA_PRIN_POINT_X = "pref_camera_prin_point_x";
	private static final String PREF_CAMERA_PRIN_POINT_Y = "pref_camera_prin_point_y";

	// Camera parameters extrinsic - determined via calibration - calculated in
	// landscape mode
	private static final String PREF_CAMERA_DISTORT_0 = "pref_camera_distort_0";
	private static final String PREF_CAMERA_DISTORT_1 = "pref_camera_distort_1";
	private static final String PREF_CAMERA_DISTORT_2 = "pref_camera_distort_2";
	private static final String PREF_CAMERA_DISTORT_3 = "pref_camera_distort_3";
	private static final String PREF_CAMERA_DISTORT_4 = "pref_camera_distort_4";

	private static SharedPreferences getSharedPreferences(Context context) {
		return context
				.getSharedPreferences(
						GazeTrackingPreferencesFragment.FILE_NAME,
						Context.MODE_PRIVATE);
	}

	public static boolean isCalibrated(Context context) {
		return getSharedPreferences(context).getBoolean(PREF_IS_CALIBRATED,
				false);
	}

	public static void setIsCalibrated(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(PREF_IS_CALIBRATED, value).commit();
	}

	public static boolean startTutorial(Context context) {
		return getSharedPreferences(context).getBoolean(PREF_START_TUTORIAL,
				true);
	}

	public static void setStartTutorial(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(PREF_START_TUTORIAL, value).commit();
	}

	public static String getDeviceOrientation(Context context) {
		return getSharedPreferences(context)
				.getString(
						PREF_DEVICE_ORIENTATION,
						context.getString(R.string.general_pref_device_orientation_default));
	}

	public static int getDeviceOrientationAsActivityInfo(Context context) {
		String orientation = getDeviceOrientation(context);

		if (orientation.equals("LANDSCAPE")) {
			return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		} else if (orientation.equals("LANDSCAPE_REVERSE")) {
			return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;

		} else if (orientation.equals("PORTRAIT")) {
			return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

		} else if (orientation.equals("PORTRAIT_REVERSE")) {
			return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
		}
		return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	}

	public static int getCameraOffsetX(Context context) {
		return Integer.valueOf(getSharedPreferences(context).getString(
				PREF_CAMERA_OFFSET_X, "0"));
	}

	public static int getCameraOffsetY(Context context) {
		return Integer.valueOf(getSharedPreferences(context).getString(
				PREF_CAMERA_OFFSET_Y, "0"));
	}

	/**
	 * @param context
	 * @return res[0] = width of camera resolution and res[1] = height (all in
	 *         landscape mode)
	 */
	public static int[] getCameraResolution(Context context) {
		String res = getSharedPreferences(context)
				.getString(
						PREF_CAMERA_RESOLUTION,
						context.getString(R.string.general_pref_camera_resolution_default));
		// Split the resolution at "x"
		String[] splittedRes = res.split("x");
		int[] result = new int[2];
		result[0] = Integer.valueOf(splittedRes[0]);
		result[1] = Integer.valueOf(splittedRes[1]);
		return result;
	}

	public static float[] getDistortionValues(Context context) {
		SharedPreferences sharedPrefs = getSharedPreferences(context);
		float[] values = new float[5];

		values[0] = sharedPrefs.getFloat(PREF_CAMERA_DISTORT_0, 0);
		values[1] = sharedPrefs.getFloat(PREF_CAMERA_DISTORT_1, 0);
		values[2] = sharedPrefs.getFloat(PREF_CAMERA_DISTORT_2, 0);
		values[3] = sharedPrefs.getFloat(PREF_CAMERA_DISTORT_3, 0);
		values[4] = sharedPrefs.getFloat(PREF_CAMERA_DISTORT_4, 0);
		return values;
	}

	public static void setCameraDistortionValues(Context context, float value0,
			float value1, float value2, float value3, float value4) {
		SharedPreferences sharedPrefs = getSharedPreferences(context);
		sharedPrefs.edit().putFloat(PREF_CAMERA_DISTORT_0, value0).commit();
		sharedPrefs.edit().putFloat(PREF_CAMERA_DISTORT_1, value1).commit();
		sharedPrefs.edit().putFloat(PREF_CAMERA_DISTORT_2, value2).commit();
		sharedPrefs.edit().putFloat(PREF_CAMERA_DISTORT_3, value3).commit();
		sharedPrefs.edit().putFloat(PREF_CAMERA_DISTORT_4, value4).commit();
	}

	public static float[] getIntrinsicCameraValues(Context context) {
		SharedPreferences sharedPrefs = getSharedPreferences(context);
		float[] values = new float[4];

		values[0] = sharedPrefs.getFloat(PREF_CAMERA_FOCAL_LEN_X, 0);
		values[1] = sharedPrefs.getFloat(PREF_CAMERA_FOCAL_LEN_Y, 0);
		values[2] = sharedPrefs.getFloat(PREF_CAMERA_PRIN_POINT_X, 0);
		values[3] = sharedPrefs.getFloat(PREF_CAMERA_PRIN_POINT_Y, 0);
		return values;
	}

	public static void setIntrinsicCameraValues(Context context,
			float focalLenX, float focalLenY, float prinPointX, float prinPointY) {
		SharedPreferences sharedPrefs = getSharedPreferences(context);
		sharedPrefs.edit().putFloat(PREF_CAMERA_FOCAL_LEN_X, focalLenX)
				.commit();
		sharedPrefs.edit().putFloat(PREF_CAMERA_FOCAL_LEN_Y, focalLenY)
				.commit();
		sharedPrefs.edit().putFloat(PREF_CAMERA_PRIN_POINT_X, prinPointX)
				.commit();
		sharedPrefs.edit().putFloat(PREF_CAMERA_PRIN_POINT_Y, prinPointY)
				.commit();
	}
}