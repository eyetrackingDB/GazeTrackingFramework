#ifndef EYE_CENTER_H
#define EYE_CENTER_H

#include "opencv2/imgproc/imgproc.hpp"

cv::Point findEyeCenter(const cv::Mat& eyeROIUnscaled, float fastScaleWidth);

#endif
