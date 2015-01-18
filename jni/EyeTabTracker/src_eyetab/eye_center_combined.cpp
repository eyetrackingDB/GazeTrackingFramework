#include "stdafx.h"

#include <opencv2/core/core.hpp>

#include "eye_center_isophotes.h"
#include "../src_eyelike/findEyeCenter.h"

using namespace cv;

const float weightGrads = 0.8f;
const float weightIso = 0.2f;

Point find_eye_center_combined(const Mat& frame_bgr, const Rect& eyeROI,
		float fastScaleWidthGrads, float fastScaleWidthIso) {
	// Find the eye center of using both methods
	Point center_grads = findEyeCenter(frame_bgr(eyeROI), fastScaleWidthGrads);
	Point center_iso = find_eye_center_isophotes(frame_bgr(eyeROI),
			fastScaleWidthIso);

	// Combine the eye enter
	Point center;
	center.x = int(center_grads.x * weightGrads + center_iso.x * weightIso);
	center.y = int(center_grads.y * weightGrads + center_iso.y * weightIso);
	return center;
}
