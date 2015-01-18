package de.vion.eyetracking.cameracalib.calibration.callback;

/**
 * Callback if the calibration was finished
 * 
 * @author André Pomp
 */
public interface CalibrationAsyncTaskCallback {

	/**
	 * Called if calibration is finished
	 */
	public void onTaskFinished();

}
