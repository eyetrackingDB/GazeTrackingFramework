package de.vion.eyetracking.testframework.tests.line.runnable;

import de.vion.eyetracking.testframework.generic.GenericTestRunnable;
import de.vion.eyetracking.testframework.tests.line.LineTestFragment;
import de.vion.eyetracking.testframework.tests.line.logging.LogObjectLineCallback;

/**
 * The runnable for the line test
 * 
 * @author André Pomp
 * 
 */
public class LineTestRunnable extends GenericTestRunnable {

	// The current values
	private static final int FRAMES_PER_SECOND = 30;
	private LineTestFragment fragment;
	private LogObjectLineCallback callback;

	public LineTestRunnable(LineTestFragment fragment,
			LogObjectLineCallback callback) {
		super();
		this.fragment = fragment;
		this.callback = callback;
	}

	@Override
	public void run() {
		super.run();

		// Log start the test was started
		this.callback.onTestStarted();

		long startTime = System.currentTimeMillis();

		this.fragment.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LineTestRunnable.this.fragment.updatePosition();
			}
		});

		while (isRunning()) {
			if ((System.currentTimeMillis() - startTime) > (1000 / FRAMES_PER_SECOND)) {
				this.fragment.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						LineTestRunnable.this.fragment.updatePosition();
					}
				});
				startTime = System.currentTimeMillis();
			}
		}
		this.callback.onTestFinished();
	}
}