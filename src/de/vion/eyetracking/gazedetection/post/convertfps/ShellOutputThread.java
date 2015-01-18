package de.vion.eyetracking.gazedetection.post.convertfps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//This class is based on https://github.com/guardianproject/android-ffmpeg-java
//You can find the license of this project in the license folder
public class ShellOutputThread extends Thread {
	private InputStream is;
	private ShellCallback sc;

	ShellOutputThread(InputStream is, ShellCallback sc) {
		this.is = is;
		this.sc = sc;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(this.is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (this.sc != null) {
					this.sc.shellOut(line);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
