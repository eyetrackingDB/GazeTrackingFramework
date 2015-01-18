package de.vion.eyetracking.gazedetection.post;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import de.vion.eyetracking.R;
import de.vion.eyetracking.gazedetection.post.convertfps.ConvertFPS;
import de.vion.eyetracking.gazedetection.post.screenprocessing.ProcessScreenRecording;
import de.vion.eyetracking.gazedetection.post.videoprocessing.ProcessVideoRecording;
import de.vion.eyetracking.misc.FileManager;

/**
 * The thread that performs the processing of a single test task
 * 
 * @author André Pomp
 * 
 */
public class PostProcessing extends Thread {

	private Context context = null;
	private PostProcessingTask task;
	private String mainNotificationText;
	private float remainingOverallDuration;

	public PostProcessing(Context context, PostProcessingTask task,
			String mainNotificationText, float remainingOverallDuration) {
		super();
		this.task = task;
		this.context = context;
		this.mainNotificationText = mainNotificationText;
		this.remainingOverallDuration = remainingOverallDuration;
	}

	@Override
	public void run() {
		// Update the Notification
		float remainingDuration = this.task.calculateRemainingTime(true, true,
				true, true);
		updateNotification(
				this.context
						.getString(R.string.service_postproc_notification_text_row2_action_convertfps_video),
				remainingDuration);

		// Convert FPS of videoFile (if video file should not be converted, then
		// the input file is the output file
		File videoFileOutput = convertFPS(
				FileManager.getVideoRecordingFilePath(this.task.getSubdir()),
				FileManager.getPostProcVideoCFRFilePath(this.task.getSubdir()),
				this.task.isConvertFPS());

		// Update the Notification
		float newRemainingDuration = this.task.calculateRemainingTime(false,
				true, true, true);
		this.remainingOverallDuration -= (remainingDuration - newRemainingDuration);
		remainingDuration = newRemainingDuration;
		updateNotification(
				this.context
						.getString(R.string.service_postproc_notification_text_row2_action_convertfps_screen),
				remainingDuration);

		// Convert FPS of videoFile (if video file should not be converted, then
		// the input file is the output file
		File screenFileOutput = convertFPS(
				FileManager.getFinalScreenRecordingName(this.task.getSubdir()),
				FileManager.getPostProcScreenCFRFilePath(this.task.getSubdir()),
				this.task.isConvertFPS());

		// Update the Notification
		newRemainingDuration = this.task.calculateRemainingTime(false, false,
				true, true);
		this.remainingOverallDuration -= (remainingDuration - newRemainingDuration);
		remainingDuration = newRemainingDuration;
		updateNotification(
				this.context
						.getString(R.string.service_postproc_notification_text_row2_action_process_video_recording),
				remainingDuration);

		// Process the video File and store the gaze points in the text file
		File gazePoints = processVideoFile(videoFileOutput,
				this.task.isConvertFPS(), this.task.isDrawOnVideo());

		// Update the Notification
		newRemainingDuration = this.task.calculateRemainingTime(false, false,
				false, true);
		this.remainingOverallDuration -= (remainingDuration - newRemainingDuration);
		remainingDuration = newRemainingDuration;
		updateNotification(
				this.context
						.getString(R.string.service_postproc_notification_text_row2_action_process_screen_recording),
				remainingDuration);

		// Process the screen recording by using the text file from produced in
		// step2
		processScreenFile(screenFileOutput, gazePoints,
				this.task.isDrawOnScreen());
	}

	private void updateNotification(String action, float remainingDuration) {
		DecimalFormat df = new DecimalFormat("#.##");
		String overallDurationString = df.format(this.remainingOverallDuration);
		String remainingDurationString = df.format(remainingDuration);

		String notificationText = this.mainNotificationText;
		notificationText += action + "\n"; // the current action
		notificationText += (this.context // the remaining time for this task
				.getString(R.string.service_postproc_notification_text_row3_remaining_time_for_this_task)
				+ " "
				+ remainingDurationString
				+ this.context
						.getString(R.string.service_postproc_notification_text_unit) + "\n");
		notificationText += (this.context // the remaining time for all tasks
				.getString(R.string.service_postproc_notification_text_row4_remaining_time_for_all_tests)
				+ " " + overallDurationString + this.context
				.getString(R.string.service_postproc_notification_text_unit));

		Intent intent = new Intent(
				PostProcessingService.RECEIVER_INTENT_PENDING_UPDATE);
		intent.putExtra(PostProcessingService.INTENT_ARG_NOTIFICATION_TEXT,
				notificationText);
		this.context.sendBroadcast(intent);
	}

	/**
	 * Converts
	 * 
	 * @param inputFilePath
	 * @param outputFilePath
	 * @param convertToCFR
	 * @return
	 */
	private File convertFPS(String inputFilePath, String outputFilePath,
			boolean convertToCFR) {
		File inputFile = new File(inputFilePath);
		if (convertToCFR) {
			File outputFile = new File(outputFilePath);
			ConvertFPS convertFPS = new ConvertFPS(this.context, inputFile,
					outputFile);
			convertFPS.convert();
			return outputFile;
		}
		return inputFile;
	}

	private File processVideoFile(File inputFile, boolean convertToCFR,
			boolean drawOnVideoRecording) {
		File outputFile = null;
		File textoutput = new File(FileManager.getPostProcTextFilePath(
				this.task.getSubdir(), convertToCFR));
		if (drawOnVideoRecording) {
			outputFile = new File(FileManager.getPostProcVideoOutputFilePath(
					this.task.getSubdir(), convertToCFR));
		}
		ProcessVideoRecording processor = new ProcessVideoRecording(
				this.context, inputFile, outputFile, textoutput,
				this.task.getSubdir());
		processor.processVideoRecording();
		return textoutput;
	}

	private void processScreenFile(File inputFile, File textFile,
			boolean drawOnScreenRecording) {
		if (drawOnScreenRecording && inputFile.exists()) {
			File outputFile = new File(
					FileManager.getPostProcScreenOutputFilePath(this.task
							.getSubdir()));
			ProcessScreenRecording process = new ProcessScreenRecording(
					inputFile, outputFile, textFile);
			process.processScreenRecording();
		}
	}
}