/*
 * The class that handles the EyeTab access
 *
 * Author: André Pomp
 *
 */
#include "GazeTrackerEyeTab.h"
#include <tbb/tbb.h>

using namespace std;

GazeTrackerEyeTab::GazeTrackerEyeTab() {
	LOG("Creating C++ Object \n");
}

void GazeTrackerEyeTab::init(string nativeHaarFileBothEyes,
		string nativeHaarFileLeftEye, string nativeHaarFileRightEye,
		string orientation, int screenSizeX_PX, int screenSizeY_PX,
		int screenSizeX_MM, int screenSizeY_MM, int cameraOffsetX,
		int cameraOffsetY, int cameraResWidth, int cameraResHeight,
		double focalLenX, double focalLenY, double focalLenZ, double prinPointX,
		double prinPointY, string eyeCenterDetectAlgo, string gazeTrackingAlgo,
		int fastEyeWidth) {
	LOG("Init C++ Object \n");

	// Settings
	initSettings(nativeHaarFileBothEyes.c_str(), nativeHaarFileLeftEye.c_str(),
			nativeHaarFileRightEye.c_str(), orientation.c_str(), screenSizeX_PX,
			screenSizeY_PX, screenSizeX_MM, screenSizeY_MM, cameraOffsetX,
			cameraOffsetY, cameraResWidth, cameraResHeight, focalLenX,
			focalLenY, focalLenZ, prinPointX, prinPointY,
			eyeCenterDetectAlgo.c_str(), gazeTrackingAlgo.c_str(),
			fastEyeWidth);

	// initialize modules
	lin_polar_init();
	eye_detect_init();
	gaze_smoothing_init();
}

void rotate_image(cv::Mat &src, cv::Mat &dst, int angle) {
	if (src.data != dst.data) {
		src.copyTo(dst);
	}

	angle = ((angle / 90) % 4) * 90;

	//0 : flip vertical; 1 flip horizontal
	bool const flip_horizontal_or_vertical = angle > 0 ? 1 : 0;
	int const number = std::abs(angle / 90);

	for (int i = 0; i != number; ++i) {
		cv::transpose(dst, dst);
		cv::flip(dst, dst, flip_horizontal_or_vertical);
	}
}

int* GazeTrackerEyeTab::detect(Mat& colorFrame, string& errorMessage) {
	// Rotate the image for processing it
	if (ORIENTATION == "LANDSCAPE") {
		//do nothing
	} else if (ORIENTATION == "LANDSCAPE_REVERSE") {
		rotate_image(colorFrame, colorFrame, 180);
	} else if (ORIENTATION == "PORTRAIT") {
		rotate_image(colorFrame, colorFrame, -90);
	} else if (ORIENTATION == "PORTRAIT_REVERSE") {
		rotate_image(colorFrame, colorFrame, 90);
	}

	// Convert image to 3 channels instead of 4 (remove alpha) and use BGR format
	cvtColor(colorFrame, colorFrame, COLOR_RGBA2BGR);

	// Track the gaze
	int* result = track_gaze(colorFrame, errorMessage);

	// Convert image back to RGB
	cvtColor(colorFrame, colorFrame, COLOR_BGR2RGB);

	return result;
}
