package de.vion.eyetracking.testframework.tests.word.runnable;

import java.util.List;

import android.content.Intent;
import de.vion.eyetracking.RecordService;
import de.vion.eyetracking.testframework.generic.GenericTestRunnable;
import de.vion.eyetracking.testframework.tests.word.WordTestFragment;
import de.vion.eyetracking.testframework.tests.word.logging.LogObjectWordCallback;

/**
 * The runnable for the word test
 * 
 * @author André Pomp
 * 
 */
public class WordTestRunnable extends GenericTestRunnable {

	// The current values
	private int numberOfWords;
	private int timePerWord = 0;
	private WordTestFragment fragment;
	private List<Float> listOfFontSizes;
	private int coloredWordPos = 0;
	private int currentFont = 0;
	private LogObjectWordCallback callback;

	// Indicates if we start at the beginning again
	private boolean loopSequences = false;

	public WordTestRunnable(WordTestFragment fragment, int numberOfWords,
			int timePerWord, List<Float> listOfFontSizes,
			boolean loopSequences, LogObjectWordCallback callback) {
		super();
		this.numberOfWords = numberOfWords;
		this.fragment = fragment;
		this.timePerWord = timePerWord;
		this.listOfFontSizes = listOfFontSizes;
		this.loopSequences = loopSequences;
		this.callback = callback;
	}

	@Override
	public void run() {
		super.run();

		// Log start the test was started
		this.callback.onTestStarted();

		this.callback.onSequenceStarted(
				String.valueOf(this.listOfFontSizes.get(this.currentFont)),
				System.currentTimeMillis());

		long startTime = System.currentTimeMillis();

		this.fragment.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WordTestRunnable.this.fragment.updateText(
						WordTestRunnable.this.coloredWordPos,
						WordTestRunnable.this.listOfFontSizes
								.get(WordTestRunnable.this.currentFont));
			}
		});

		while (isRunning()) {
			// Check the current time for the active point
			if ((System.currentTimeMillis() - startTime) >= this.timePerWord) {
				startTime = System.currentTimeMillis(); // set a new start time
				this.coloredWordPos = (this.coloredWordPos + 1)
						% this.numberOfWords;

				// if colored word pos is 0 then we have to update the font size
				if (this.coloredWordPos == 0) {
					// Finish the old sequence
					this.callback.onSequenceFinished();

					if (this.loopSequences) {
						this.currentFont = (this.currentFont + 1)
								% this.listOfFontSizes.size();
					} else {
						this.currentFont = (this.currentFont + 1);
						if (this.currentFont == this.listOfFontSizes.size()) {
							this.fragment
									.getActivity()
									.sendBroadcast(
											new Intent(
													RecordService.RECEIVER_INTENT_ACTIVITY_STOP));
							return;
						}
					}

					// Start the new sequence
					this.callback.onSequenceStarted(
							String.valueOf(this.listOfFontSizes
									.get(this.currentFont)), System
									.currentTimeMillis());
				}

				this.fragment.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						WordTestRunnable.this.fragment
								.updateText(
										WordTestRunnable.this.coloredWordPos,
										WordTestRunnable.this.listOfFontSizes
												.get(WordTestRunnable.this.currentFont));
					}
				});
			}
		}
	}
}