using namespace std;
using namespace cv;

void eye_detect_init();

bool detect_eyes(const Mat& frame_bgr, Rect& eye0_roi, Rect& eye1_roi);
