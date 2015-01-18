package de.vion.eyetracking.gazedetection.post.videoprocessing;

import java.io.File;

import android.content.Context;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerFactory;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerSettingsAbstract;
import de.vion.eyetracking.gazedetection.post.misc.FrameProcessor;

/**
 * The class for processing the video recording
 * 
 * @author André Pomp
 * 
 */
public class ProcessVideoRecording {

	private Context context;
	private File videoRecordingInput;
	private File videoRecordingOutput;
	private File textOutput;
	private String settingsDirectory;

	public ProcessVideoRecording(Context context, File videoRecordingInput,
			File videoRecordingOutput, File textOutput, String settingsDirectory) {
		super();
		this.context = context;
		this.videoRecordingInput = videoRecordingInput;
		this.videoRecordingOutput = videoRecordingOutput;
		this.textOutput = textOutput;
		this.settingsDirectory = settingsDirectory;
	}

	public void processVideoRecording() {
		// Check if we have to rotate the output
		GazeTrackerSettingsAbstract settings = GazeTrackerFactory
				.createGazeTrackerSettings();
		settings.loadSettingsFromFile(this.settingsDirectory);

		boolean rotateOutput = false;
		if (settings.getOrientation().equals("LANDSCAPE")
				|| settings.getOrientation().equals("LANDSCAPE_REVERSE")) {
			rotateOutput = false;
		} else {
			rotateOutput = true;
		}
		FrameProcessor eyeTracking = new FrameProcessor(new ProcessVideoAction(
				this.settingsDirectory, this.textOutput, this.context),
				rotateOutput);
		eyeTracking.processVideo(this.videoRecordingInput,
				this.videoRecordingOutput);
	}
}