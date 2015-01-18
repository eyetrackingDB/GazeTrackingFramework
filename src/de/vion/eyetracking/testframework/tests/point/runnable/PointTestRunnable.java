package de.vion.eyetracking.testframework.tests.point.runnable;

import android.os.Handler;
import de.vion.eyetracking.testframework.generic.GenericTestRunnable;
import de.vion.eyetracking.testframework.tests.point.item.PointItem;
import de.vion.eyetracking.testframework.tests.point.logging.LogObjectPointCallback;
import de.vion.eyetracking.testframework.tests.point.view.PointTestView;

/**
 * The runnable for the point test
 * 
 * @author André Pomp
 * 
 */
public class PointTestRunnable extends GenericTestRunnable {

	// The current values
	private PointTestView testView;
	private Handler handler;
	private LogObjectPointCallback callback;

	private int currentPoint = 0;
	private int currentRepetition = 0;
	private int timePerPoint;
	private int numberOfPoints;
	private int numberOfRepetitions;

	public PointTestRunnable(PointTestView testView,
			LogObjectPointCallback callback, Handler handler, int timePerPoint,
			int numberOfPoints, int numberOfRepetitions) {
		super();
		this.testView = testView;
		this.handler = handler;
		this.callback = callback;
		this.timePerPoint = timePerPoint;
		this.numberOfPoints = numberOfPoints;
		this.numberOfRepetitions = numberOfRepetitions;
	}

	@Override
	public void run() {
		super.run();

		// Log start the test was started
		this.callback.onTestStarted();

		// Show the first point
		PointItem firstPoint = this.testView.getPoint(this.currentPoint);

		this.callback.onPointStarted(firstPoint.getCenter().x,
				firstPoint.getCenter().y, System.currentTimeMillis());

		// update UI
		this.handler.post(new Runnable() {
			@Override
			public void run() {
				PointTestRunnable.this.testView
						.updatePointPos(PointTestRunnable.this.currentPoint);
			}
		});

		long startTime = System.currentTimeMillis();
		while (isRunning()) {
			if (System.currentTimeMillis() - startTime > this.timePerPoint) {
				this.currentPoint = (this.currentPoint + 1)
						% this.numberOfPoints;

				// Check if a repetition takes place
				if (this.currentPoint == 0) {
					this.currentRepetition += 1;
					if (this.currentRepetition >= this.numberOfRepetitions) {
						// stop after the number of repetitions
						break;
					}
				}

				// Now get the point and log it
				PointItem point = this.testView.getPoint(this.currentPoint);
				this.callback.onPointStarted(point.getCenter().x,
						point.getCenter().y, System.currentTimeMillis());

				// update UI
				this.handler.post(new Runnable() {
					@Override
					public void run() {
						PointTestRunnable.this.testView
								.updatePointPos(PointTestRunnable.this.currentPoint);
					}
				});

				// Reset the time
				startTime = System.currentTimeMillis();
			}
		}
		this.callback.onTestFinished();
	}
}