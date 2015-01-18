package de.vion.eyetracking.testframework.tests.screen.runnable;

import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.media.ToneGenerator;
import de.vion.eyetracking.testframework.generic.GenericTestRunnable;
import de.vion.eyetracking.testframework.tests.screen.ScreenTestFragment;
import de.vion.eyetracking.testframework.tests.screen.item.ScreenAction;
import de.vion.eyetracking.testframework.tests.screen.logging.LogObjectScreenCallback;

/**
 * The runnable for the second screen test
 * 
 * @author André Pomp
 * 
 */
public class ScreenTestRunnable extends GenericTestRunnable {

	// true of we are currently gazing on the screen
	private boolean onScreen = true;

	private int timeOnScreen;
	private int timeBesideScreen;
	private int numberOfRepetitions;
	private int currentRepetition = 1;
	private int currentPosition = 0;
	private List<ScreenAction> actions = new ArrayList<ScreenAction>();

	// The current values
	private ScreenTestFragment fragment;
	private LogObjectScreenCallback callback;

	// Tone Generator if we have to gaze at the screen
	private ToneGenerator toneGenerator;

	public ScreenTestRunnable(int timeOnScreen, int timeBesideScreen,
			int numberOfRepetitions, ScreenTestFragment fragment,
			LogObjectScreenCallback callback) {
		super();
		this.timeOnScreen = timeOnScreen;
		this.timeBesideScreen = timeBesideScreen;
		this.numberOfRepetitions = numberOfRepetitions;
		this.fragment = fragment;
		this.callback = callback;

		this.toneGenerator = new ToneGenerator(
				AudioManager.STREAM_NOTIFICATION, 100);

		this.actions.add(new ScreenAction("LEFT", "\u21E6"));
		this.actions.add(new ScreenAction("RIGHT", "\u21E8"));
		this.actions.add(new ScreenAction("ABOVE", "\u21E7"));
	}

	@Override
	public void run() {
		super.run();

		// Log start the test was started
		this.callback.onTestStarted();

		// Start the test
		long startTime = System.currentTimeMillis();

		// we start with the screen
		this.fragment.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ScreenTestRunnable.this.fragment.updateAction("\u25A1");
			}
		});
		this.callback.onActionChanged(startTime, "SCREEN");

		while (isRunning()) {
			long currentTime = System.currentTimeMillis();

			if (this.onScreen) {
				if ((currentTime - startTime) >= this.timeOnScreen) {
					// Check for repetitions
					if (this.currentPosition == this.actions.size()) {
						if (this.currentRepetition == this.numberOfRepetitions) {
							break; // test finished
						} else {
							this.currentPosition = 0;
							this.currentRepetition++;
						}
					}

					final ScreenAction action = ScreenTestRunnable.this.actions
							.get(ScreenTestRunnable.this.currentPosition);

					// Update the action
					this.fragment.getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ScreenTestRunnable.this.fragment
									.updateAction(action.getUnicode());
						}
					});

					this.callback
							.onActionChanged(currentTime, action.getText());

					// reset the time
					startTime = System.currentTimeMillis();
					this.onScreen = false;

					// update the positon
					this.currentPosition++;
				}
			} else {
				if ((currentTime - startTime) >= this.timeBesideScreen) {
					// Play a tone
					this.toneGenerator
							.startTone(ToneGenerator.TONE_PROP_PROMPT);

					// Update the action
					this.fragment.getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ScreenTestRunnable.this.fragment
									.updateAction("\u25A1");
						}
					});

					// Update the logging
					this.callback.onActionChanged(currentTime, "SCREEN");

					// reset the time
					startTime = System.currentTimeMillis();
					this.onScreen = true;
				}
			}
		}
		this.callback.onTestFinished();
	}
}