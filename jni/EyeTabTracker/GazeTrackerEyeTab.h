/*
 * The class that handles the EyeTab access
 *
 * Author: André Pomp
 *
 */
#ifndef GAZETRACKER_H
#define GAZETRACKER_H

#include <opencv2/core/core.hpp>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include "src_eyetab/eye_detect.h"
#include "src_eyetab/erase_specular.h"
#include "src_eyetab/get_poss_limb_pts.h"
#include "src_eyetab/fit_ellipse.h"
#include "src_eyetab/utils.h"
#include "src_eyetab/get_eyelids.h"
#include "src_eyetab/gaze_system.h"
#include "src_eyetab/gaze_smoothing.h"
#include "src_eyetab/gaze_geometry.h"

#include "../Logging.h"
#include "GazeSettingsEyeTab.h"

using namespace cv;

class GazeTrackerEyeTab {
public:
	void init(string nativeHaarFileBothEyes, string nativeHaarFileLeftEye,
			string nativeHaarFileRightEye, string orientation,
			int screenSizeX_PX, int screenSizeY_PX, int screenSizeX_MM,
			int screenSizeY_MM, int cameraOffsetX, int cameraOffsetY,
			int cameraResWidth, int cameraResHeight, double focalLenX,
			double focalLenY, double focalLenZ, double prinPointX,
			double prinPointY, string eyeCenterDetectAlgo,
			string gazeTrackingAlgo, int fastEyeWidth);
	int* detect(Mat& colorFrame, string& errorMessage);
	GazeTrackerEyeTab();
};

#endif
