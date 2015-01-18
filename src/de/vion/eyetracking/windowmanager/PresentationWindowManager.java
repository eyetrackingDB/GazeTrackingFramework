package de.vion.eyetracking.windowmanager;

import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import de.vion.eyetracking.callback.WindowManagerCallback;

/**
 * 
 * Manages the window for the second screen that is, e.g., attached for playing
 * videos on the second screen.
 * 
 * @author André Pomp
 * 
 */
public class PresentationWindowManager implements SurfaceHolder.Callback {

	private WindowManager windowManager;
	private Context context;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private WindowManagerCallback callback;

	private MediaPlayer mediaPlayer;
	private String videoFilePath;

	public PresentationWindowManager(Context context,
			WindowManagerCallback callback, String videoFilePath) {
		super();
		this.context = context;
		this.callback = callback;
		this.windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setLooping(true);
		this.videoFilePath = videoFilePath;
	}

	public void initViews() {
		// Setup the surface view
		this.surfaceView = new SurfaceView(this.context);
		this.surfaceHolder = this.surfaceView.getHolder();
		this.surfaceHolder.addCallback(this);

		// Window params
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0, 0,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 0,
				PixelFormat.UNKNOWN);

		// Add the view to this window manager
		this.windowManager.addView(this.surfaceView, params);
	}

	public void start() {
		// Init the media player => saves time
		try {
			this.mediaPlayer.setDataSource(this.videoFilePath);
			this.mediaPlayer.prepare();
			this.mediaPlayer.setDisplay(this.surfaceHolder);
			this.mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		this.mediaPlayer.stop();
		this.mediaPlayer.setDisplay(null);
		this.mediaPlayer.release();
		this.mediaPlayer = null;

		this.windowManager.removeView(this.surfaceView);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// callback that the surface was created
		this.callback.onPresentationWindowFinished();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// do nothing
	}
}