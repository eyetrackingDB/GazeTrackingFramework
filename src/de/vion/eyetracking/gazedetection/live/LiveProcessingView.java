package de.vion.eyetracking.gazedetection.live;

import java.util.List;

import org.opencv.android.JavaCameraView;
import org.opencv.core.Size;

import android.content.Context;
import android.util.AttributeSet;
import de.vion.eyetracking.settings.GazeTrackingPreferences;

/**
 * The view for the live camera gaze tracking
 * 
 * @author André Pomp
 * 
 */
public class LiveProcessingView extends JavaCameraView {

	private Context context;

	public LiveProcessingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected org.opencv.core.Size calculateCameraFrameSize(
			List<?> supportedSizes, ListItemAccessor accessor,
			int surfaceWidth, int surfaceHeight) {

		int[] res = GazeTrackingPreferences.getCameraResolution(this.context);
		return new Size(res[0], res[1]);
	}
}