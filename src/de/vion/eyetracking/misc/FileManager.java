package de.vion.eyetracking.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.os.Environment;
import de.vion.eyetracking.testframework.TestType;

@SuppressLint("DefaultLocale")
/**
 * 
 * Helper class for the managing of all file accesses of the complete application
 * 
 * @author André Pomp
 * 
 */
public class FileManager {

	// Main Path
	private static String DIR_MAIN_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/EyeTracking/";

	// Test resources and sub directories
	private static String DIR_TEST_RESOURCES = DIR_MAIN_PATH
			+ "test_resources/";
	private static String DIR_TEST_RESOURCES_MUSIC = DIR_TEST_RESOURCES
			+ "music/";
	private static String DIR_TEST_RESOURCES_VIDEO = DIR_TEST_RESOURCES
			+ "video/";
	private static String DIR_TEST_RESOURCES_TEXT = DIR_TEST_RESOURCES
			+ "text/";

	// Misc directory
	private static String DIR_MISC_FILES = DIR_MAIN_PATH + "misc/";

	// Test specific sub directories (main directory is the test name)
	private static String DIR_TEST = DIR_MAIN_PATH + "tests/";
	private static String DIR_LOGGING_SENSORS = "logging_sensors/";
	private static String DIR_LOGGING_TESTS = "logging_tests/";
	private static String DIR_POST_PROC = "postproc/";

	// Calibration Names
	private static String FILE_NAME_CALIBRATION = "calibration_pattern.png";

	// Recording files included in each test sub directory (raw files which are
	// recorded with vfr)
	private static String FILE_NAME_VIDEO_RECORDING_RAW_VFR = "video_recording_raw_vfr.mp4";
	private static String FILE_NAME_SCREEN_RECORDING_RUNNING_RAW_VFR = "screen_recording_raw_vfr";
	private static String FILE_NAME_SCREEN_RECORDING_RAW_VFR = "screen_recording_raw_vfr.mp4";

	// Raw files which were converted to CFR during post processing
	private static String FILE_NAME_VIDEO_RECORDING_RAW_CFR = "video_recording_raw_cfr.mp4";
	private static String FILE_NAME_SCREEN_RECORDING_RAW_CFR = "screen_recording_raw_cfr.mp4";

	// Video Files which were processed (either cfr or vfr)
	private static String FILE_NAME_VIDEO_RECORDING_PROC_VFR = "video_recording_proc_vfr.mp4";
	private static String FILE_NAME_VIDEO_RECORDING_PROC_CFR = "video_recording_proc_cfr.mp4";

	// Screen recoding which were processed (only cfr)
	private static String FILE_NAME_SCREEN_RECORDING_PROC_CFR = "screen_recording_proc_cfr.mp4";

	// Text files as output from video processing (either vfr or cfr)
	private static String FILE_NAME_TEXT_GAZE_POINTS_VFR = "gaze_points_vfr.txt";
	private static String FILE_NAME_TEXT_GAZE_POINTS_CFR = "gaze_points_cfr.txt";

	// Readme and test settings that are included in each test subdirectory
	private static String FILE_NAME_README = "Readme.txt";
	private static String FILE_NAME_SETTINGS_GAZE_TRACKING = "GazeTrackingSettings.txt";
	private static String FILE_NAME_SETTINGS_TEST_PREFERENCES = "TestPreferences.txt";

	// Logging files
	private static String FILE_NAME_SENSOR_LIGHT = "sensor_light.log";
	private static String FILE_NAME_SENSOR_ACC = "sensor_acc.log";
	private static String FILE_NAME_SENSOR_GYRO = "sensor_gyro.log";
	private static String FILE_NAME_SENSOR_ROT = "sensor_rot.log";

	public static void createDirectoryStructure() {
		// Create the directory if it does not exist
		makeDir(DIR_MAIN_PATH);

		// Create test resources and its sub directories
		makeDir(DIR_TEST_RESOURCES);
		makeDir(DIR_TEST_RESOURCES_MUSIC);
		makeDir(DIR_TEST_RESOURCES_TEXT);
		makeDir(DIR_TEST_RESOURCES_VIDEO);

		// Create misc file directory
		makeDir(DIR_MISC_FILES);

		// Create test file directory
		makeDir(DIR_TEST);
		// Create Test directories and sub directories
		for (TestType type : TestType.values()) {
			// Make the test type specific directory
			makeDir(DIR_TEST + type.name().toLowerCase());
		}
	}

	private static void makeDir(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * Creates a sub directory with the current time as file_name
	 * 
	 * @return the file path
	 */
	public static String createTestDirectory(TestType type, String abbreviation) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date currentTime = Calendar.getInstance().getTime();
		String directoryName = df.format(currentTime) + "_" + abbreviation
				+ "/";

		String filePath = DIR_TEST + type.name().toLowerCase() + "/"
				+ directoryName;
		makeDir(filePath);

		// Create the sub directories for logging
		makeDir(filePath + DIR_LOGGING_SENSORS);
		makeDir(filePath + DIR_LOGGING_TESTS);
		makeDir(filePath + DIR_POST_PROC);
		return filePath;
	}

	public static String getTestTypeDirectory(TestType type) {
		return DIR_TEST + type.name().toLowerCase() + "/";
	}

	public static String getTestDirectorySensorLogging(String directory) {
		return directory + DIR_LOGGING_SENSORS;
	}

	public static String getTestDirectoryTestLogging(String directory) {
		return directory + DIR_LOGGING_TESTS;
	}

	public static String getVideoRecordingFilePath(String directory) {
		return directory + FILE_NAME_VIDEO_RECORDING_RAW_VFR;
	}

	public static String getTestMusicDir() {
		return DIR_TEST_RESOURCES_MUSIC;
	}

	public static String getTestVideoDir() {
		return DIR_TEST_RESOURCES_VIDEO;
	}

	public static String getTestTextDir() {
		return DIR_TEST_RESOURCES_TEXT;
	}

	public static String getScreenRecordingFilePath(String directory,
			int counter) {
		return directory + FILE_NAME_SCREEN_RECORDING_RUNNING_RAW_VFR + "_"
				+ counter + ".mp4";
	}

	public static String getFinalScreenRecordingName(String directory) {
		return directory + FILE_NAME_SCREEN_RECORDING_RAW_VFR;
	}

	public static String getPostProcVideoCFRFilePath(String directory) {
		return directory + DIR_POST_PROC + FILE_NAME_VIDEO_RECORDING_RAW_CFR;
	}

	public static String getPostProcScreenCFRFilePath(String directory) {
		return directory + DIR_POST_PROC + FILE_NAME_SCREEN_RECORDING_RAW_CFR;
	}

	public static String getPostProcVideoOutputFilePath(String directory,
			boolean cfr) {
		if (cfr) {
			return directory + DIR_POST_PROC
					+ FILE_NAME_VIDEO_RECORDING_PROC_CFR;
		}
		return directory + DIR_POST_PROC + FILE_NAME_VIDEO_RECORDING_PROC_VFR;
	}

	public static String getPostProcTextFilePath(String directory, boolean cfr) {
		if (cfr) {
			return directory + DIR_POST_PROC + FILE_NAME_TEXT_GAZE_POINTS_CFR;
		}
		return directory + DIR_POST_PROC + FILE_NAME_TEXT_GAZE_POINTS_VFR;
	}

	public static String getPostProcScreenOutputFilePath(String directory) {
		return directory + DIR_POST_PROC + FILE_NAME_SCREEN_RECORDING_PROC_CFR;
	}

	public static String getSensorFilePath(String directory, int sensorType) {
		switch (sensorType) {
		case Sensor.TYPE_ACCELEROMETER:
			return directory + DIR_LOGGING_SENSORS + FILE_NAME_SENSOR_ACC;
		case Sensor.TYPE_GYROSCOPE:
			return directory + DIR_LOGGING_SENSORS + FILE_NAME_SENSOR_GYRO;
		case Sensor.TYPE_LIGHT:
			return directory + DIR_LOGGING_SENSORS + FILE_NAME_SENSOR_LIGHT;
		case Sensor.TYPE_ROTATION_VECTOR:
			return directory + DIR_LOGGING_SENSORS + FILE_NAME_SENSOR_ROT;
		}
		return null;
	}

	public static String getGazeTrackingSettingsFilePath(String directory) {
		return directory + FILE_NAME_SETTINGS_GAZE_TRACKING;
	}

	public static String getTestPreferencesFilePath(String directory) {
		return directory + FILE_NAME_SETTINGS_TEST_PREFERENCES;
	}

	public static void writeRessourceToSD(Context context, String directory,
			int resource) {
		InputStream in = context.getResources().openRawResource(resource);
		FileOutputStream out;
		try {
			out = new FileOutputStream(directory + FILE_NAME_README);

			byte[] buff = new byte[1024];
			int read = 0;
			while ((read = in.read(buff)) > 0) {
				out.write(buff, 0, read);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getCalibrationFileName() {
		return FILE_NAME_CALIBRATION;
	}

	public static String getCalibrationFilePath() {
		return DIR_MISC_FILES + FILE_NAME_CALIBRATION;
	}

	public static File copyAssetFileToSdcard(Context context, String filePath,
			String assetFileName) {
		AssetManager assetManager = context.getResources().getAssets();
		File f = new File(filePath);

		if (f.exists()) {
			return f;
		}
		InputStream is;
		try {
			is = assetManager.open(assetFileName);
			OutputStream os = new FileOutputStream(f, true);
			final int buffer_size = 1024 * 1024;
			try {
				byte[] bytes = new byte[buffer_size];
				for (;;) {
					int count = is.read(bytes, 0, buffer_size);
					if (count == -1) {
						break;
					}
					os.write(bytes, 0, count);
				}
				is.close();
				os.close();
				return f;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}