package de.vion.eyetracking.cameracalib.calibration.callback;

import org.opencv.core.Mat;

/**
 * The callback for handling events during the calibration
 * 
 * @author André Pomp
 */
public interface CalibrationCallback {

	/**
	 * Called at the init
	 * 
	 * @param width
	 *            the current width of the frame
	 * @param height
	 *            the current height of the frame
	 */
	public void onInit(int width, int height);

	/**
	 * Called for each received frame
	 * 
	 * @param colorFrame
	 *            the frame in color
	 * @param greyFrame
	 *            the frame in grey
	 */
	public void onFrameReceived(Mat colorFrame, Mat greyFrame);

	/**
	 * Called if the display is touched
	 */
	public void onTouch();

	/**
	 * Called if the user wants to finish the calibration
	 */
	public void onFinishedPressed();

}
