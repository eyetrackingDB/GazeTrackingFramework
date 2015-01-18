using namespace cv;

Mat get_centermap_isophotes(Mat& eye_grey, float fastScaleWidth);

Point find_eye_center_isophotes(const Mat& eye_bgr, float fastScaleWidth);
