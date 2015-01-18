package de.vion.eyetracking.recording;

import java.io.IOException;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import de.vion.eyetracking.misc.FileManager;

/**
 * 
 * The class that handles the recording of the camera
 * 
 * @author André Pomp
 * 
 */
@SuppressWarnings("deprecation")
public class CameraRecorder {

	private MediaRecorder recorder;
	private Camera camera;
	private String subDir;

	public CameraRecorder(String subDir) {
		super();
		this.subDir = subDir;
	}

	/**
	 * Starts the recording of the specified camera on the surface
	 * 
	 * @param holder
	 * @param frontCameraIndex
	 */
	public void startRecording(SurfaceHolder holder, int frontCameraIndex) {
		this.camera = Camera.open(frontCameraIndex);

		this.recorder = new MediaRecorder();
		this.recorder.setCamera(this.camera); // this must be done first

		// small trick to ensure that the camera is free (otherwise we get
		// exceptions)
		this.camera.lock();
		this.camera.unlock();

		this.recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		this.recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		this.recorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_720P));
		this.recorder.setOutputFile(FileManager
				.getVideoRecordingFilePath(this.subDir));
		this.recorder.setPreviewDisplay(holder.getSurface());
		try {
			this.recorder.prepare();
			this.recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops the recording
	 */
	public void stopRecording() {
		try {
			this.recorder.stop();
		} catch (RuntimeException e) {
			// This catch is required to prevent the crash of the app
			// Usually stop throws is exception if it is called to fast after
			// start
			// See android documentation for more information
		}
		this.recorder = null;
		this.camera.release();
	}
}