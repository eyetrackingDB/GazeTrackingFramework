/*
 * The class that handles the dynamic settings for EyeTab
 *
 * Author: André Pomp
 *
 */
#ifndef GAZESETTINGS_H
#define GAZESETTINGS_H

#include <opencv2/core/core.hpp>

using namespace std;
using namespace cv;

extern string CASCADE_FILE_BOTH_EYES;
extern string CASCADE_FILE_LEFT_EYE;
extern string CASCADE_FILE_RIGHT_EYE;

//DISPLAY PARAMETERS
extern Size SCREEN_SIZE_PX;		// screen size in pixels
extern Size SCREEN_SIZE_MM;        // screen size in mm
extern string ORIENTATION; // the orientation of the device

//CAMERA Parameters
extern Size CAMERA_RES_PX;	//the resolution of the camera
extern Point2i CAMERA_OFFSET_MM;	// vector from top left of screen to camera

// Scale
extern float SCALE_X;
extern float SCALE_Y;

//INTRINSIC CAMERA PARAMETERS
extern double FOCAL_LEN_X_PX;
extern double FOCAL_LEN_Y_PX;
extern double FOCAL_LEN_Z_PX;
extern Point2d PRIN_POINT;

//Algorithm Parameter
extern string EYE_CENTER_DETECT_ALGO;
extern string GAZE_TRACKING_ALGO;
extern int FAST_EYE_WIDTH;

void initSettings(string nativeHaarFileBothEyes, string nativeHaarFileLeftEye,
		string nativeHaarFileRightEye, string orientation, int screenSizeX_PX,
		int screenSizeY_PX, int screenSizeX_MM, int screenSizeY_MM,
		int cameraOffsetX, int cameraOffsetY, int cameraResWidth,
		int cameraResHeight, double focalLenX, double focalLenY,
		double focalLenZ, double prinPointX, double prinPointY,
		string eyeCenterDetectAlgo, string gazeTrackingAlgo, int fastEyeWidth);

#endif
