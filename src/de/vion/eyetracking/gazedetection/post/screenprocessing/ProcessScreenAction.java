package de.vion.eyetracking.gazedetection.post.screenprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import de.vion.eyetracking.gazedetection.post.misc.FrameAction;

/**
 * The class for processing the screen action
 * 
 * @author André Pomp
 * 
 */
public class ProcessScreenAction implements FrameAction {

	// Constructor values
	private File textInputFile;

	// Reading values
	private BufferedReader reader;

	public ProcessScreenAction(File textInputFile) {
		super();
		this.textInputFile = textInputFile;
	}

	@Override
	public void start() throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(this.textInputFile), "utf-8"));
	}

	@Override
	public void processFrame(Mat frame) throws IOException {
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2BGR);

		// Check if we have a line (if not the video stream is longer than the
		// video frame)
		String line = this.reader.readLine();
		if (line != null) {
			String[] splittedLine = line.split(";");
			if (splittedLine[1].equals("NULL")) {
				// do nothing => no gaze point for this frame
			} else {
				// draw the gaze point
				Core.circle(frame, new Point(Integer.valueOf(splittedLine[1]),
						Integer.valueOf(splittedLine[2])), 20, new Scalar(0,
						255, 255), -1);
			}
		}

		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGBA);
	}

	@Override
	public void stop() throws IOException {
		this.reader.close();
	}
}