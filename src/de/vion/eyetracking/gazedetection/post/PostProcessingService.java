package de.vion.eyetracking.gazedetection.post;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testmanagement.TestManagementActivity;

/**
 * The services that ensures that the processing can take place in the background
 * 
 * @author André Pomp
 * 
 */
public class PostProcessingService extends Service {

	public static PostProcessingService instance = null;

	public static final String INTENT_ARG_LIST_OF_TASKS = "LIST_OF_TASKS";
	public static final String INTENT_ARG_NOTIFICATION_TEXT = "NOTIFICATION_TEXT";

	public static final String RECEIVER_INTENT_PENDING_STOP_FINISHED = "EYE_TRACKING_POSTPROC_PENDING_INTENT_STOP_FINISHED";
	public static final String RECEIVER_INTENT_PENDING_STOP_NOT_FINISHED = "EYE_TRACKING_POSTPROC_PENDING_INTENT_STOP_NOT_FINISHED";
	public static final String RECEIVER_INTENT_PENDING_UPDATE = "EYE_TRACKING_POSTPROC_PENDING_INTENT_UPDATE";

	private static final int NOTI_ID = 237133;

	// Notification variables
	private NotificationManager notificationManager;;
	private Notification.Builder builder;
	private BroadcastReceiver broadcastReceiver;

	// The tasks which we have to process
	private ArrayList<PostProcessingTask> listOfTasks = null;

	// The current thread
	private PostProcessingManager postProcessingManager = null;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this; // save the instance

		// Put the service into the foreground
		startForeground(NOTI_ID, initNotification());

		// Init the notification manager
		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Setup the broadcast receiver
		this.broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action_name = intent.getAction();
				if (action_name.equals(RECEIVER_INTENT_PENDING_STOP_FINISHED)) {
					stopSelf();
				} else if (action_name.equals(RECEIVER_INTENT_PENDING_UPDATE)) {
					String notificationText = intent
							.getStringExtra(INTENT_ARG_NOTIFICATION_TEXT);
					updateNotificationText(notificationText);
				} else if (action_name
						.equals(RECEIVER_INTENT_PENDING_STOP_NOT_FINISHED)) {
					// TODO stop all threads and finish
				}
			}
		};

		// Register the broadcast receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(RECEIVER_INTENT_PENDING_UPDATE);
		filter.addAction(RECEIVER_INTENT_PENDING_STOP_FINISHED);
		registerReceiver(this.broadcastReceiver, filter);

		// Change the notification
		this.notificationManager.notify(NOTI_ID, initNotification());
	}

	@Override
	public IBinder onBind(Intent intent) {
		// do nothing
		return null;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(this.broadcastReceiver);
		instance = null;
		this.postProcessingManager = null;
		super.onDestroy();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Get the testtype and a reference to its settings
		this.listOfTasks = (ArrayList<PostProcessingTask>) intent
				.getSerializableExtra(INTENT_ARG_LIST_OF_TASKS);

		Queue<PostProcessingTask> taskQueue = new LinkedList<PostProcessingTask>();
		for (PostProcessingTask task : this.listOfTasks) {
			taskQueue.add(task);
		}

		// Start the processing
		this.postProcessingManager = new PostProcessingManager(
				getApplicationContext(), taskQueue);
		this.postProcessingManager.start();

		return super.onStartCommand(intent, flags, startId);
	}

	private Notification initNotification() {
		// Start the notification manager
		this.builder = new Notification.Builder(this);

		// open home activity on click
		Intent intent = new Intent(this, TestManagementActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		this.builder.setContentIntent(pendingIntent);

		// Create a service notification icon
		this.builder
				.setSmallIcon(R.drawable.ic_noti_post_proc)
				.setAutoCancel(false)
				.setContentTitle(
						getString(R.string.service_postproc_notification_title));

		// Add the stop button
		this.builder.addAction(R.drawable.ic_action_stop,
				getString(R.string.service_main_notification_action_pause),
				createStopPendingIntent());

		return this.builder.build();
	}

	private void updateNotificationText(String text) {
		this.builder
				.setContentText(getString(R.string.service_postproc_notification_text_content));
		this.builder.setStyle(new Notification.BigTextStyle().bigText(text));
		this.notificationManager.notify(NOTI_ID, this.builder.build());
	}

	private PendingIntent createStopPendingIntent() {
		// Define the pending intent
		Intent intent = new Intent(RECEIVER_INTENT_PENDING_STOP_NOT_FINISHED);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		return pendingIntent;
	}

	public boolean isTaskRunning(String subdir) {
		if (this.postProcessingManager != null) {
			return this.postProcessingManager.isTaskRunning(subdir);
		}
		return false;
	}

	public boolean isTaskInWaitingQueue(String subdir) {
		if (this.postProcessingManager != null) {
			return this.postProcessingManager.isTaskInWaitingQueue(subdir);
		}
		return false;
	}
}