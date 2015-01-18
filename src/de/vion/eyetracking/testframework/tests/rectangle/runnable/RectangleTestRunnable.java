package de.vion.eyetracking.testframework.tests.rectangle.runnable;

import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Handler;
import de.vion.eyetracking.testframework.generic.GenericTestRunnable;
import de.vion.eyetracking.testframework.tests.rectangle.item.Rectangle;
import de.vion.eyetracking.testframework.tests.rectangle.logging.LogObjectRectangleCallback;
import de.vion.eyetracking.testframework.tests.rectangle.view.RectangleTestView;

/**
 * The runnable for a single sequence of the rectangle test
 * 
 * @author André Pomp
 * 
 */
public class RectangleTestRunnable extends GenericTestRunnable {

	private Handler handler;
	private RectangleTestView view;

	private int rectanglePos = -1;
	private int numberOfRectangles = 0;
	private int timePerRectangle = 0;
	private int pauseBetweenRectangles = 0;

	private MediaPlayer player;
	private ToneGenerator toneGenerator;
	private boolean playMusic = false;
	private boolean playTone = false;

	private LogObjectRectangleCallback logCallback;

	public RectangleTestRunnable(Handler handler, RectangleTestView view,
			int numberOfRectangles, int timePerRectangle,
			int pauseBetweenRectangles, MediaPlayer player, boolean playMusic,
			ToneGenerator toneGenerator, boolean playTone,
			LogObjectRectangleCallback logCallback) {
		super();
		this.view = view;
		this.handler = handler;
		this.numberOfRectangles = numberOfRectangles;
		this.timePerRectangle = timePerRectangle;
		this.pauseBetweenRectangles = pauseBetweenRectangles;
		this.player = player;
		this.playMusic = playMusic;
		this.toneGenerator = toneGenerator;
		this.playTone = playTone;
		this.logCallback = logCallback;
	}

	@Override
	public void run() {
		super.run();

		long startTime = System.currentTimeMillis();
		boolean updatedPause = false;
		boolean updatedRectangle = false;

		while (isRunning()) {
			long currentTime = System.currentTimeMillis();

			if ((currentTime - startTime) <= this.pauseBetweenRectangles) {
				if (!updatedPause) {
					// start music
					if (this.pauseBetweenRectangles > 0 && this.playMusic) {
						this.player.start();
					}

					// update UI
					this.handler.post(new Runnable() {
						@Override
						public void run() {
							RectangleTestRunnable.this.view.updateRectanglePos(
									RectangleTestRunnable.this.rectanglePos,
									true);
						}
					});

					updatedPause = true;
				}
			} else if ((currentTime - startTime) > this.pauseBetweenRectangles
					&& (currentTime - startTime) <= (this.pauseBetweenRectangles + this.timePerRectangle)) {
				if (!updatedRectangle) {
					updateView();
					updatedRectangle = true;
				}
			} else {
				updatedPause = false;
				updatedRectangle = false;
				startTime = System.currentTimeMillis();
			}
		}
		this.logCallback.onSequenceFinished();
	}

	private void updateView() {
		// stop music
		if (this.pauseBetweenRectangles > 0 && this.playMusic) {
			this.player.pause();
		}

		// play the beep
		if (this.playTone) {
			this.toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT);
		}

		this.rectanglePos = (this.rectanglePos + 1) % this.numberOfRectangles;

		// Check if we did all rectangles once
		if (this.rectanglePos == 0) {
			this.view.shuffleRectangles();
		}

		// Now get the rectangle and log it
		Rectangle rectangle = this.view.getRectangle(this.rectanglePos);
		this.logCallback.onRectangleStarted(rectangle.getTopLeft().x,
				rectangle.getTopLeft().y, rectangle.getBottomRight().x,
				rectangle.getBottomRight().y, rectangle.getRow(),
				rectangle.getCol(), System.currentTimeMillis());

		// update UI
		this.handler.post(new Runnable() {
			@Override
			public void run() {
				RectangleTestRunnable.this.view.updateRectanglePos(
						RectangleTestRunnable.this.rectanglePos, false);
			}
		});
	}
}