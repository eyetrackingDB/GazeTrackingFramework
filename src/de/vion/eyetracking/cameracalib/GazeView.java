package de.vion.eyetracking.cameracalib;

import java.util.List;

import org.opencv.android.JavaCameraView;
import org.opencv.core.Size;

import android.content.Context;
import android.util.AttributeSet;
import de.vion.eyetracking.settings.GazeTrackingPreferences;

/**
 * The view for showing the current camera picture based on OpenCV
 * JavaCameraView
 * 
 * @author André Pomp
 * 
 */
public class GazeView extends JavaCameraView {

	private Context context;

	public GazeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected org.opencv.core.Size calculateCameraFrameSize(
			List<?> supportedSizes, ListItemAccessor accessor,
			int surfaceWidth, int surfaceHeight) {

		// Get the current camera resolution
		int[] res = GazeTrackingPreferences.getCameraResolution(this.context);
		return new Size(res[0], res[1]);
	}
}