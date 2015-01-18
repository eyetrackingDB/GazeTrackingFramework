package de.vion.eyetracking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.os.IBinder;
import de.vion.eyetracking.callback.AppDisplayManagerCallback;
import de.vion.eyetracking.logging.SensorLoggerManager;
import de.vion.eyetracking.recording.ScreenRecorder;
import de.vion.eyetracking.testframework.TestFactory;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;
import de.vion.eyetracking.windowmanager.AppDisplayManager;

/**
 * 
 * The Service that handles the complete funtionality of recording and playing
 * tests. It moreover allows to use a demo modus.
 * 
 * @author André Pomp
 * 
 */
public class RecordService extends Service implements AppDisplayManagerCallback {

	public static final String INTENT_ARG_TEST_TYPE = "TEST_TYPE";
	public static final String INTENT_ARG_SUB_DIR = "SUB_DIR";
	public static final String INTENT_ARG_DEMO_MODUS = "DEMO_MODUS";

	// Lets the MainService Stop
	public static final String RECEIVER_INTENT_PENDING_STOP = "EYE_TRACKING_REC_PENDING_INTENT_STOP";
	public static final String RECEIVER_INTENT_ACTIVITY_STOP = "EYE_TRACKING_REC_ACTIVITY_STOP";

	private static final int NOTI_ID = 237132;

	// The demo modus
	private boolean demoModus = false;

	// Notification variables
	private NotificationManager notificationManager;
	private BroadcastReceiver stopReceiver;

	// Display Manager
	private AppDisplayManager displayManager;

	// Logging
	private SensorLoggerManager sensorLoggerManager;

	// Screen Recorder
	private ScreenRecorder screenRecorder;

	// The current type and its settings
	private TestType testType;
	private GenericTestSettings testSettings;

	@Override
	public void onCreate() {
		super.onCreate();
		// Put the service into the foreground
		startForeground(NOTI_ID, initNotification());

		// Init the notification manager
		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Setup the broadcast receiver
		this.stopReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action_name = intent.getAction();
				if (action_name.equals(RECEIVER_INTENT_PENDING_STOP)
						|| action_name.equals(RECEIVER_INTENT_ACTIVITY_STOP)) {
					// Calls on destroy which will stop everything
					stopSelf();
				}
			}
		};

		// Register the broadcast receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(RECEIVER_INTENT_ACTIVITY_STOP);
		filter.addAction(RECEIVER_INTENT_PENDING_STOP);
		registerReceiver(this.stopReceiver, filter);

		// Change the notification
		this.notificationManager.notify(NOTI_ID, initNotification());
	}

	private void start() {
		// Start the logging of the sensor data
		if (!this.demoModus && this.testSettings.isLogSensorData(this)) {
			this.sensorLoggerManager.startLogging();
		}

		// Start the display manager (recording of video and displaying of
		// second screen)
		this.displayManager.start();

		// Start the screen recording
		if (!this.demoModus && this.testSettings.isRecordScreen(this)) {
			this.screenRecorder.startRecording();
		}
	}

	private void stop() {
		// Stop the logging
		if (!this.demoModus && this.testSettings.isLogSensorData(this)) {
			this.sensorLoggerManager.stopLogging();
			this.sensorLoggerManager = null;
		}

		// Stop camera recording
		// Stop playing video on second screen
		this.displayManager.stop();

		// Stop screen recording
		if (!this.demoModus && this.testSettings.isRecordScreen(this)) {
			this.screenRecorder.stopRecording();
			this.screenRecorder = null;
		}

		// Stop Test Activity
		Intent intent = new Intent(this, TestActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		if (!this.demoModus) {
			intent.putExtra(TestActivity.INTENT_START, false);
		} else {
			intent.putExtra(TestActivity.INTENT_START, true);
		}
		startActivity(intent);

		// Cancel all notifications
		this.notificationManager.cancelAll();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stop(); // stop everything
		unregisterReceiver(this.stopReceiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Get the testtype and a reference to its settings
		this.testType = (TestType) intent
				.getSerializableExtra(INTENT_ARG_TEST_TYPE);
		this.testSettings = TestFactory.getTestSettings(this.testType);

		// Get the subdir
		String subdir = intent.getStringExtra(INTENT_ARG_SUB_DIR);

		// Check the demo modus
		this.demoModus = intent.getBooleanExtra(INTENT_ARG_DEMO_MODUS, false);

		// Init the logger manager
		this.sensorLoggerManager = new SensorLoggerManager(this, subdir);
		this.sensorLoggerManager.addSensor(Sensor.TYPE_ACCELEROMETER);
		this.sensorLoggerManager.addSensor(Sensor.TYPE_GYROSCOPE);
		this.sensorLoggerManager.addSensor(Sensor.TYPE_LIGHT);
		this.sensorLoggerManager.addSensor(Sensor.TYPE_ROTATION_VECTOR);

		// Init the screen recorder
		this.screenRecorder = new ScreenRecorder(subdir);

		// Init the display manager
		this.displayManager = new AppDisplayManager(this, this,
				this.testSettings, this.demoModus);
		this.displayManager.initDisplays(subdir,
				this.testSettings.getSecondScreenVideoFilePath(this));

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// do nothing
		return null;
	}

	private Notification initNotification() {
		// Start the notification manager
		Notification.Builder builder = new Notification.Builder(this);

		// open home activity on click
		Intent intent = new Intent(this, TestActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(TestActivity.INTENT_START, false);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setContentIntent(pendingIntent);

		// Create a service notification icon
		builder.setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(false)
				.setContentTitle(
						getString(R.string.service_main_notification_title))
				.setContentText(
						getString(R.string.service_main_notification_text));

		// Add the stop button
		builder.addAction(R.drawable.ic_action_stop,
				getString(R.string.service_main_notification_action_pause),
				createStopPendingIntent());

		return builder.build();
	}

	private PendingIntent createStopPendingIntent() {
		// Define the pending intent
		Intent intent = new Intent(RECEIVER_INTENT_PENDING_STOP);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		return pendingIntent;
	}

	@Override
	public void onDisplaysInitialized() {
		start();
	}

	@Override
	public void onDisplayRemoved() {
		// Calls on destroy which will stop everything
		stopSelf();
	}
}