package de.vion.eyetracking.testframework.tests.screen.logging;

/**
 * The callbacks for the logging of second screen actions
 * 
 * @author André Pomp
 * 
 */
public interface LogObjectScreenCallback {

	public void onTestStarted();

	public void onActionChanged(long time, String action);

	public void onTestFinished();

}
