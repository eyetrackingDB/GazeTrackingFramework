package de.vion.eyetracking.gazedetection.post.misc;

import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.FrameRecorder;
import org.opencv.core.Mat;

import android.util.Log;

/**
 * The class for processing a frame
 * 
 * @author André Pomp
 * 
 */
public class FrameProcessor {

	private static final String TAG = "OPENCV TEST2 - FrameProcessor";

	private FrameAction action;
	private boolean rotateOutput;

	public FrameProcessor(FrameAction action, boolean rotateOutput) {
		this.action = action;
		this.rotateOutput = rotateOutput;
	}

	public void processVideo(File videoInputFile, File videoOutputFile) {
		try {
			// Init the grabber
			FrameGrabber grabber = new FFmpegFrameGrabber(
					videoInputFile.getCanonicalPath());
			grabber.start();

			// Init the recorder
			FrameRecorder recorder = null;
			if (videoOutputFile != null) {
				if (!this.rotateOutput) {
					recorder = new FFmpegFrameRecorder(
							videoOutputFile.getCanonicalPath(),
							grabber.getImageWidth(), grabber.getImageHeight());
				} else {
					recorder = new FFmpegFrameRecorder(
							videoOutputFile.getCanonicalPath(),
							grabber.getImageHeight(), grabber.getImageWidth());

				}
				recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
				recorder.setFrameRate(30);
				recorder.setVideoQuality(22);
				recorder.setFormat("mp4");
				recorder.start();
			}

			// Init the converter
			FrameConverter converter;
			if (!this.rotateOutput) {
				// no rotation = input width/height = output width/height
				converter = new FrameConverter(grabber.getImageWidth(),
						grabber.getImageHeight(), grabber.getImageWidth(),
						grabber.getImageHeight());

			} else {
				// rotation = input width/height != output width/height
				converter = new FrameConverter(grabber.getImageWidth(),
						grabber.getImageHeight(), grabber.getImageHeight(),
						grabber.getImageWidth());
			}

			// Init the Action
			this.action.start();

			// The frame variables
			IplImage grabbedFrame = null;
			Mat openCVFrame = null;
			IplImage recordedFrame = null;

			// OpenCV variables
			while (true) {
				try {
					long startTime = System.currentTimeMillis();
					grabbedFrame = grabber.grab();
					if (grabbedFrame == null) {
						// no new frames
						break;
					}

					// Convert Frame from JavaCV to OpenCV
					openCVFrame = converter.convertJavaCVtoOpenCV(grabbedFrame);

					// Edit the frame using OpenCV stuff
					this.action.processFrame(openCVFrame);

					// Convert Frame from OpenCV to JavaCV
					recordedFrame = converter
							.convertOpenCVtoJavaCV(openCVFrame);

					// Record the frame
					if (recorder != null) {
						recorder.record(recordedFrame);
					}
					Log.d(TAG, "Time Per Frame: "
							+ (System.currentTimeMillis() - startTime) + " ms");
				} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
					e.printStackTrace();
				}
			}
			this.action.stop();
			grabber.stop();
			if (recorder != null) {
				recorder.stop();
				recorder.release();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} catch (org.bytedeco.javacv.FrameRecorder.Exception e1) {
			e1.printStackTrace();
		}
	}
}