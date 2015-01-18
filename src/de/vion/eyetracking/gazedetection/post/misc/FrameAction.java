package de.vion.eyetracking.gazedetection.post.misc;

import java.io.IOException;

import org.opencv.core.Mat;

/**
 * The actions that are possible for a frame
 * 
 * @author André Pomp
 * 
 */
public interface FrameAction {

	public void start() throws IOException;

	public void processFrame(Mat frame) throws IOException;

	public void stop() throws IOException;

}
