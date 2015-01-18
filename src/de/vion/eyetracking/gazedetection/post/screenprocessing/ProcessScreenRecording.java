package de.vion.eyetracking.gazedetection.post.screenprocessing;

import java.io.File;

import de.vion.eyetracking.gazedetection.post.misc.FrameProcessor;

/**
 * The class for processing the screen recording
 * 
 * @author André Pomp
 * 
 */
public class ProcessScreenRecording {

	private File screenRecordingInput;
	private File screenRecordingOutput;
	private File textInput;

	public ProcessScreenRecording(File screenRecordingInput, File screenRecordingOutput,
			File textInput) {
		super();
		this.screenRecordingInput = screenRecordingInput;
		this.screenRecordingOutput = screenRecordingOutput;
		this.textInput = textInput;
	}

	public void processScreenRecording() {
		FrameProcessor screenRecording = new FrameProcessor(new ProcessScreenAction(
				this.textInput), false);
		screenRecording.processVideo(this.screenRecordingInput,
				this.screenRecordingOutput);
	}

}