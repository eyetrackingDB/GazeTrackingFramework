/*
 * The class that handles the dynamic settings for EyeTab
 *
 * Author: André Pomp
 *
 */
#include "GazeSettingsEyeTab.h"
#include "../Logging.h"

//CASCADE_PARAMETERS
string CASCADE_FILE_BOTH_EYES;
string CASCADE_FILE_LEFT_EYE;
string CASCADE_FILE_RIGHT_EYE;

//DISPLAY PARAMETERS
Size SCREEN_SIZE_PX;		// screen size in pixels
Size SCREEN_SIZE_MM;        // screen size in mm
string ORIENTATION;

//CAMERA Parameters
Size CAMERA_RES_PX;	//the resolution of the camera
Point2i CAMERA_OFFSET_MM;	// vector from top left of screen to camera

// Scale
float SCALE_X;
float SCALE_Y;

//INTRINSIC CAMERA PARAMETERS
double FOCAL_LEN_X_PX;
double FOCAL_LEN_Y_PX;
double FOCAL_LEN_Z_PX;
cv::Point2d PRIN_POINT;

//Algorithm Parameter
string EYE_CENTER_DETECT_ALGO;
string GAZE_TRACKING_ALGO;
int FAST_EYE_WIDTH;

void initSettings(string nativeHaarFileBothEyes, string nativeHaarFileLeftEye,
		string nativeHaarFileRightEye, string orientation, int screenSizeX_PX,
		int screenSizeY_PX, int screenSizeX_MM, int screenSizeY_MM,
		int cameraOffsetX, int cameraOffsetY, int cameraResWidth,
		int cameraResHeight, double focalLenX, double focalLenY,
		double focalLenZ, double prinPointX, double prinPointY,
		string eyeCenterDetectAlgo, string gazeTrackingAlgo, int fastEyeWidth) {
	CASCADE_FILE_BOTH_EYES = nativeHaarFileBothEyes;
	CASCADE_FILE_LEFT_EYE = nativeHaarFileLeftEye;
	CASCADE_FILE_RIGHT_EYE = nativeHaarFileRightEye;
	LOG("CascadeFile Both Eyes: %s \n", CASCADE_FILE_BOTH_EYES.c_str());
	LOG("CascadeFile Left Eye: %s \n", CASCADE_FILE_LEFT_EYE.c_str());
	LOG("CascadeFile Right Eye: %s \n", CASCADE_FILE_RIGHT_EYE.c_str());

	//Set the display parameters
	ORIENTATION = orientation;
	SCREEN_SIZE_PX = Size(screenSizeX_PX, screenSizeY_PX);
	SCREEN_SIZE_MM = Size(screenSizeX_MM, screenSizeY_MM);

	//Set the camera parameters
	CAMERA_OFFSET_MM = Point2i(cameraOffsetX, cameraOffsetY);
	CAMERA_RES_PX = Size(cameraResWidth, cameraResHeight);

	// Calculate the scale
	SCALE_X = CAMERA_RES_PX.width / float(SCREEN_SIZE_PX.width);
	SCALE_Y = CAMERA_RES_PX.height / float(SCREEN_SIZE_PX.height);

	LOG("Orientation: %s \n", ORIENTATION.c_str());
	LOG("ScreenSize X PX: %d \n", SCREEN_SIZE_PX.width);
	LOG("ScreenSize Y PX: %d \n", SCREEN_SIZE_PX.height);
	LOG("ScreenSize X MM: %d \n", SCREEN_SIZE_MM.width);
	LOG("ScreenSize Y MM: %d \n", SCREEN_SIZE_MM.height);
	LOG("CameraRes X: %d \n", CAMERA_RES_PX.width);
	LOG("CameraRes Y: %d \n", CAMERA_RES_PX.height);
	LOG("CameraOffset X: %d \n", CAMERA_OFFSET_MM.x);
	LOG("CameraOffset Y: %d \n", CAMERA_OFFSET_MM.y);
	LOG("ScaleX: %f \n", SCALE_X);
	LOG("ScaleY: %f \n", SCALE_Y);

	// Set the intrinsic camera parameters
	FOCAL_LEN_X_PX = focalLenX;
	FOCAL_LEN_Y_PX = focalLenY;
	FOCAL_LEN_Z_PX = focalLenZ;
	PRIN_POINT = Point2d(prinPointX, prinPointY);

	LOG("FocalLenX: %f \n", FOCAL_LEN_X_PX);
	LOG("FocalLenY: %f \n", FOCAL_LEN_Y_PX);
	LOG("FocalLenZ: %f \n", FOCAL_LEN_Z_PX);
	LOG("PrinPointX: %f \n", PRIN_POINT.x);
	LOG("PrinPointY: %f \n", PRIN_POINT.y);

	EYE_CENTER_DETECT_ALGO = eyeCenterDetectAlgo;
	GAZE_TRACKING_ALGO = gazeTrackingAlgo;
	FAST_EYE_WIDTH = fastEyeWidth;
	LOG("EYE CENTER DETECT ALGO: %s \n", EYE_CENTER_DETECT_ALGO.c_str());
	LOG("GAZE TRACKING ALGO: %s \n", GAZE_TRACKING_ALGO.c_str());
	LOG("FAST EYE WIDTH: %d \n", FAST_EYE_WIDTH);
}
