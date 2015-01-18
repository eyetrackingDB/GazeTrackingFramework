package de.vion.eyetracking.testframework.generic;

/**
 * 
 * The runnable that must be inherited by each runnable of a test.
 * 
 * @author André Pomp
 * 
 */
public abstract class GenericTestRunnable implements Runnable {

	// Control variables
	private boolean isRunning = false;

	public GenericTestRunnable() {
		super();
	}

	@Override
	public void run() {
		this.isRunning = true;
	}

	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return this.isRunning;
	}

	/**
	 * @param isRunning
	 *            the isRunning to set
	 */
	public void stopRunning() {
		this.isRunning = false;
	}
}