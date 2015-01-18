package de.vion.eyetracking.gazedetection.post.convertfps;

import java.io.File;

import android.content.Context;

/**
 * Converts the FPS of a video
 * 
 * @author André Pomp
 * 
 */
public class ConvertFPS implements ShellCallback {

	private Context context;
	private File inputFile;
	private File outputFile;

	public ConvertFPS(Context context, File inputFile, File outputFile) {
		super();
		this.context = context;
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}

	public void convert() {
		// Get the library path
		Ffmpeg ffmpeg = Ffmpeg.getInstance(this.context);
		try {
			ffmpeg.convertFPS(this.inputFile, this.outputFile, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shellOut(String shellLine) {
		System.out.println(shellLine);
	}

	@Override
	public void processComplete(int exitValue) {
		System.out.println("Finished");
	}
}