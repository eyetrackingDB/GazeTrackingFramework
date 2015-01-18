#include "stdafx.h"
#include <algorithm>

#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include "../../Logging.h"
#include "../GazeSettingsEyeTab.h"

using namespace std;
using namespace cv;

//                              eye_1     eye_2
//                        skin  |    nose |    skin
//                        |     |    |    |    |
float EYE_PART_RATIOS[] = { 0.05, 0.3, 0.3, 0.3, 0.05 };
float angles[] = { 0.0f, 15.0f, -15.0f, 30.0f, -30.0f };

CascadeClassifier casecade_eyepair;
CascadeClassifier casecade_eyeleft;
CascadeClassifier casecade_eyeright;

void eye_detect_init() {
	casecade_eyepair.load(CASCADE_FILE_BOTH_EYES);
	casecade_eyeleft.load(CASCADE_FILE_LEFT_EYE);
	casecade_eyeright.load(CASCADE_FILE_RIGHT_EYE);
}

float measure_blurriness_log(const Mat& grey_frame){
	//Make a gaussian blur on the grey image
	GaussianBlur(grey_frame, grey_frame, Size(3, 3), 0);

	//Make a Laplacian transformation
	Mat LoG_img;
	Laplacian(grey_frame, LoG_img, CV_16S, 5, 15);

	//Copy the MAT values into an float array 
	int counter = 0;
	int array_size = LoG_img.rows * LoG_img.cols;	
	float* mat_values = new float[array_size];
	
	// Cast explicity from uchar to float and store the value in the array
	for (int i = 0; i < LoG_img.rows; i++) {
		for (int j = 0; j < LoG_img.cols; j++) {
			float castedValue = ((float) LoG_img.at<short>(i, j));  			mat_values[counter] = castedValue;
			counter++;
		}
	}

	// Sort the array
	sort(mat_values, mat_values + array_size);
	
	// calculate the 90% percentile
	int pos = int(ceilf(0.9f * float(array_size)));
		
	float threshold = mat_values[pos];

	// remain all values that are above this threshold
	vector<int> values;
	for(int i=0; i<array_size; i++){
		if(mat_values[i] > threshold){
			values.push_back(mat_values[i]);
		}
	}

	// Calculate the mean and return it
	float sum = accumulate(values.begin(), values.end(), 0.0);
	float av_edge_strength = sum/values.size();
	return av_edge_strength;
}

Rect choose_best_eyepair(const vector<Rect>& eye_pairs, const Mat& grey_frame){
	float maxValue = 0;	
	Rect result;	
	for(Rect rect : eye_pairs){
		float currentMax = measure_blurriness_log(grey_frame(rect));
		if(currentMax > maxValue){
			maxValue = currentMax;
			result = rect;
		}
	}
	return result;
}

bool detect_eye_pair(const Mat& frame, float scale, Rect& eye0_roi,
		Rect& eye1_roi, float angle) {
	//Define the min eye pair size
	Size min_eye_pair_size = Size(frame.rows / 4, frame.rows / 16);

	//Check if we have to rotate the image
	if (angle != 0) {
		//Define the center of the rotation
		Point2f rotCenter(frame.cols / 2.0f, frame.rows / 2.0f);

		//Get the rotation matrix
		Mat rotationMatrix = getRotationMatrix2D(rotCenter, angle, 1.0f);

		//Rotate the image
		warpAffine(frame, frame, rotationMatrix, Size(frame.cols, frame.rows));
	}

	// Find the EyePairs
	vector<Rect> eye_pairs;
	casecade_eyepair.detectMultiScale(frame, eye_pairs, 1.1, 3, 0, min_eye_pair_size);

	// Choose the correct eye-pair if one was found
	Rect eye_pair;
	if (eye_pairs.size() == 0) {
		return false;
	} else if (eye_pairs.size() > 1){
		eye_pair = choose_best_eyepair(eye_pairs, frame);
	} else {
		eye_pair = eye_pairs[0];
	}

	LOG("Initial EyePair at Angle %f: StartX: %d StartY: %d Width: %d Height: %d \n", angle, eye_pair.x, eye_pair.y, eye_pair.width, eye_pair.height);

	// Start and end pts for eye-pair ROI in full-frame
	Point roi_start(eye_pair.x / scale, eye_pair.y / scale);
	Point roi_end((eye_pair.x + eye_pair.width) / scale,
			(eye_pair.y + eye_pair.height) / scale);

	int width = roi_end.x - roi_start.x;

	// Extract two coarse eye-rois from the eye-pair roi
	// ########### Changed to make it equal to python version ###################
	eye0_roi = Rect(roi_start.x + width * EYE_PART_RATIOS[0], roi_start.y,
			width * (EYE_PART_RATIOS[1]), roi_end.y - roi_start.y);

	eye1_roi = Rect(
			roi_start.x
					+ width
							* (EYE_PART_RATIOS[0] + EYE_PART_RATIOS[1]
									+ EYE_PART_RATIOS[2]), roi_start.y,
			width * (EYE_PART_RATIOS[3]), roi_end.y - roi_start.y);

	//We have to unrotate the rectangles
	if (angle != 0) {
		//Find currently rotated middles of eye ROIs
		Point3f eye0_mid_rot;
		eye0_mid_rot.x = eye0_roi.x + eye0_roi.width / 2;
		eye0_mid_rot.y = eye0_roi.y + eye0_roi.height / 2;
		eye0_mid_rot.z = (1/scale);

		Point3f eye1_mid_rot;
		eye1_mid_rot.x = eye1_roi.x + eye1_roi.width / 2;
		eye1_mid_rot.y = eye1_roi.y + eye1_roi.height / 2;
		eye1_mid_rot.z = (1/scale);

		// Get the rotation matrix again
		Point2f rotCenter(frame.cols / 2.0f, frame.rows / 2.0f);
		Mat rotationMatrix = getRotationMatrix2D(rotCenter, -angle, 1.0f);

		// Unrotate midpoint by calculating dot product
		Point2f eye0_mid_unrot;
		eye0_mid_unrot.x = eye0_mid_rot.x * rotationMatrix.at<double>(0,0) + eye0_mid_rot.y * rotationMatrix.at<double>(0,1) + eye0_mid_rot.z * rotationMatrix.at<double>(0,2);
		eye0_mid_unrot.y = eye0_mid_rot.x * rotationMatrix.at<double>(1,0) + eye0_mid_rot.y * rotationMatrix.at<double>(1,1) + eye0_mid_rot.z * rotationMatrix.at<double>(1,2);

		Point2f eye1_mid_unrot;
		eye1_mid_unrot.x = eye1_mid_rot.x * rotationMatrix.at<double>(0,0) + eye1_mid_rot.y * rotationMatrix.at<double>(0,1) + eye1_mid_rot.z * rotationMatrix.at<double>(0,2);
		eye1_mid_unrot.y = eye1_mid_rot.x * rotationMatrix.at<double>(1,0) + eye1_mid_rot.y * rotationMatrix.at<double>(1,1) + eye1_mid_rot.z * rotationMatrix.at<double>(1,2);

		// Redefine ROIs upper points of the unrotated rectangles
		eye0_roi.x = eye0_mid_unrot.x - eye0_roi.width/2; //can be outside of the frame => see fix below
		eye0_roi.y = eye0_mid_unrot.y - eye0_roi.height/2;

		// Check that the eye0 roi x is not outside of the picture
		if(eye0_roi.x < 0){
			eye0_roi.x = 0;
		}

		eye1_roi.x = eye1_mid_unrot.x - eye1_roi.width/2;
		eye1_roi.y = eye1_mid_unrot.y - eye1_roi.height/2;
	}
	return true;
}

bool detect_single_eyes(const Mat& frame, float scale, Rect& eye0_roi,
		Rect& eye1_roi) {
	//Define the min eye pair size
	Size min_eye_pair_size = Size(frame.rows / 6, frame.rows / 9);

	//Define the ROI for the left eye
	Rect rightEyeROI = Rect();
	rightEyeROI.x = 0;
	rightEyeROI.y = 0;
	rightEyeROI.height = frame.rows;
	rightEyeROI.width = ((frame.cols) / 2);

	vector<Rect> right_eye_pairs;
	casecade_eyeleft.detectMultiScale(frame(rightEyeROI), right_eye_pairs, 1.1,
			3, 0, min_eye_pair_size);

	//Choose the best right eye
	Rect rightEye;
	if (right_eye_pairs.size() == 0) {
		return false;
	} else if (right_eye_pairs.size() > 1){
		rightEye = choose_best_eyepair(right_eye_pairs, frame);
	} else {
		rightEye = right_eye_pairs[0];
	}

	//Define the ROI for the right eye
	Rect leftEyeROI = Rect();
	leftEyeROI.x = ((frame.cols) / 2);
	leftEyeROI.y = 0;
	leftEyeROI.height = frame.rows;
	leftEyeROI.width = ((frame.cols) / 2);

	vector<Rect> left_eye_pairs;
	casecade_eyeright.detectMultiScale(frame(leftEyeROI), left_eye_pairs, 1.1,
			3, 0, min_eye_pair_size);

	//Choose the best left eye
	Rect leftEye;
	if (left_eye_pairs.size() == 0) {
		return false;
	} else if (left_eye_pairs.size() > 1){
		leftEye = choose_best_eyepair(left_eye_pairs, frame);
	} else {
		leftEye = left_eye_pairs[0];
	}

	// Define the ROI for the left eye (scaled)
	eye0_roi = Rect();
	eye0_roi.x = leftEye.x / scale;
	eye0_roi.y = leftEye.y / scale;
	eye0_roi.width = leftEye.width / scale;
	eye0_roi.height = leftEye.height / scale;

	// Define the ROI for the right eye (scaled)
	eye1_roi = Rect();
	eye1_roi.x = (rightEye.x + ((frame.cols) / 2)) / scale;
	eye1_roi.y = rightEye.y / scale;
	eye1_roi.width = rightEye.width / scale;
	eye1_roi.height = rightEye.height / scale;
	return true;
}

bool detect_eyes(const Mat& frame_bgr, Rect& eye0_roi, Rect& eye1_roi) {
	// Convert the image to grey
	Mat frame_grey;
	cvtColor(frame_bgr, frame_grey, COLOR_BGR2GRAY);

	// Scale down two times using a gaussian pyramid
	Mat smallFrame;		
	pyrDown(frame_grey, smallFrame, Size(frame_grey.cols / 2, frame_grey.rows / 2));
	pyrDown(smallFrame, smallFrame, Size(smallFrame.cols / 2, smallFrame.rows / 2));

	// Calculate the scale
	float scale = smallFrame.cols / (float) frame_grey.size().width;

	// Find the eyes for the pair cascade file
	for (float angle : angles) {
		// Create a deep copy for the rotation later
		Mat tmpFrame; 
		smallFrame.copyTo(tmpFrame);		

		// Search for the eyes
		bool foundEyes = detect_eye_pair(tmpFrame, scale, eye0_roi, eye1_roi, angle);

		// If we found eyes we can stop
		if (foundEyes) {
			return true;
		}
	}

	// Finally try the single eyes
	return detect_single_eyes(smallFrame, scale, eye0_roi, eye1_roi);
}
