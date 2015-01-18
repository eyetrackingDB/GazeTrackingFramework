using namespace cv;

Point find_eye_center_combined(const Mat& frame_bgr, const Rect& eyeROI,
		float fastScaleWidthGrads, float fastScaleWidthIso);
