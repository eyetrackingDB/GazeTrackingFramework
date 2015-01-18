#include "stdafx.h"

#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <math.h>
#include "../lib_eigen/Geometry"
#include "../lib_eigen/StdVector"

#include "utils.h"
#include "ConicSection.h"

#include "../GazeSettingsEyeTab.h"

using namespace Eigen;
using namespace std;
using namespace cv;

const float LIMBUS_R_MM = 6;

vector<Vector3d> ellipse_to_limbus_approx(cv::RotatedRect ellipse, bool limbus_switch=true){
	vector<Vector3d> limbus_to_return;

	double maj_axis_px = ellipse.size.width, min_axis_px = ellipse.size.height;

	// Using iris_r_px / focal_len_px = iris_r_mm / distance_to_iris_mm
	double iris_z_mm = (LIMBUS_R_MM * 2 * FOCAL_LEN_Z_PX) / maj_axis_px;
    
    // Using (x_screen_px - prin_point) / focal_len_px = x_world / z_world
	double iris_x_mm = -iris_z_mm * (ellipse.center.x - PRIN_POINT.x) / FOCAL_LEN_X_PX;
    double iris_y_mm = iris_z_mm * (ellipse.center.y - PRIN_POINT.y) / FOCAL_LEN_Y_PX;

	Vector3d limbus_center(iris_x_mm, iris_y_mm, iris_z_mm);
	double psi = CV_PI / 180.0 * (ellipse.angle+90);    // z-axis rotation (radians)
	double tht = acos(min_axis_px / maj_axis_px);       // y-axis rotation (radians)

	if (limbus_switch) tht = -tht;                      // ambiguous acos, so sometimes switch limbus

    // Get limbus normal for chosen theta
    Vector3d limb_normal(sin(tht) * cos(psi), -sin(tht) * sin(psi), -cos(tht));

	// Now correct for weak perspective by modifying angle by offset between camera axis and limbus
    double x_correction = -atan2(iris_y_mm, iris_z_mm);
    double y_correction = -atan2(iris_x_mm, iris_z_mm);

    AngleAxisd rot1(y_correction, Vector3d(0,-1,0));
	AngleAxisd rot2(x_correction, Vector3d(1,0,0));
    limb_normal = rot1 * limb_normal;

    limb_normal = rot2 * limb_normal;

	limbus_to_return.push_back(limbus_center);
	limbus_to_return.push_back(limb_normal);
	return limbus_to_return;
}

vector<Vector3d> ellipse_to_limbus_geo(cv::RotatedRect ellipse) {
	vector<Vector3d> limbus_to_return;

	double maj_axis_px = ellipse.size.width;

	// Using iris_r_px / focal_len_px = iris_r_mm / distance_to_iris_mm
	double iris_z_mm = (LIMBUS_R_MM * 2 * FOCAL_LEN_Z_PX) / maj_axis_px;

	// Using (x_screen_px - prin_point) / focal_len_px = x_world / z_world
	double iris_x_mm = -iris_z_mm * (ellipse.center.x - PRIN_POINT.x)
			/ FOCAL_LEN_X_PX;
	double iris_y_mm = iris_z_mm * (ellipse.center.y - PRIN_POINT.y)
			/ FOCAL_LEN_Y_PX;

	Vector3d limbus_center(iris_x_mm, iris_y_mm, iris_z_mm);

	//Create the new rotated rectangle
	float rotRecX0 = ellipse.center.x - PRIN_POINT.x;
	float rotRecY0 = ellipse.center.y - PRIN_POINT.y;
	RotatedRect rotRect(Point2f(rotRecX0, rotRecY0), ellipse.size,
			ellipse.angle);

	//Create the conic section object and a matrix of it
	ConicSection ell(rotRect);
	Matx33f mati(ell.A, ell.B / 2.0f, ell.D / (2.0f * FOCAL_LEN_Z_PX),
			 ell.B / 2.0f,  ell.C, ell.E / (2.0f * FOCAL_LEN_Z_PX),
			 ell.D / (2.0f * FOCAL_LEN_Z_PX), ell.E / (2.0f * FOCAL_LEN_Z_PX), ell.F / (FOCAL_LEN_Z_PX * FOCAL_LEN_Z_PX));

	Matx31f eigenValues;
	Matx33f eigenVectors;
	eigen(mati, true, eigenValues, eigenVectors);

	//Get the indexes for sorting
	vector<float> eigValVec(3);
	for(int i=0; i<eigValVec.size(); i++){
		eigValVec[i] = eigenValues.val[i];
	}
	vector<size_t> indexes = sort_indexes(eigValVec);

	//Rearange the matrix
	Matx31f sortedEigenValues;
	Matx33f sortedEigenVectors;
	int counter = 0;
	for(size_t i : indexes){
		sortedEigenValues.val[i] = eigenValues.val[counter];

		sortedEigenVectors.val[i*3] = eigenVectors.val[counter*3];
		sortedEigenVectors.val[(i*3)+1] = eigenVectors.val[(counter*3) + 1];
		sortedEigenVectors.val[(i*3)+2] = eigenVectors.val[(counter*3) + 2];
		counter++;
	}
	float L1 = sortedEigenValues.val[0];
	float L2 = sortedEigenValues.val[1];
	float L3 = sortedEigenValues.val[2];

	// Calculate the parameters for determining the normals
	float g = sqrt((L2-L3) / (L1-L3));
	float h = sqrt((L1-L2) / (L1-L3));

	//Calculate the normals
	float norm1_x = sortedEigenVectors.val[0] * h + sortedEigenVectors.val[1] * 0 + sortedEigenVectors.val[2] * -g;
	float norm1_y = sortedEigenVectors.val[3] * h + sortedEigenVectors.val[4] * 0 + sortedEigenVectors.val[5] * -g;
	float norm1_z = sortedEigenVectors.val[6] * h + sortedEigenVectors.val[7] * 0 + sortedEigenVectors.val[8] * -g;

	float norm2_x = sortedEigenVectors.val[0] * h + sortedEigenVectors.val[1] * 0 + sortedEigenVectors.val[2] * g;
	float norm2_y = sortedEigenVectors.val[3] * h + sortedEigenVectors.val[4] * 0 + sortedEigenVectors.val[5] * g;
	float norm2_z = sortedEigenVectors.val[6] * h + sortedEigenVectors.val[7] * 0 + sortedEigenVectors.val[8] * g;

	float norm3_x = sortedEigenVectors.val[0] * -h + sortedEigenVectors.val[1] * 0 + sortedEigenVectors.val[2] * -g;
	float norm3_y = sortedEigenVectors.val[3] * -h + sortedEigenVectors.val[4] * 0 + sortedEigenVectors.val[5] * -g;
	float norm3_z = sortedEigenVectors.val[6] * -h + sortedEigenVectors.val[7] * 0 + sortedEigenVectors.val[8] * -g;

	float norm4_x = sortedEigenVectors.val[0] * -h + sortedEigenVectors.val[1] * 0 + sortedEigenVectors.val[2] * g;
	float norm4_y = sortedEigenVectors.val[3] * -h + sortedEigenVectors.val[4] * 0 + sortedEigenVectors.val[5] * g;
	float norm4_z = sortedEigenVectors.val[6] * -h + sortedEigenVectors.val[7] * 0 + sortedEigenVectors.val[8] * g;

	// Consider the constraints (some are specific for reverse portrait!!)
	float nx, ny, nz;
	if(iris_x_mm > 0){
		nx = norm1_x;
		ny = norm1_y;
		nz = norm1_z;
	} else {
		nx = norm2_x;
		ny = norm2_y;
		nz = norm2_z;
	}

	if(nz > 0){
		nx = -nx;
		ny = -ny;
		nz = -nz;
	}

	if((ny * nz) < 0){
		ny *= -1;
	}

	if(iris_x_mm > 0){
		if(nx > 0){
			nx *= -1;
		}
	} else if (nx < 0){
		nx *= -1;
	}
	//Finally, the normal
	Vector3d limb_normal(nx,ny,nz);

	limbus_to_return.push_back(limbus_center);
	limbus_to_return.push_back(limb_normal);
	return limbus_to_return;
}

bool isOutliner(Vector3d& limbusCenterEye0, Vector3d& limbusCenterEye1){
	//no need to remove gaze points that are out of the screen, as we want them to exist
	//only remove gaze points where the eye center distance is higher than 80mm

	double diffX = limbusCenterEye1.x() - limbusCenterEye0.x();
	double diffY = limbusCenterEye1.y() - limbusCenterEye0.y();
	double diffZ = limbusCenterEye1.z() - limbusCenterEye0.z();
	double distance = sqrt(pow(diffX, 2.0) + pow(diffY, 2.0) + pow(diffZ, 2.0));

	if(distance > 80.0){ //check if we distance is bigger than 80.0mm
		return true;
	}
	return false;
}

// returns intersection with z-plane of optical axis vector (mm)
Point2d get_gaze_point_mm(Vector3d limb_center, Vector3d limb_normal){
    // ray/plane intersection
    double t = -limb_center.z() / limb_normal.z();
    return Point2d(limb_center.x() + limb_normal.x() * t, limb_center.y() + limb_normal.y() * t);
}

void get_gaze_pt_mm(RotatedRect& ellipse, Vector3d& limbusCenter, Point2d& gp_mm){
	if(GAZE_TRACKING_ALGO == "GAZE_TRACKING_ALGO_APPROX"){
		// get two possible limbus centres and normals because of ambiguous trig
		vector<Vector3d> limbus_a = ellipse_to_limbus_approx(ellipse, true);
		vector<Vector3d> limbus_b = ellipse_to_limbus_approx(ellipse, false);

		Vector3d limb_center_a = limbus_a[0];
		Vector3d limb_normal_a = limbus_a[1];
		Vector3d limb_center_b = limbus_b[0];
		Vector3d limb_normal_b = limbus_b[1];

		// calculate gaze points for each possible limbus
		Point2d gp_mm_a = get_gaze_point_mm(limbus_a[0], limbus_a[1]);
		Point2d gp_mm_b = get_gaze_point_mm(limbus_b[0], limbus_b[1]);

		// calculate distance from centre of screen for each possible gaze point
		int dist_a = std::abs(gp_mm_a.x) + std::abs(gp_mm_a.y);
		int dist_b = std::abs(gp_mm_b.x) + std::abs(gp_mm_b.y);

		if(dist_a < dist_b){
			gp_mm = gp_mm_a;
			limbusCenter = limb_center_a;
		} else {
			gp_mm = gp_mm_b;
			limbusCenter = limb_center_b;
		}
	} else if (GAZE_TRACKING_ALGO == "GAZE_TRACKING_ALGO_GEO"){
		vector<Vector3d> limbus = ellipse_to_limbus_geo(ellipse);

		Vector3d limb_center = limbus[0];
		Vector3d limb_normal = limbus[1];

		gp_mm = get_gaze_point_mm(limbus[0], limbus[1]);
	}
}

Point2i convert_gaze_pt_mm_to_px(Point2d gaze_pt_mm){
	int gp_px_x = (gaze_pt_mm.x + CAMERA_OFFSET_MM.x) / SCREEN_SIZE_MM.width * SCREEN_SIZE_PX.width;
	//###### Changed to fit python code (gaze_pt_mm.y - CAMERA_OFFSET_MM.y) Before it was a +
	int gp_px_y = (gaze_pt_mm.y - CAMERA_OFFSET_MM.y) / SCREEN_SIZE_MM.height * SCREEN_SIZE_PX.height;
    return Point2i(gp_px_x, gp_px_y);
}
