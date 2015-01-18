#include "stdafx.h"

#include <limits>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

const double weight_ratio_edge = 1.0;
const double weight_ratio_darkness = 0.0;

void nan_to_num(Mat& mat) {
	float value = 0.0f;
	for (int i = 0; i < mat.rows; i++) {
		for (int j = 0; j < mat.cols; j++) {
			value = mat.at<float>(i, j);
			if (value != value) { //check for NAN
				mat.at<float>(i, j) = 0.0f;
			} else if (value > 0 && value / value != value / value) { //check for positive infinity
				mat.at<float>(i, j) = numeric_limits<float>::max();
			} else if (value < 0 && value / value != value / value) { //check for negative infinity
				mat.at<float>(i, j) = numeric_limits<float>::min();
			}
		}
	}
}

Mat get_centermap_isophotes(Mat& eye_grey, float fastScaleWidth) {

	// Calculate the gradients
	Mat f_x, f_y, f_xy, f_xx, f_yy;
	Sobel(eye_grey, f_x, CV_32F, 1, 0, 9);
	Sobel(eye_grey, f_y, CV_32F, 0, 1, 9);
	Sobel(eye_grey, f_xy, CV_32F, 1, 1, 9);
	Sobel(eye_grey, f_xx, CV_32F, 2, 0, 9);
	Sobel(eye_grey, f_yy, CV_32F, 0, 2, 9);

	// Calculate the curved-ness
	Mat f_xx_square = f_xx.mul(f_xx);
	Mat f_xy_double_square = 2.0 * (f_xy.mul(f_xy));
	Mat f_yy_square = f_yy.mul(f_yy);
	Mat curvedness;
	sqrt((f_xx_square + f_xy_double_square + f_yy_square), curvedness);
	Mat curvedness_norm;
	normalize(curvedness, curvedness_norm, 0, 255, NORM_MINMAX);
	curvedness_norm.convertTo(curvedness_norm, CV_8U); //convert to 8 bit unsigned int

	// Calculate the weighting function
	Mat weight_edge;
	normalize(curvedness, weight_edge, 0, 255.0 * weight_ratio_edge,
			NORM_MINMAX);
	Mat weight_middle;
	normalize(curvedness, weight_middle, 0, 255.0 * weight_ratio_darkness,
			NORM_MINMAX);

	//Calculate the displacement vectors
	Mat temp_top = f_x.mul(f_x) + f_y.mul(f_y);
	Mat temp_bot = (f_y.mul(f_y)).mul(f_xx) - ((2 * f_x).mul(f_xy)).mul(f_y)
			+ (f_x.mul(f_x)).mul(f_yy) + 0.0001;
	Mat d_vec_mul;
	divide((-1 * temp_top), temp_bot, d_vec_mul);
	Mat d_vec_x = d_vec_mul.mul(f_x);
	Mat d_vec_y = d_vec_mul.mul(f_y);

	//Remove infinite displacements for straight lines
	Mat mag;
	nan_to_num(d_vec_x);
	nan_to_num(d_vec_y);
	magnitude(d_vec_x, d_vec_y, mag);

	//Prevent using weights with bad radius size
	double min_rad = fastScaleWidth / 8.0;
	double max_rad = fastScaleWidth / 2.0;
	weight_edge.setTo(0, mag < min_rad);
	weight_edge.setTo(0, mag > max_rad);

	//Calculate curvature to ensure we use gradients which point in right direction
	Mat power;
	cv::pow(temp_top, 1.5, power);
	Mat curvature;
	divide(temp_bot, (0.0001 + power), curvature);
	weight_edge.setTo(0, curvature < 0);
	weight_edge.setTo(0, curvedness_norm < 20);

	// Initialize 1d vectors of x and y indices of Mat
	vector<int> x_inds_vec, y_inds_vec;
	for (int i = 0; i < eye_grey.size().width; i++)
		x_inds_vec.push_back(i);
	for (int i = 0; i < eye_grey.size().height; i++)
		y_inds_vec.push_back(i);

	// Repeat vectors to form indices Mats
	Mat x_inds(x_inds_vec), y_inds(y_inds_vec);
	x_inds = repeat(x_inds.t(), eye_grey.size().height, 1);
	y_inds = repeat(y_inds, 1, eye_grey.size().width);

	// Cast explicity from float to int as convertTO rounds in another way
	for (int i = 0; i < d_vec_x.rows; i++) {
		for (int j = 0; j < d_vec_x.cols; j++) {
			d_vec_x.at<float>(i, j) = ((int) d_vec_x.at<float>(i, j));
		}
	}
	d_vec_x.convertTo(d_vec_x, CV_32S); //finally do convertTO on already rounded matrix

	// Cast explicity from float to int as convertTO rounds in another way
	for (int i = 0; i < d_vec_y.rows; i++) {
		for (int j = 0; j < d_vec_y.cols; j++) {
			d_vec_y.at<float>(i, j) = ((int) d_vec_y.at<float>(i, j));
		}
	}
	d_vec_y.convertTo(d_vec_y, CV_32S); //finally do convertTO on already rounded matrix

	// Make indexes into accumulator using basic grid and vector offsets
	Mat acc_inds_x = x_inds + d_vec_x;
	Mat acc_inds_y = y_inds + d_vec_y;

	// Prevent indexes outside of accumulator
	acc_inds_x.setTo(0, acc_inds_x < 0);
	acc_inds_y.setTo(0, acc_inds_y < 0);
	acc_inds_x.setTo(0, acc_inds_x >= eye_grey.size().width);
	acc_inds_y.setTo(0, acc_inds_y >= eye_grey.size().height);

	// Make center map accumulator
	Mat accumulator = Mat::zeros(eye_grey.size(), CV_32F);

	// Add weights
	for (int y = 0; y < eye_grey.rows; y++) {
		for (int x = 0; x < eye_grey.cols; x++) {
			int indX = acc_inds_x.at<int>(y, x);
			int indY = acc_inds_y.at<int>(y, x);
			accumulator.at<float>(indY, indX) = weight_edge.at<float>(y, x);
		}
	}
	accumulator += weight_middle;

	// Post processing
	Mat morph_kernel = getStructuringElement(MORPH_ELLIPSE, Size(3, 3));
	morphologyEx(accumulator, accumulator, MORPH_DILATE, morph_kernel);
	GaussianBlur(accumulator, accumulator, Size(13, 13), 0);

	return accumulator;
}

Point find_eye_center_isophotes(const Mat& eye_bgr, float fastScaleWidth) {
	// Convert BGR coarse ROI to gray
	Mat eye_grey, eye_grey_small;
	cvtColor(eye_bgr, eye_grey, CV_BGR2GRAY);

	// Resize the image to a constant fast size
	float scale = fastScaleWidth / (float) eye_grey.size().width;
	resize(eye_grey, eye_grey_small, Size(0, 0), scale, scale);

	// Blur image
	GaussianBlur(eye_grey_small, eye_grey_small, Size(3, 3), 0);

	// Create centermap
	Mat centermap = get_centermap_isophotes(eye_grey_small, fastScaleWidth);

	// Find position of max value in small-size centermap
	Point maxLoc;
	minMaxLoc(centermap, NULL, NULL, NULL, &maxLoc);

	// Scale the maxLoc
	maxLoc = maxLoc * (1.0f / scale);

	return maxLoc;
}
