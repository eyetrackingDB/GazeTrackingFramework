#include "stdafx.h"

#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <math.h>       /* floor */

//Eye Tab Includes
#include "erase_specular.h"
#include "get_poss_limb_pts.h"
#include "fit_ellipse.h"
#include "utils.h"
#include "get_eyelids.h"
#include "gaze_geometry.h"
#include "gaze_smoothing.h"
#include "eye_detect.h"
#include "eye_center_isophotes.h"
#include "eye_center_combined.h"

//Eye Like
#include "../src_eyelike/findEyeCenter.h"

//Settings and Android LOG
#include "../../Logging.h"
#include "../GazeSettingsEyeTab.h"

using namespace std;
using namespace cv;

// Ratio of eye-pair distance to refined eye-ROI size
float EYE_PAIR_DIST_ROI_RATIO = 0.15;

int* track_gaze(Mat& frame_bgr, string& errorMessage) {
	LOG("######### Started Tracking ######## \n");

	// Find eye_pairs and store them in the vector
	Rect eye0_roi;
	Rect eye1_roi;
	bool foundEyes = detect_eyes(frame_bgr, eye0_roi, eye1_roi);

	//Check if we found eyes
	if (!foundEyes) {
		LOG("******** No eyes found ******** \n");
		flip(frame_bgr, frame_bgr, 1);
		errorMessage = "No eyes";
		return NULL;
	}
	//Draw the coarse ROI
	rectangle(frame_bgr, eye0_roi, GREEN);
	rectangle(frame_bgr, eye1_roi, GREEN);
	
	LOG("Left ROI (Eye0): StartX: %d StartY: %d Width: %d Height: %d \n",
			eye0_roi.x, eye0_roi.y, eye0_roi.width, eye0_roi.height);
	LOG("Right ROI (Eye1): StartX: %d StartY: %d Width: %d Height: %d \n",
			eye1_roi.x, eye1_roi.y, eye1_roi.width, eye1_roi.height);

	//Erase specular
	erase_specular(frame_bgr(eye0_roi));
	erase_specular(frame_bgr(eye1_roi));

	// Find relative eye-centers for each eye-roi
	Point c0;
	Point c1;
	if (EYE_CENTER_DETECT_ALGO == "EYE_CENTER_ALGO_GRADIENTS") {
		// Using Fabian Timm's Algorithm
		c0 = findEyeCenter(frame_bgr(eye0_roi), FAST_EYE_WIDTH);
		c1 = findEyeCenter(frame_bgr(eye1_roi), FAST_EYE_WIDTH);
	} else if (EYE_CENTER_DETECT_ALGO == "EYE_CENTER_ALGO_ISOPHOTES") {
		// Using Curvature Isophotes Method
		c0 = find_eye_center_isophotes(frame_bgr(eye0_roi), FAST_EYE_WIDTH);
		c1 = find_eye_center_isophotes(frame_bgr(eye1_roi), FAST_EYE_WIDTH);
	} else if (EYE_CENTER_DETECT_ALGO == "EYE_CENTER_ALGO_COMBINED"){
		c0 = find_eye_center_combined(frame_bgr, eye0_roi, 50.0f, 80.0f);
		c1 = find_eye_center_combined(frame_bgr, eye1_roi, 50.0f, 80.0f);
	}

	if (c0.y == 0 || c1.y == 0) {
		LOG("========= Bad Pupil Value ======== \n");
		flip(frame_bgr, frame_bgr, 1);
		errorMessage = "Bad pupil";
		return NULL;
	}
	// Draw the eye centers
	cross(frame_bgr, c0 + eye0_roi.tl(), 3, RED);
	cross(frame_bgr, c1 + eye1_roi.tl(), 3, RED);
	LOG("Pupil Eye0: X: %d, Y: %d \n", c0.x, c0.y);
	LOG("Pupil Eye1: X: %d, Y: %d \n", c1.x, c1.y);

	// Get vector and inter-eye distance between eye-centers
	Point eye_cs_vec = (eye0_roi.tl() + c0) - (eye1_roi.tl() + c1);
	double eye_centre_dist = norm(eye_cs_vec);

	// Refine eye-rois about detected eye-center
	Rect eye0_roi_ref = roiAround(eye0_roi.tl() + c0,
			(int) (eye_centre_dist * EYE_PAIR_DIST_ROI_RATIO));
	Rect eye1_roi_ref = roiAround(eye1_roi.tl() + c1,
			(int) (eye_centre_dist * EYE_PAIR_DIST_ROI_RATIO));

	// If a ROI lies outside capture frame bounds, terminate early
	Rect capFrameRect = Rect(Point(0, 0), frame_bgr.size());
	if (eye0_roi_ref != (capFrameRect & eye0_roi_ref)
			|| eye1_roi_ref != (capFrameRect & eye1_roi_ref)) {
		LOG("========= Bad ROI ======== \n");
		flip(frame_bgr, frame_bgr, 1);
		errorMessage = "Bad ROI";
		return NULL;
	}
	// Draw refined ROIs
	rectangle(frame_bgr, eye0_roi_ref, RED);
	rectangle(frame_bgr, eye1_roi_ref, RED);

	// Get inital possible limbus points (including erroneous upper-eyelid points)
	vector<Point2f> poss_limb_pts_0 = get_poss_limb_pts(
			frame_bgr(eye0_roi_ref));
	vector<Point2f> poss_limb_pts_1 = get_poss_limb_pts(
			frame_bgr(eye1_roi_ref));

	// Get upper-eyelid parabolae for each eye, and filter poss-limbus points
	Mat eye0_eyelid = get_upper_eyelid(eye0_roi.tl() + c0, eye_cs_vec,
			frame_bgr(eye0_roi_ref));
	Mat eye1_eyelid = get_upper_eyelid(eye1_roi.tl() + c1, eye_cs_vec,
			frame_bgr(eye1_roi_ref));
	poss_limb_pts_0 = filter_poss_limb_pts(poss_limb_pts_0, eye0_eyelid,
			eye0_roi_ref.tl());
	poss_limb_pts_1 = filter_poss_limb_pts(poss_limb_pts_1, eye1_eyelid,
			eye1_roi_ref.tl());
	// Draw eyelids
	draw_eyelid(frame_bgr, eye0_eyelid, eye0_roi_ref);
	draw_eyelid(frame_bgr, eye1_eyelid, eye1_roi_ref);

	// Calculate image gradients, and fit ellipse for each set of filtered poss-limbus points
	Mat frame_grad_grey;	
	cvtColor(frame_bgr, frame_grad_grey, COLOR_BGR2GRAY);
	blur(frame_grad_grey,frame_grad_grey, Size(3, 3));

	Mat grad_x, grad_y;
	Sobel(frame_grad_grey(eye0_roi_ref), grad_x, CV_32F, 1, 0, 5);
	Sobel(frame_grad_grey(eye0_roi_ref), grad_y, CV_32F, 0, 1, 5);
	RotatedRect ellipse0 = fit_ellipse(poss_limb_pts_0, grad_x, grad_y);

	Sobel(frame_grad_grey(eye1_roi_ref), grad_x, CV_32F, 1, 0, 5);
	Sobel(frame_grad_grey(eye1_roi_ref), grad_y, CV_32F, 0, 1, 5);
	RotatedRect ellipse1 = fit_ellipse(poss_limb_pts_1, grad_x, grad_y);

	// Shift ellipses fit by their ROI offset
	ellipse0 = RotatedRect(
			Point2i(ellipse0.center.x, ellipse0.center.y) + eye0_roi_ref.tl(),
			ellipse0.size, ellipse0.angle);
	ellipse1 = RotatedRect(
			Point2i(ellipse1.center.x, ellipse1.center.y) + eye1_roi_ref.tl(),
			ellipse1.size, ellipse1.angle);
	// Draw the elipse
	ellipse(frame_bgr, ellipse0, YELLOW, 1);
	ellipse(frame_bgr, ellipse1, YELLOW, 1);

	// Calculate the gaze points for eye0
	Point2d gaze_pt_mm_eye0(NAN,NAN);
	Vector3d limbus_center_eye0;
	get_gaze_pt_mm(ellipse0, limbus_center_eye0, gaze_pt_mm_eye0);

	// Calculate the gaze points for eye1
	Point2d gaze_pt_mm_eye1(NAN,NAN);
	Vector3d limbus_center_eye1;
	get_gaze_pt_mm(ellipse1, limbus_center_eye1, gaze_pt_mm_eye1);

	// Calculate the combined gaze point
	Point2d gaze_pt_mm = (gaze_pt_mm_eye0 + gaze_pt_mm_eye1) * 0.5;

	// Check if we have outliner (bad for second screen)
//	if(isOutliner(limbus_center_eye0, limbus_center_eye1)){
//		LOG("&&&&&&&& OUTLINER &&&&&&&&&& \n");
//		flip(frame_bgr, frame_bgr, 1);
//		errorMessage = "OUTLINER";
//		return NULL;
//	}

	if (gaze_pt_mm.x != gaze_pt_mm.x || gaze_pt_mm.y != gaze_pt_mm.y) {
		LOG("&&&&&&&& INVALID ELLIPSE &&&&&&&&&& \n");
		flip(frame_bgr, frame_bgr, 1);
		errorMessage = "INVALID ELLIPSE";
		return NULL;
	}

	// Smooth the gaze point
	gaze_pt_mm = smooth_gaze(gaze_pt_mm);

	// Convert the gaze point to px
	Point2i gaze_pt_px = convert_gaze_pt_mm_to_px(gaze_pt_mm);

	// flip image for mirroring
	flip(frame_bgr, frame_bgr, 1);

	// Scale the smoothed gaze point for showing
	int scaledSmoothedPointX = gaze_pt_px.x * SCALE_X;
	int scaledSmoothedPointY = gaze_pt_px.y * SCALE_Y;

	// Draw the scaled point
	Point2i scaledSmoothPoint(scaledSmoothedPointX, scaledSmoothedPointY);
	LOG("Scaled Smoothed Gaze Point PX: X: %d Y: %d \n",
				scaledSmoothPoint.x, scaledSmoothPoint.y);
	circle(frame_bgr, scaledSmoothPoint, 20, YELLOW, -1);

	//Prepare the result
	static int result[4];
	result[0] = scaledSmoothPoint.x;
	result[1] = scaledSmoothPoint.y;
	result[2] = gaze_pt_px.x;
	result[3] = gaze_pt_px.y;
	return result;
}
