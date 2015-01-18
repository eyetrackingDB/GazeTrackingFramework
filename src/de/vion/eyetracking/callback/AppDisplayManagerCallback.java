package de.vion.eyetracking.callback;

/**
 * The Callback for the AppDisplay Manager
 * 
 * @author André Pomp
 * 
 */
public interface AppDisplayManagerCallback {

	/**
	 * Called when all displays are initialized
	 */
	public void onDisplaysInitialized();

	/**
	 * Called if a display was removed
	 */
	public void onDisplayRemoved();

}
