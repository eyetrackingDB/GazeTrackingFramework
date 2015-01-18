/*
 * The class that allows the access for the given gaze tracking algorithms
 *
 * Author: André Pomp
 *
 */

#include <jni.h>
#include <opencv2/core/core.hpp>
#include "EyeTabTracker/GazeTrackerEyeTab.h"

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT jlong JNICALL Java_de_vion_eyetracking_gazedetection_gazetracker_GazeTrackerEyeTab_nativeCreateObject(
		JNIEnv* jenv, jclass) {
	LOG("Call JNI Create Object");

	return (jlong) new GazeTrackerEyeTab();
}
}

extern "C" {
JNIEXPORT void JNICALL Java_de_vion_eyetracking_gazedetection_gazetracker_GazeTrackerEyeTab_nativeInitObject(
		JNIEnv* jenv, jclass, jlong thiz, jstring haarFileBothEyes,
		jstring haarFileLeftEye, jstring haarFileRightEye, jstring jOrientation,
		jint screenSizeX_PX, jint screenSizeY_PX, jint screenSizeX_MM,
		jint screenSizeY_MM, jint cameraOffsetX, jint cameraOffsetY,
		jint cameraResWidth, jint cameraResHeight, jdouble focalLenX,
		jdouble focalLenY, jdouble focalLenZ, jdouble prinPointX,
		jdouble prinPointY, jstring eyeCenterDetectAlgo,
		jstring gazeTrackingAlgo, jint fastEyeWidth) {

	LOG("Call JNI Init Object");

	//Convert Java values into C++ values
	int nativeScreenSizeX_PX = (int) screenSizeX_PX;
	int nativeScreenSizeY_PX = (int) screenSizeY_PX;
	int nativeScreenSizeX_MM = (int) screenSizeX_MM;
	int nativeScreenSizeY_MM = (int) screenSizeY_MM;
	int nativeCameraOffsetX = (int) cameraOffsetX;
	int nativeCameraOffsetY = (int) cameraOffsetY;
	int nativeCameraResWidth = (int) cameraResWidth;
	int nativeCameraResHeight = (int) cameraResHeight;
	double nativeFocalLenX = (double) focalLenX;
	double nativeFocalLenY = (double) focalLenY;
	double nativeFocalLenZ = (double) focalLenZ;
	double nativePrinPointX = (double) prinPointX;
	double nativePrinPointY = (double) prinPointY;
	string nativeHaarFileBothEyes = jenv->GetStringUTFChars(haarFileBothEyes,
	NULL);
	string nativeHaarFileLeftEye = jenv->GetStringUTFChars(haarFileLeftEye,
	NULL);
	string nativeHaarFileRightEye = jenv->GetStringUTFChars(haarFileRightEye,
	NULL);
	string nativeOrientation = jenv->GetStringUTFChars(jOrientation, NULL);
	string nativeEyeCenterDetectAlgo = jenv->GetStringUTFChars(
			eyeCenterDetectAlgo, NULL);
	string nativeGazeTrackingAlgo = jenv->GetStringUTFChars(gazeTrackingAlgo,
	NULL);
	int nativeFastEyeWidth = (int) fastEyeWidth;

	//Call the method
	((GazeTrackerEyeTab*) thiz)->init(nativeHaarFileBothEyes, nativeHaarFileLeftEye,
			nativeHaarFileRightEye, nativeOrientation, nativeScreenSizeX_PX,
			nativeScreenSizeY_PX, nativeScreenSizeX_MM, nativeScreenSizeY_MM,
			nativeCameraOffsetX, nativeCameraOffsetY, nativeCameraResWidth,
			nativeCameraResHeight, nativeFocalLenX, nativeFocalLenY,
			nativeFocalLenZ, nativePrinPointX, nativePrinPointY,
			nativeEyeCenterDetectAlgo, nativeGazeTrackingAlgo,
			nativeFastEyeWidth);
}
}

extern "C" {
JNIEXPORT jintArray JNICALL Java_de_vion_eyetracking_gazedetection_gazetracker_GazeTrackerEyeTab_nativeDetect(
		JNIEnv* jenv, jclass, jlong thiz, jlong addrRgba) {
	//Convert the mat addresses into the objects
	Mat& rgbFrame = *(Mat*) addrRgba;

	// Call the detect function
	int* result;
	string errorMessage;
	result = ((GazeTrackerEyeTab*) thiz)->detect(rgbFrame, errorMessage);

	jintArray javaArray;
	if (result == NULL) {
		LOG("No GazePoint found: %s",errorMessage.c_str());
		return NULL;
	} else {
		javaArray = jenv->NewIntArray(4);
		jenv->SetIntArrayRegion(javaArray, 0, 4, result);
		return javaArray;
	}
}
}

extern "C" {
JNIEXPORT void JNICALL Java_de_vion_eyetracking_gazedetection_gazetracker_GazeTrackerEyeTab_nativeDestroyObject(
		JNIEnv* jenv, jclass, jlong thiz) {
	LOG("Call JNI Destoy Object");
	if (thiz != 0) {
		delete (GazeTrackerEyeTab*) thiz;
	}
}
}
