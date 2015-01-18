package de.vion.eyetracking.testframework.tests.rectangle.logging;

/**
 * The callbacks for the logging of rectangles
 * 
 * @author André Pomp
 * 
 */
public interface LogObjectRectangleCallback {

	public void onTestStarted();

	public void onSequenceStarted(int numberOfRows, int numberOfCols,
			long startTime);

	public void onRectangleStarted(int topLeftX, int topLeftY,
			int bottomRightX, int bottomRightY, int row, int col, long startTime);

	public void onSequenceFinished();

}
