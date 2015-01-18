package de.vion.eyetracking.gazedetection.post;

import java.io.File;
import java.io.Serializable;

import android.media.MediaMetadataRetriever;
import de.vion.eyetracking.misc.FileManager;

/**
 * The class that represents a single processing task
 * 
 * @author André Pomp
 * 
 */
public class PostProcessingTask implements Serializable {

	/**
	 * The number of frames which we assume for a video per second
	 */
	private static final int PROCESSING_FRAMES_PER_SECOND = 30;

	/**
	 * The estimated time (in ms) which we need to convert a single frame of a
	 * video recording
	 */
	private static final int PROCESSING_TIME_CONVERT_FPS_VIDEO = 100;

	/**
	 * The estimated time (in ms) which we need to convert a single frame of a
	 * screen recording
	 */
	private static final int PROCESSING_TIME_CONVERT_FPS_SCREEN = 100;

	/**
	 * The estimated time (in ms) which we need to process a single frame with
	 * eyetab
	 */
	private static final int PROCESSING_TIME_PER_FRAME_VIDEO = 700;

	/**
	 * The estimated time (in ms) which we need to process a single frame of the
	 * screen recording
	 */
	private static final int PROCESSING_TIME_PER_FRAME_SCREEN = 100;

	private static final long serialVersionUID = -6668285161418929787L;

	private long videoDuration;
	private long screenDuration;
	private boolean convertFPS;
	private boolean drawOnVideo;
	private boolean drawOnScreen;
	private String subdir;

	public PostProcessingTask(boolean convertFPS, boolean drawOnVideo,
			boolean drawOnScreen, String subdir) {
		super();
		this.convertFPS = convertFPS;
		this.drawOnVideo = drawOnVideo;
		this.drawOnScreen = drawOnScreen;
		this.subdir = subdir;
		this.videoDuration = calculateVideoDuration(FileManager
				.getVideoRecordingFilePath(subdir));
		this.screenDuration = calculateVideoDuration(FileManager
				.getFinalScreenRecordingName(subdir));
	}

	private long calculateVideoDuration(String filepath) {
		if (new File(filepath).exists()) {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(filepath);
			String time = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			long timeInmillisec = Long.parseLong(time);
			return (timeInmillisec / 1000);
		}
		return 0;
	}

	public boolean isConvertFPS() {
		return this.convertFPS;
	}

	public boolean isDrawOnVideo() {
		return this.drawOnVideo;
	}

	public boolean isDrawOnScreen() {
		return this.drawOnScreen;
	}

	public String getSubdir() {
		return this.subdir;
	}

	public float calculateRemainingTime(boolean convertVideo,
			boolean convertScreen, boolean videoProcessing,
			boolean screenProcessing) {

		float completeTime = 0;
		if (convertVideo && this.convertFPS) {
			completeTime += (this.videoDuration * PROCESSING_FRAMES_PER_SECOND * PROCESSING_TIME_CONVERT_FPS_VIDEO)
					/ (1000.0 * 60.0);
		}

		if (convertScreen && this.convertFPS) {
			completeTime += (this.screenDuration * PROCESSING_FRAMES_PER_SECOND * PROCESSING_TIME_CONVERT_FPS_SCREEN)
					/ (1000.0 * 60.0);
		}

		if (videoProcessing) {
			completeTime += (this.videoDuration * PROCESSING_FRAMES_PER_SECOND * PROCESSING_TIME_PER_FRAME_VIDEO)
					/ (1000.0 * 60.0);
		}

		if (screenProcessing && this.drawOnScreen) {
			completeTime += (this.screenDuration * PROCESSING_FRAMES_PER_SECOND * PROCESSING_TIME_PER_FRAME_SCREEN)
					/ (1000.0 * 60.0);
		}

		return completeTime;
	}
}