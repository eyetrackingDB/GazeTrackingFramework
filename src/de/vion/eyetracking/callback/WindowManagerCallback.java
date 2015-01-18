package de.vion.eyetracking.callback;

/**
 * The Callback for the window manager classes
 * 
 * @author André Pomp
 * 
 */
public interface WindowManagerCallback {

	/**
	 * Called if the main window was initialized
	 */
	public void onMainWindowFinished();

	/**
	 * Called if the presentation window was initialized
	 */
	public void onPresentationWindowFinished();

}
