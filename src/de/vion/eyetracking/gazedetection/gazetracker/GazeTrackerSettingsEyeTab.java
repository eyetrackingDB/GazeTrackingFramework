package de.vion.eyetracking.gazedetection.gazetracker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import android.content.Context;
import de.vion.eyetracking.misc.Device;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.settings.GazeTrackingPreferences;

/**
 * 
 * The settings that are important for eyetab
 * 
 * @author André Pomp
 * 
 */
public class GazeTrackerSettingsEyeTab extends GazeTrackerSettingsAbstract {

	// Camera parameters that on orientation/setting
	private static final String CAMERA_OFFSET_X = "CAMERA_OFFSET_X";
	private static final String CAMERA_OFFSET_Y = "CAMERA_OFFSET_Y";

	// Camera parameters (intrinsic) - requires calibration
	private static final String CAMERA_FOCAL_LEN_X = "CAMERA_FOCAL_LEN_X";
	private static final String CAMERA_FOCAL_LEN_Y = "CAMERA_FOCAL_LEN_Y";
	private static final String CAMERA_PRIN_POINT_X = "CAMERA_PRIN_POINT_X";
	private static final String CAMERA_PRIN_POINT_Y = "CAMERA_PRIN_POINT_Y";

	// Camera parameters extrinsic - requires calibration
	private static final String CAMERA_DISTORT_0 = "CAMERA_DISTORT_0";
	private static final String CAMERA_DISTORT_1 = "CAMERA_DISTORT_1";
	private static final String CAMERA_DISTORT_2 = "CAMERA_DISTORT_2";
	private static final String CAMERA_DISTORT_3 = "CAMERA_DISTORT_3";
	private static final String CAMERA_DISTORT_4 = "CAMERA_DISTORT_4";

	// Camera parameters that depend on orientation
	private int cameraOffsetX = Integer.MIN_VALUE;
	private int cameraOffsetY = Integer.MIN_VALUE;

	// Camera parameters (intrinsic) - requires calibration
	float[] intrinsicParam = new float[4];

	// Camera parameters extrinsic - requires calibration
	float[] extrinsicParam = new float[5];

	public GazeTrackerSettingsEyeTab() {
		super();
	}

	public int getCameraOffsetX() {
		return this.cameraOffsetX;
	}

	public int getCameraOffsetY() {
		return this.cameraOffsetY;
	}

	public float[] getIntrinsicParam() {
		return this.intrinsicParam;
	}

	public float[] getExtrinsicParam() {
		return this.extrinsicParam;
	}

	@Override
	public void storeSettings(String subdir, Context context) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(
					FileManager.getGazeTrackingSettingsFilePath(subdir));

			loadSettingsFromPreferences(context);

			// Get the current device parameters
			prop.setProperty(DEVICE_MODEL, this.deviceModel);
			prop.setProperty(SCREEN_HEIGHT_PX,
					String.valueOf(this.deviceSettings[0]));
			prop.setProperty(SCREEN_WIDTH_PX,
					String.valueOf(this.deviceSettings[1]));
			prop.setProperty(SCREEN_HEIGHT_MM,
					String.valueOf(this.deviceSettings[2]));
			prop.setProperty(SCREEN_WIDTH_MM,
					String.valueOf(this.deviceSettings[3]));

			// Get the current orientation
			prop.setProperty(ORIENTATION, this.orientation);

			// Get the current camera resolution based on the orientation
			prop.setProperty(CAMERA_RES_WIDTH,
					String.valueOf(this.cameraResWidth));
			prop.setProperty(CAMERA_RES_HEIGHT,
					String.valueOf(this.cameraResHeight));

			// Get the current camera offset based on the orientation
			prop.setProperty(CAMERA_OFFSET_X,
					String.valueOf(this.cameraOffsetX));
			prop.setProperty(CAMERA_OFFSET_Y,
					String.valueOf(this.cameraOffsetY));

			// Get the Instrict Camera Parameters
			prop.setProperty(CAMERA_FOCAL_LEN_X,
					String.valueOf(this.intrinsicParam[0]));
			prop.setProperty(CAMERA_FOCAL_LEN_Y,
					String.valueOf(this.intrinsicParam[1]));
			prop.setProperty(CAMERA_PRIN_POINT_X,
					String.valueOf(this.intrinsicParam[2]));
			prop.setProperty(CAMERA_PRIN_POINT_Y,
					String.valueOf(this.intrinsicParam[3]));

			// Get the Extrinsic camera parameters
			prop.setProperty(CAMERA_DISTORT_0,
					String.valueOf(this.extrinsicParam[0]));
			prop.setProperty(CAMERA_DISTORT_1,
					String.valueOf(this.extrinsicParam[1]));
			prop.setProperty(CAMERA_DISTORT_2,
					String.valueOf(this.extrinsicParam[2]));
			prop.setProperty(CAMERA_DISTORT_3,
					String.valueOf(this.extrinsicParam[3]));
			prop.setProperty(CAMERA_DISTORT_4,
					String.valueOf(this.extrinsicParam[4]));
			// save properties to project root folder
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	public void loadSettingsFromPreferences(Context context) {
		this.deviceModel = Device.getDeviceModel();
		this.deviceSettings = Device.getDeviceParameters(context);

		// Get the current orientation
		this.orientation = String.valueOf(GazeTrackingPreferences
				.getDeviceOrientation(context));

		// Get the current camera resolution based on the orientation
		int[] cameraRes = convertCameraResolution(
				GazeTrackingPreferences.getCameraResolution(context),
				GazeTrackingPreferences
						.getDeviceOrientationAsActivityInfo(context));
		this.cameraResWidth = cameraRes[0];
		this.cameraResHeight = cameraRes[1];

		// Get the current camera offset based on the orientation
		int[] cameraOffset = convertCameraOffset(
				GazeTrackingPreferences.getCameraOffsetX(context),
				GazeTrackingPreferences.getCameraOffsetY(context),
				GazeTrackingPreferences
						.getDeviceOrientationAsActivityInfo(context),
				this.deviceSettings[3], this.deviceSettings[2]);
		this.cameraOffsetX = cameraOffset[0];
		this.cameraOffsetY = cameraOffset[1];

		// Get the Instrict Camera Parameters
		this.intrinsicParam = convertInstrinctParameters(
				GazeTrackingPreferences.getIntrinsicCameraValues(context),
				GazeTrackingPreferences
						.getDeviceOrientationAsActivityInfo(context));

		// Get the Extrinsic camera parameters
		this.extrinsicParam = GazeTrackingPreferences
				.getDistortionValues(context);
	}

	@Override
	public void loadSettingsFromFile(String subdir) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(
					FileManager.getGazeTrackingSettingsFilePath(subdir));

			// Load the properties
			prop.load(input);

			this.deviceModel = prop.getProperty(DEVICE_MODEL);
			this.deviceSettings[0] = Integer.valueOf(prop
					.getProperty(SCREEN_HEIGHT_PX));
			this.deviceSettings[1] = Integer.valueOf(prop
					.getProperty(SCREEN_WIDTH_PX));
			this.deviceSettings[2] = Integer.valueOf(prop
					.getProperty(SCREEN_HEIGHT_MM));
			this.deviceSettings[3] = Integer.valueOf(prop
					.getProperty(SCREEN_WIDTH_MM));

			this.orientation = prop.getProperty(ORIENTATION);

			this.cameraResWidth = Integer.valueOf(prop
					.getProperty(CAMERA_RES_WIDTH));
			this.cameraResHeight = Integer.valueOf(prop
					.getProperty(CAMERA_RES_HEIGHT));

			this.cameraOffsetX = Integer.valueOf(prop
					.getProperty(CAMERA_OFFSET_X));
			this.cameraOffsetY = Integer.valueOf(prop
					.getProperty(CAMERA_OFFSET_Y));

			this.intrinsicParam[0] = Float.valueOf(prop
					.getProperty(CAMERA_FOCAL_LEN_X));
			this.intrinsicParam[1] = Float.valueOf(prop
					.getProperty(CAMERA_FOCAL_LEN_Y));
			this.intrinsicParam[2] = Float.valueOf(prop
					.getProperty(CAMERA_PRIN_POINT_X));
			this.intrinsicParam[3] = Float.valueOf(prop
					.getProperty(CAMERA_PRIN_POINT_Y));

			this.extrinsicParam[0] = Float.valueOf(prop
					.getProperty(CAMERA_DISTORT_0));
			this.extrinsicParam[1] = Float.valueOf(prop
					.getProperty(CAMERA_DISTORT_1));
			this.extrinsicParam[2] = Float.valueOf(prop
					.getProperty(CAMERA_DISTORT_2));
			this.extrinsicParam[3] = Float.valueOf(prop
					.getProperty(CAMERA_DISTORT_3));
			this.extrinsicParam[4] = Float.valueOf(prop
					.getProperty(CAMERA_DISTORT_4));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}