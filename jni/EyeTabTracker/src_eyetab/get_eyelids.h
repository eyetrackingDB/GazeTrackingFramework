using namespace cv;

vector<Point> guess_eye_corners(Point2i& eye_center, Point2i& eye_cs_vec);

Point find_upper_eyelid_pt(Point2i& eye_center, const Mat& eye_c_col);

Mat get_upper_eyelid(const Point2i& eye_center, const Point2i& eye_cs_vec, const Mat& eye_bgr);

vector<Point2f> filter_poss_limb_pts(const vector<Point2f>& poss_limb_pts, const Mat& parabola, const Point2i& roi_tl);

void draw_eyelid(Mat& img, Mat& parabola, Rect& roi, Scalar color = RED);
