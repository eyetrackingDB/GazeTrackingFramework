package de.vion.eyetracking.testframework.tests.rectangle.runnable;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Handler;
import android.view.ViewGroup;
import de.vion.eyetracking.RecordService;
import de.vion.eyetracking.testframework.tests.rectangle.item.RectangleProfile;
import de.vion.eyetracking.testframework.tests.rectangle.logging.LogObjectRectangleCallback;
import de.vion.eyetracking.testframework.tests.rectangle.view.RectangleTestView;
import de.vion.eyetracking.testframework.tests.rectangle.view.RectangleTestViewCreatedCallback;

/**
 * The runnable for the sequences of the rectangle test
 * 
 * @author André Pomp
 * 
 */
public class RectangleControlRunnable implements Runnable,
		RectangleTestViewCreatedCallback {

	// Fragment, View and Activity values
	private RectangleTestRunnable testRunnable;
	private ViewGroup containerView;
	private RectangleTestView testView;
	private Context context;
	private Handler handler;

	// Profile Values
	private List<RectangleProfile> profiles;
	private int currentProfile = -1;
	private boolean loopProfile = false;

	// Settings
	private int timePerSequence;
	private int timePerRectangle;
	private int pauseBetweenRectangles;

	private ToneGenerator toneGenerator;
	private boolean playTone;

	private MediaPlayer player;
	private boolean playMusic;

	// Logging callback
	private LogObjectRectangleCallback logCallback;
	private boolean initialLogCreated = false;

	public RectangleControlRunnable(ViewGroup containerView, Context context,
			Handler handler, List<RectangleProfile> profiles,
			boolean loopProfile, int timePerSequence, int timePerRectangle,
			int pauseBetweenRectangles, ToneGenerator toneGenerator,
			boolean playTone, MediaPlayer player, boolean playMusic,
			LogObjectRectangleCallback logCallback) {
		super();
		this.containerView = containerView;
		this.context = context;
		this.handler = handler;
		this.profiles = profiles;
		this.loopProfile = loopProfile;
		this.timePerSequence = timePerSequence;
		this.timePerRectangle = timePerRectangle;
		this.pauseBetweenRectangles = pauseBetweenRectangles;
		this.toneGenerator = toneGenerator;
		this.playTone = playTone;
		this.player = player;
		this.playMusic = playMusic;
		this.logCallback = logCallback;
	}

	@Override
	public void run() {
		// if the old runnable is still running => stop it
		if (this.testRunnable != null) {
			this.testRunnable.stopRunning();
		}

		// Create the log file
		if (!this.initialLogCreated) {
			this.logCallback.onTestStarted();
			this.initialLogCreated = true;
		}

		// Remove the old view
		this.containerView.removeView(this.testView);

		// Update the current profile or stop it if all sequences were
		// tested and no loop is required
		if (this.loopProfile) {
			this.currentProfile = (this.currentProfile + 1)
					% this.profiles.size();
		} else {
			this.currentProfile = (this.currentProfile + 1);
			if (this.currentProfile == this.profiles.size()) {
				this.context.sendBroadcast(new Intent(
						RecordService.RECEIVER_INTENT_ACTIVITY_STOP));
				return;
			}
		}

		// Create the new view
		this.testView = new RectangleTestView(this.context);
		this.testView.setRectangles(this.profiles.get(this.currentProfile)
				.getNumberOfRows(), this.profiles.get(this.currentProfile)
				.getNumberOfColumns());
		this.testView.setCallback(this);
		this.containerView.addView(this.testView);
	}

	public void stopRunnable() {
		if (this.testRunnable != null) {
			this.testRunnable.stopRunning();
		}

		if (this.testView != null) {
			this.containerView.removeView(this.testView);
		}

		// Stop the media player if it was used
		if (this.pauseBetweenRectangles > 0 && this.playMusic) {
			this.player.stop();
		}
		this.player.release();
	}

	@Override
	public void rectanglesCreated() {
		// Start the new runnable
		this.testRunnable = new RectangleTestRunnable(this.handler,
				this.testView, this.profiles.get(this.currentProfile)
						.getNumberOfRectangles(), this.timePerRectangle,
				this.pauseBetweenRectangles, this.player, this.playMusic,
				this.toneGenerator, this.playTone, this.logCallback);

		// Log that the sequence started
		this.logCallback.onSequenceStarted(
				this.profiles.get(this.currentProfile).getNumberOfRows(),
				this.profiles.get(this.currentProfile).getNumberOfColumns(),
				System.currentTimeMillis());

		new Thread(this.testRunnable).start();

		// Redo this after a certain time
		this.handler.postDelayed(this, this.timePerSequence);
	}
}