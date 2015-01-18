package de.vion.eyetracking.gazedetection.post.videoprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.opencv.core.Mat;

import android.content.Context;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerAbstract;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerFactory;
import de.vion.eyetracking.gazedetection.gazetracker.GazeTrackerSettingsAbstract;
import de.vion.eyetracking.gazedetection.post.misc.FrameAction;

/**
 * The class for processing the video action
 * 
 * @author André Pomp
 * 
 */
public class ProcessVideoAction implements FrameAction {

	// Constructor values
	private File textOutputFile;
	private String settingsDirectory;

	// Writing values
	private Writer writer;

	// The frame counter
	private int frameCounter = 0;

	// The gaze tracker
	private GazeTrackerAbstract tracker;

	public ProcessVideoAction(String settingsDirectory, File textOutputFile,
			Context context) {
		super();
		this.textOutputFile = textOutputFile;
		this.settingsDirectory = settingsDirectory;
		this.tracker = GazeTrackerFactory.createGazeTracker(context);
	}

	@Override
	public void start() throws IOException {
		// Init the gaze tracker
		initGazeTracker();

		// Init the writer
		this.writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(this.textOutputFile, false), "utf-8"));
	}

	@Override
	public void processFrame(Mat frame) throws IOException {
		int[] result = this.tracker.detect(frame);

		String output = this.frameCounter + ";";
		if (result == null) {
			output += "NULL\n";
		} else {
			output += result[0] + ";";
			output += result[1] + "\n";
		}
		this.writer.append(output);
		this.frameCounter++;
	}

	@Override
	public void stop() throws IOException {
		this.writer.close();
	}

	private void initGazeTracker() {
		GazeTrackerSettingsAbstract settings = GazeTrackerFactory
				.createGazeTrackerSettings();
		settings.loadSettingsFromFile(this.settingsDirectory);
		this.tracker.init(settings);
	}
}