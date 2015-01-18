#include "../lib_eigen/Geometry"

using namespace Eigen;
using namespace std;
using namespace cv;

bool isOutliner(Vector3d& limbusCenterEye0, Vector3d& limbusCenterEye1);

void get_gaze_pt_mm(RotatedRect& ellipse, Vector3d& limbusCenter, Point2d& gp_mm);

Point2i convert_gaze_pt_mm_to_px(Point2d gaze_pt_mm);
