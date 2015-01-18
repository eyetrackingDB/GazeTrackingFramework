package de.vion.eyetracking.gazedetection.post.convertfps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;

// This class is based on the idea of https://github.com/guardianproject/android-ffmpeg-java
// You can find the license of this project in the license folder
// The code was adopted to my needs and the libffmpeg was compiled myself (did not use the version in the repository)
public class Ffmpeg {

	private File ffmpegBinDir;
	private String ffmpegBinFilePath;

	public static Ffmpeg getInstance(Context context) {
		Ffmpeg instance = new Ffmpeg();
		instance.init(context);
		return instance;
	}

	private Ffmpeg() {
		super();
		// force to use getInstance
	}

	private void init(Context context) {
		try {
			this.ffmpegBinDir = new File(context.getFilesDir().getParentFile(),
					"lib");
			this.ffmpegBinFilePath = new File(this.ffmpegBinDir, "libffmpeg.so")
					.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	// ffmpeg -i input.mp4 -r 30 -vcodec libx264 -profile:v baseline
	// -level 3.1 -crf 1 output.mp4
	public void convertFPS(File inputFile, File outputFile, ShellCallback sc)
			throws Exception {
		ArrayList<String> cmd = new ArrayList<String>();

		cmd.add(this.ffmpegBinFilePath);
		cmd.add("-y");

		// Add the Input File
		cmd.add("-i");
		cmd.add(inputFile.getCanonicalPath());

		// Add the Framerate
		cmd.add("-r");
		cmd.add("30");

		// Add the libx264 codec
		cmd.add("-vcodec");
		cmd.add("libx264");

		// Add the profile
		cmd.add("-profile:v");
		cmd.add("baseline");

		// Add the level of the profile
		cmd.add("-level");
		cmd.add("3.1");

		// Add the Quality
		cmd.add("-crf");
		cmd.add("1");

		// remove audio
		cmd.add("-an");

		// Add the Output File
		cmd.add(outputFile.getCanonicalPath());

		// Execute the conversion
		execFFMPEG(cmd, sc);
	}

	private void execFFMPEG(List<String> cmd, ShellCallback sc)
			throws IOException, InterruptedException {

		String runtimeCmd = new File(this.ffmpegBinDir, "ffmpeg")
				.getCanonicalPath();

		Runtime.getRuntime().exec("chmod 777 " + runtimeCmd);

		execProcess(cmd, sc, this.ffmpegBinDir);
	}

	private void execProcess(List<String> cmds, ShellCallback sc, File fileExec)
			throws IOException, InterruptedException {

		// ensure that the arguments are in the correct Locale format
		for (String cmd : cmds) {
			cmd = String.format(Locale.US, "%s", cmd);
		}

		ProcessBuilder pb = new ProcessBuilder(cmds);
		pb.directory(fileExec);

		StringBuffer cmdlog = new StringBuffer();

		for (String cmd : cmds) {
			cmdlog.append(cmd);
			cmdlog.append(' ');
		}

		sc.shellOut(cmdlog.toString());

		Process process = pb.start();

		ShellOutputThread errorThread = new ShellOutputThread(
				process.getErrorStream(), sc);
		ShellOutputThread outputThread = new ShellOutputThread(
				process.getInputStream(), sc);
		errorThread.start();
		outputThread.start();

		int exitVal = process.waitFor();
		sc.processComplete(exitVal);
	}
}