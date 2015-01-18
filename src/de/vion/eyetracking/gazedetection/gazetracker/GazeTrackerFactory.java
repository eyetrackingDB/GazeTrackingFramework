package de.vion.eyetracking.gazedetection.gazetracker;

import android.content.Context;

/**
 * 
 * The Factory for choosing a gaze tracking implementation
 * 
 * @author André Pomp
 * 
 */
public class GazeTrackerFactory {

	public static final GazeTrackerType type = GazeTrackerType.EYETAB;

	public static GazeTrackerAbstract createGazeTracker(Context context)
			throws IllegalArgumentException {
		switch (type) {
		case EYETAB:
			return new GazeTrackerEyeTab(context);
		default:
			throw new IllegalArgumentException("Invalid Gaze Type");
		}
	}

	public static GazeTrackerSettingsAbstract createGazeTrackerSettings() {
		switch (type) {
		case EYETAB:
			return new GazeTrackerSettingsEyeTab();
		default:
			throw new IllegalArgumentException("Invalid Gaze Type");
		}
	}
}
