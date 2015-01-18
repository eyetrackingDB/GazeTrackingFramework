package de.vion.eyetracking.gazedetection.post.convertfps;

//This class is based on https://github.com/guardianproject/android-ffmpeg-java
//You can find the license of this project in the license folder
public interface ShellCallback {

	public void shellOut(String shellLine);

	public void processComplete(int exitValue);

}
