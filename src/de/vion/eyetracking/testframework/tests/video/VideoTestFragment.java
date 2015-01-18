package de.vion.eyetracking.testframework.tests.video;

import java.io.File;
import java.io.IOException;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestFragment;

/**
 * The Fragment for the video test.
 * 
 * @author André Pomp
 * 
 */
public class VideoTestFragment extends GenericTestFragment implements
		SurfaceHolder.Callback {

	public static VideoTestFragment createInstance(TestType type,
			String abbreviation, String subdir, boolean demo) {
		VideoTestFragment fragment = new VideoTestFragment();
		fragment.setArguments(createArgs(type, abbreviation, subdir, demo));
		return fragment;
	}

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private MediaPlayer mediaPlayer;
	private File videoFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.videoFile = new File(
				((VideoTestSettings) this.settings)
						.getVideoFilePath(getActivity()));

		// Init the MediaPlayer
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setLooping(((VideoTestSettings) this.settings)
				.isLooping(getActivity()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.test_fragment_video,
				container, false);

		this.surfaceView = (SurfaceView) rootView
				.findViewById(R.id.test_fragment_video_sv);
		this.surfaceHolder = this.surfaceView.getHolder();
		this.surfaceHolder.addCallback(this);

		return rootView;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// do nothing
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// Init the media player => saves time
		try {
			this.mediaPlayer.setDataSource(this.videoFile.getAbsolutePath());
			this.mediaPlayer.prepare();

			int videoWidth = this.mediaPlayer.getVideoWidth();
			int videoHeight = this.mediaPlayer.getVideoHeight();

			// Keep Aspect ratio
			// See:
			// http://stackoverflow.com/questions/4835060/android-mediaplayer-how-to-use-surfaceview-or-mediaplayer-to-play-video-in-cor

			Point screenDim = new Point();
			getActivity().getWindowManager().getDefaultDisplay()
					.getSize(screenDim);
			ViewGroup.LayoutParams lp = this.surfaceView.getLayoutParams();
			lp.width = screenDim.x;
			lp.height = (int) (((float) videoHeight / (float) videoWidth) * screenDim.x);
			this.surfaceView.setLayoutParams(lp);

			// Set the display
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

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.mediaPlayer.stop();
		this.mediaPlayer.setDisplay(null);
		this.mediaPlayer.release();
		this.mediaPlayer = null;
	}
}