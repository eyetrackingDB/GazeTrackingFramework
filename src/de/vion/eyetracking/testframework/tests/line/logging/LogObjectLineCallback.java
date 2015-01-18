package de.vion.eyetracking.testframework.tests.line.logging;

/**
 * The callbacks for the line logging
 * 
 * @author André Pomp
 * 
 */
public interface LogObjectLineCallback {

	public void onTestStarted();

	public void onPositionUpdated(long time, int lineY, int lineNumber);

	public void onTestFinished();

}
