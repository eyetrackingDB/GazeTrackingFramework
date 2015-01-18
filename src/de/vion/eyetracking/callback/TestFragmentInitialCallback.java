package de.vion.eyetracking.callback;

/**
 * The Callback for the Initial Fragment displayed before starting a test
 * 
 * @author André Pomp
 * 
 */
public interface TestFragmentInitialCallback {

	/**
	 * Called if start was clicked
	 */
	public void onStartClicked();

	/**
	 * Called if ShowDemo was clicked
	 */
	public void onShowDemoClicked();

	/**
	 * Called if align face was clicked
	 */
	public void onAlignFaceClicked();

}
