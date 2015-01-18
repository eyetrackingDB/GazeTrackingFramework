package de.vion.eyetracking.gazedetection.post;

import java.util.Queue;

import android.content.Context;
import android.content.Intent;
import de.vion.eyetracking.R;

/**
 * The thread that manages the processing of several tasks
 * 
 * @author André Pomp
 * 
 */
public class PostProcessingManager extends Thread {

	// Context
	private Context context;

	// The tasks which we have to process
	private Queue<PostProcessingTask> taskQueue = null;

	// The current running thread
	private PostProcessing postProcessingThread = null;

	private PostProcessingTask currentTask = null;

	private float remainingOverallDuration;

	public PostProcessingManager(Context context,
			Queue<PostProcessingTask> taskQueue) {
		super();
		this.context = context;
		this.taskQueue = taskQueue;
	}

	@Override
	public void run() {
		super.run();

		// Sum up the duration one time
		for (PostProcessingTask task : this.taskQueue) {
			this.remainingOverallDuration += task.calculateRemainingTime(true,
					true, true, true);
		}

		int numberOfTasks = this.taskQueue.size();
		int currentTask = 1;

		while (!this.taskQueue.isEmpty()) {
			this.currentTask = this.taskQueue.poll();
			String notificationText = generateNotificationText(currentTask,
					numberOfTasks);
			this.postProcessingThread = new PostProcessing(this.context,
					this.currentTask, notificationText,
					this.remainingOverallDuration);
			this.postProcessingThread.start();
			try {
				this.postProcessingThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// this video is finished so we can remove the duration of this
			// video again
			this.remainingOverallDuration -= this.currentTask
					.calculateRemainingTime(true, true, true, true);
			currentTask++;
		}
		this.context.sendBroadcast(new Intent(
				PostProcessingService.RECEIVER_INTENT_PENDING_STOP_FINISHED));
	}

	private String generateNotificationText(int currentNumber, int allNumbers) {
		String notificationText = this.context
				.getString(R.string.service_postproc_notification_text_row1);
		notificationText += " " + currentNumber + "/" + allNumbers + "\n";
		return notificationText;
	}

	public boolean isTaskRunning(String subdir) {
		if (this.currentTask != null) {
			return this.currentTask.getSubdir().equals(subdir);
		}
		return false;
	}

	public boolean isTaskInWaitingQueue(String subdir) {
		for (PostProcessingTask task : this.taskQueue) {
			if (task.getSubdir().equals(subdir)) {
				return true;
			}
		}
		return false;
	}
}