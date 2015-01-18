package de.vion.eyetracking.testmanagement.detail;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.vion.eyetracking.R;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerFactory;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerSettingsAbstract;
import de.vion.eyetracking.gazedetection.post.PostProcessingService;
import de.vion.eyetracking.gazedetection.post.PostProcessingTask;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.testframework.TestFactory;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;

/**
 * 
 * An detail fragment that shows the details of a test
 * 
 * @author André Pomp
 * 
 */
public class TestManagementFragmentDetail extends Fragment {

	private static final String BUNDLE_ARGS_TEST_TYPE = "TEST_TYPE";
	private static final String BUNDLE_ARGS_DIRECTORY = "DIRECTORY";

	private TestType testType;
	private String directory;
	private GazeTrackerSettingsAbstract settings;

	private String abbreviation;
	private String time;
	private String date;

	public static Fragment getInstance(TestType type, String directory) {
		Fragment fragment = new TestManagementFragmentDetail();
		Bundle args = new Bundle();
		args.putSerializable(BUNDLE_ARGS_TEST_TYPE, type);
		args.putString(BUNDLE_ARGS_DIRECTORY, directory);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		this.testType = (TestType) args.getSerializable(BUNDLE_ARGS_TEST_TYPE);
		this.directory = args.getString(BUNDLE_ARGS_DIRECTORY);
		this.settings = GazeTrackerFactory.createGazeTrackerSettings();
		this.settings.loadSettingsFromFile(this.directory);

		new PostProcessingTask(false, false, false, this.directory);

		parseDirectoryName();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(
				R.layout.test_management_fragment_detail, container, false);

		LinearLayout mainLayout = (LinearLayout) rootView
				.findViewById(R.id.fragment_testmanagement_detail_ll);

		TextView tvAbbreviation = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_abbreviation_value);
		tvAbbreviation.setText(this.abbreviation);

		TextView tvDate = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_date_value);
		tvDate.setText(this.date);

		TextView tvTime = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_time_value);
		tvTime.setText(this.time);

		TextView tvDuration = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_duration_value);
		tvDuration.setText(calculateDuration());

		TextView tvDeviceModel = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_device_model_value);
		tvDeviceModel.setText(this.settings.getDeviceModel());

		TextView tvOrientation = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_orientation_value);
		tvOrientation.setText(this.settings.getOrientation());

		TextView tvDeviceWidthPX = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_width_px_device);
		tvDeviceWidthPX.setText(this.settings.getDeviceSettings()[1]
				+ getString(R.string.unit_px));

		TextView tvDeviceWidthMM = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_width_mm_device);
		tvDeviceWidthMM.setText(this.settings.getDeviceSettings()[3]
				+ getString(R.string.unit_mm));

		TextView tvDeviceHeightPX = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_height_px_device);
		tvDeviceHeightPX.setText(this.settings.getDeviceSettings()[0]
				+ getString(R.string.unit_px));

		TextView tvDeviceHeightMM = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_height_mm_device);
		tvDeviceHeightMM.setText(this.settings.getDeviceSettings()[2]
				+ getString(R.string.unit_mm));

		TextView tvCameraResWidth = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_resolution_camera_width);
		tvCameraResWidth.setText(this.settings.getCameraResWidth()
				+ getString(R.string.unit_px));

		TextView tvCameraResHeight = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_resolution_camera_height);
		tvCameraResHeight.setText(this.settings.getCameraResHeight()
				+ getString(R.string.unit_px));

		TextView tvStatus = (TextView) rootView
				.findViewById(R.id.fragment_testmanagement_detail_tv_status_value);
		tvStatus.setText(calculateProcessingStatus());

		// Load the test specific settings
		mainLayout.addView(createTestSpecificLayout());

		return rootView;
	}

	private TableLayout createTestSpecificLayout() {
		GenericTestSettings settings = TestFactory
				.getTestSettings(this.testType);
		Map<String, String> settingValues = settings
				.loadSharedPreferences(this.directory);

		// Create the table layout
		TableLayout layout = new TableLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		List<TableRow> tableRows = settings.getTableRows(settingValues,
				getActivity());
		for (TableRow row : tableRows) {
			layout.addView(row);
		}
		return layout;
	}

	@SuppressLint("SimpleDateFormat")
	private void parseDirectoryName() {
		String fileName = new File(this.directory).getName();
		this.abbreviation = fileName.substring(fileName.lastIndexOf("_") + 1);
		String dateString = fileName.substring(0, fileName.lastIndexOf("_"));
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		try {
			// Parse the string to a date
			Date currentDate = df.parse(dateString);

			// Parse the date to a date string
			DateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy");
			this.date = dfDate.format(currentDate);

			// Parse the date to atime
			DateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
			this.time = dfTime.format(currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private String calculateDuration() {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(FileManager
				.getVideoRecordingFilePath(this.directory));
		String time = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		long timeInmillisec = Long.parseLong(time);
		long duration = timeInmillisec / 1000;
		long hours = duration / 3600;
		long minutes = (duration - hours * 3600) / 60;
		long seconds = duration - (hours * 3600 + minutes * 60);

		return String.format("%02d", minutes) + ":"
				+ String.format("%02d", seconds) + " "
				+ getString(R.string.unit_min);
	}

	private String calculateProcessingStatus() {
		// Check if the task is currently processed
		PostProcessingService service = PostProcessingService.instance;
		if (service != null) {
			// Check if the task is currently running
			if (service.isTaskRunning(this.directory)) {
				return getString(R.string.fragment_testmanagement_detail_tv_status_running);
			}
			if (service.isTaskInWaitingQueue(this.directory)) {
				return getString(R.string.fragment_testmanagement_detail_tv_status_wating);
			}
		}
		// if the task is not running or not in the queue we can check if the
		// gaze.txt file exists
		// if the file exists the task was processed before
		if (new File(FileManager.getPostProcTextFilePath(this.directory, true))
				.exists()
				|| new File(FileManager.getPostProcTextFilePath(this.directory,
						false)).exists()) {
			return getString(R.string.fragment_testmanagement_detail_tv_status_processed);
		}
		return getString(R.string.fragment_testmanagement_detail_tv_status_not_processed);
	}
}