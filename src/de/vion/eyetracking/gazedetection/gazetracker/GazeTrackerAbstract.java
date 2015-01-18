package de.vion.eyetracking.gazedetection.gazetracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.core.Mat;

import android.content.Context;

/**
 * 
 * The Abstract class that defines the methods for each gaze tracker that we
 * want to implement.
 * 
 * @author André Pomp
 * 
 */
public abstract class GazeTrackerAbstract {

	protected Context context;
	protected long nativeObjectAddress;

	public GazeTrackerAbstract(Context context) {
		super();
		this.context = context;
	}

	/**
	 * Handles the initilization of the gaze tracking approach
	 * 
	 * @param settings
	 */
	public abstract void init(GazeTrackerSettingsAbstract settings);

	/**
	 * Handles the detection of the gaze based on a frame
	 * 
	 * @param colorFrame
	 * @return
	 */
	public abstract int[] detect(Mat colorFrame);

	/**
	 * Releases all resources
	 */
	public abstract void release();

	/**
	 * Loads a single file from the resources
	 * 
	 * @param dir
	 * @param res
	 * @param filename
	 */
	protected File loadHaarFile(File dir, int res, String filename) {
		try {
			InputStream is = this.context.getResources().openRawResource(res);
			File target = new File(dir, filename);
			FileOutputStream os = new FileOutputStream(target);
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();
			return target;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}