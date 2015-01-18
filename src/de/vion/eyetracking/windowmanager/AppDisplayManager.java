package de.vion.eyetracking.windowmanager;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.view.Display;
import de.vion.eyetracking.callback.AppDisplayManagerCallback;
import de.vion.eyetracking.callback.WindowManagerCallback;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;

/**
 * 
 * Manages the different attached displays (e.g., if a second screen is
 * available)
 * 
 * @author André Pomp
 * 
 */
public class AppDisplayManager implements DisplayListener,
		WindowManagerCallback {

	// Demo Modus
	private boolean demoModus;

	// The Android Display Manager
	private DisplayManager displayManager;
	private AppDisplayManagerCallback callback;

	// Context
	private Context contextPrimaryDisplay;
	private Context contextSecondaryDisplay;

	// Values which indicate that the displays were initialized
	private boolean primaryDisplayInitialized = false;
	private boolean secondaryDisplayInitialized = false;

	// Content that is displayed on the primary display (only one of the manager
	// could be present at the same time)
	private CameraRecorderWindowManager cameraRecorderWindowManager;
	private LiveGazeWindowManager liveGazeWindowManager;

	// Content that is displayed on the secondary display
	private Display presentationDisplay;
	private PresentationWindowManager presentationWindowManager;

	// The test settings
	private GenericTestSettings testSettings;

	public AppDisplayManager(Context contextPrimaryDisplay,
			AppDisplayManagerCallback callback,
			GenericTestSettings testSettings, boolean demoModus) {
		super();
		this.demoModus = demoModus;
		this.callback = callback;
		this.contextPrimaryDisplay = contextPrimaryDisplay;
		this.testSettings = testSettings;
		this.displayManager = (DisplayManager) this.contextPrimaryDisplay
				.getSystemService(Context.DISPLAY_SERVICE);
		this.displayManager.registerDisplayListener(this, null);
	}

	public void initDisplays(String subdir, String videoFilePath) {
		// Init the main display
		if (this.demoModus) {
			this.liveGazeWindowManager = new LiveGazeWindowManager(
					this.contextPrimaryDisplay, this);
			this.liveGazeWindowManager.initViews();
		} else {
			this.cameraRecorderWindowManager = new CameraRecorderWindowManager(
					this.contextPrimaryDisplay, this, subdir);
			this.cameraRecorderWindowManager.initViews();
		}

		// Init the presentation display
		Display[] presentationDisplays = this.displayManager
				.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
		// check if we use the presentation display (availability is checked
		// before)
		if (this.testSettings.isShowSecondScreen(this.contextPrimaryDisplay)) {
			// Choose the first display
			this.presentationDisplay = presentationDisplays[0];

			// Create the context for the presentation display
			this.contextSecondaryDisplay = this.contextPrimaryDisplay
					.createDisplayContext(this.presentationDisplay);
			this.presentationWindowManager = new PresentationWindowManager(
					this.contextSecondaryDisplay, this, videoFilePath);
			this.presentationWindowManager.initViews();
		}
	}

	public void start() {
		if (this.demoModus) {
			this.liveGazeWindowManager.startGazeTracker();
		} else {
			this.cameraRecorderWindowManager.startRecording();
		}

		if (this.presentationWindowManager != null) {
			this.presentationWindowManager.start();
		}
	}

	public void stop() {
		if (this.demoModus) {
			this.liveGazeWindowManager.stopGazeTracker();
		} else {
			this.cameraRecorderWindowManager.stopRecording();
		}

		if (this.presentationWindowManager != null) {
			this.presentationWindowManager.stop();
		}
	}

	@Override
	public void onDisplayAdded(int displayId) {
		// do nothing (display must be present before)
	}

	@Override
	public void onDisplayChanged(int displayId) {
		// do nothing
	}

	@Override
	public void onDisplayRemoved(int displayId) {
		this.callback.onDisplayRemoved();

		this.presentationWindowManager = null;
		this.contextSecondaryDisplay = null;
		this.presentationDisplay = null;
		this.secondaryDisplayInitialized = false;
	}

	@Override
	public void onMainWindowFinished() {
		this.primaryDisplayInitialized = true;

		if (!this.testSettings.isShowSecondScreen(this.contextPrimaryDisplay)
				|| this.secondaryDisplayInitialized) {
			this.callback.onDisplaysInitialized();
		}
	}

	@Override
	public void onPresentationWindowFinished() {
		this.secondaryDisplayInitialized = true;
		if (this.primaryDisplayInitialized) {
			this.callback.onDisplaysInitialized();
		}
	}

	/**
	 * @param context
	 * @return
	 */
	public static boolean isSecondScreenAvailable(Context context) {
		DisplayManager displayManager = (DisplayManager) context
				.getSystemService(Context.DISPLAY_SERVICE);
		Display[] presentationDisplays = displayManager
				.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
		// check if a presentation display is available
		if (presentationDisplays.length > 0) {
			return true;
		}
		return false;
	}
}