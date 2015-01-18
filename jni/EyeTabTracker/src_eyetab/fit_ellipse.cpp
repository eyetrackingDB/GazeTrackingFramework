#include "stdafx.h"

#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <tbb/tbb.h>

#include "ConicSection.h"
#include "utils.h"

#include "../../Logging.h"

using namespace std;

// adapted from https://bitbucket.org/Leszek/pupil-tracker/ - Lech Swirski
// robustly fit ellipse to potential limbus points with RANSAC
cv::RotatedRect fit_ellipse(std::vector<cv::Point2f> edgePoints,
		cv::Mat_<float> mPupilSobelX, cv::Mat_<float> mPupilSobelY) {

	// Number of points needed for a model
	const int n = 5;

	cv::RotatedRect elPupil;

	if (edgePoints.size() >= n) // Minimum points for ellipse
			{
		// Number of ransac iterations
		int k = 50; //changed from 10

		// Use TBB for RANSAC
		struct EllipseRansac_out {
			std::vector<cv::Point2f> bestInliers;
			cv::RotatedRect bestEllipse;
			double bestEllipseGoodness;
			int earlyRejections;
			bool earlyTermination;

			EllipseRansac_out() :
					bestEllipseGoodness(
							-std::numeric_limits<double>::infinity()), earlyTermination(
							false), earlyRejections(0) {
			}
		};

		struct EllipseRansac {
			const std::vector<cv::Point2f>& edgePoints;
			int n;
			const cv::Rect& bb;
			const cv::Mat_<float>& mDX;
			const cv::Mat_<float>& mDY;
			int earlyRejections;
			bool earlyTermination;

			EllipseRansac_out out;

			EllipseRansac(const std::vector<cv::Point2f>& edgePoints, int n,
					const cv::Rect& bb, const cv::Mat_<float>& mDX,
					const cv::Mat_<float>& mDY) :
					edgePoints(edgePoints), n(n), bb(bb), mDX(mDX), mDY(mDY), earlyTermination(
							false), earlyRejections(0) {
			}

			EllipseRansac(EllipseRansac& other, tbb::split) :
					edgePoints(other.edgePoints), n(other.n), bb(other.bb), mDX(
							other.mDX), mDY(other.mDY), earlyTermination(
							other.earlyTermination), earlyRejections(
							other.earlyRejections) {
			}

			void operator()(const tbb::blocked_range<size_t>& r) {
				if (out.earlyTermination)
					return;

				for (size_t i = r.begin(); i != r.end(); ++i) {
					// Ransac Iteration
					// ----------------
					std::vector<cv::Point2f> sample;
					sample = randomSubset(edgePoints, n);

					cv::RotatedRect ellipseSampleFit = fitEllipse(sample);

					// Normalise ellipse to have width as the major axis.
					if (ellipseSampleFit.size.height
							> ellipseSampleFit.size.width) {
						ellipseSampleFit.angle = std::fmod(
								ellipseSampleFit.angle + 90, 180);
						std::swap(ellipseSampleFit.size.height,
								ellipseSampleFit.size.width);
					}

					const int Radius_Max = 100;
					const int Radius_Min = 20;
					cv::Size s = ellipseSampleFit.size;
					cv::Rect r = ellipseSampleFit.boundingRect();

					// Discard useless ellipses early
					if (!ellipseSampleFit.center.inside(bb)
							|| s.height > Radius_Max * 2
							|| s.width > Radius_Max * 2
							|| s.height < Radius_Min * 2
									&& s.width < Radius_Min * 2
							|| s.height > 4 * s.width
							|| s.width > 4 * s.height) {
						continue;
					}

					// Use conic section's algebraic distance as an error measure
					ConicSection conicSampleFit(ellipseSampleFit);

					// Check if sample's gradients are correctly oriented
					bool gradientCorrect = true;
					for (const cv::Point2f& p : sample) {
						cv::Point2f grad = conicSampleFit.algebraicGradientDir(
								p);
						float dx = mDX(cv::Point(p.x, p.y));
						float dy = mDY(cv::Point(p.x, p.y));

						float dotProd = dx * grad.x + dy * grad.y;

						gradientCorrect &= dotProd > 0;
					}

					// If gradients not matched, EARLY REJECTION
					if (!gradientCorrect) {
						out.earlyRejections++;
						continue;
					}

					// Assume that the sample is the only inliers

					cv::RotatedRect ellipseInlierFit = ellipseSampleFit;
					ConicSection conicInlierFit = conicSampleFit;
					std::vector<cv::Point2f> inliers, prevInliers;

					// Iteratively find inliers, and re-fit the ellipse
					const int InlierIterations = 6; //changed from 3
					for (int i = 0; i < InlierIterations; ++i) {
						// Get error scale for 1px out on the minor axis
						cv::Point2f minorAxis(-std::sin(
						CV_PI / 180.0 * ellipseInlierFit.angle), std::cos(
						CV_PI / 180.0 * ellipseInlierFit.angle));
						cv::Point2f minorAxisPlus1px = ellipseInlierFit.center
								+ (ellipseInlierFit.size.height / 2 + 1)
										* minorAxis;
						float errOf1px = conicInlierFit.distance(
								minorAxisPlus1px);
						float errorScale = 1.0f / errOf1px;

						// Find inliers
						inliers.reserve(edgePoints.size());
						const float MAX_ERR = 2;
						for (const cv::Point2f& p : edgePoints) {
							float err = errorScale * conicInlierFit.distance(p);

							if (err * err < MAX_ERR * MAX_ERR)
								inliers.push_back(p);
						}

						if (inliers.size() < n) {
							inliers.clear();
							continue;
						}

						// Refit ellipse to inliers
						ellipseInlierFit = fitEllipse(inliers);
						conicInlierFit = ConicSection(ellipseInlierFit);

						// Normalise ellipse to have width as the major axis.
						if (ellipseInlierFit.size.height
								> ellipseInlierFit.size.width) {
							ellipseInlierFit.angle = std::fmod(
									ellipseInlierFit.angle + 90, 180);
							std::swap(ellipseInlierFit.size.height,
									ellipseInlierFit.size.width);
						}
					}
					if (inliers.empty()) {
						continue;
					}

					// Discard useless ellipses again
					s = ellipseInlierFit.size;
					if (!ellipseInlierFit.center.inside(bb)
							|| s.height > Radius_Max * 2
							|| s.width > Radius_Max * 2
							|| s.height < Radius_Min * 2
									&& s.width < Radius_Min * 2
							|| s.height > 4 * s.width
							|| s.width > 4 * s.height) {
						continue;
				}

					// Calculate ellipse goodness (Image aware support)
					double ellipseGoodness = 0;
					for (cv::Point2f& p : inliers) {
						cv::Point2f grad = conicInlierFit.algebraicGradientDir(
								p);
						float dx = mDX(p);
						float dy = mDY(p);

						double edgeStrength = dx * grad.x + dy * grad.y;

						ellipseGoodness += edgeStrength;
					}

					int earlyTerminationPercentage = 95;
					// If ellipse goodness is high enough, store new ellipse (no early termination)
					if (ellipseGoodness > out.bestEllipseGoodness) {
						std::swap(out.bestEllipseGoodness, ellipseGoodness);
						std::swap(out.bestInliers, inliers);
						std::swap(out.bestEllipse, ellipseInlierFit);

						// Early termination, if 90% of points match
						if (earlyTerminationPercentage > 0
								&& out.bestInliers.size()
										> earlyTerminationPercentage
												* edgePoints.size() / 100) {
							earlyTermination = true;
							break;
						}
					}
				}
			}

			void join(EllipseRansac& other) {

				// RANSAC join
				if (other.out.bestEllipseGoodness > out.bestEllipseGoodness) {
					std::swap(out.bestEllipseGoodness,
							other.out.bestEllipseGoodness);
					std::swap(out.bestInliers, other.out.bestInliers);
					std::swap(out.bestEllipse, other.out.bestEllipse);
				}
				out.earlyRejections += other.out.earlyRejections;
				earlyTermination |= other.earlyTermination;

				out.earlyTermination = earlyTermination;
			}
		};

		// iris center must be close to refined ROI center
		cv::Rect bbPupil = roiAround(mPupilSobelX.size().width / 2,
				mPupilSobelX.size().width / 2,
				int(mPupilSobelX.size().width * 0.5));

		EllipseRansac ransac(edgePoints, n, bbPupil, mPupilSobelX,
				mPupilSobelY);

		try {
			tbb::parallel_reduce(tbb::blocked_range<size_t>(0, k, k / 8),
					ransac);
		} catch (std::exception& e) {
			const char* c = e.what();
			LOG("TBB Exception \n");
		}

		cv::RotatedRect ellipseBestFit = ransac.out.bestEllipse;
		LOG("Ellipse CenterX: %f CenterY: %f \n", ellipseBestFit.center.x,
				ellipseBestFit.center.y);
		elPupil = ellipseBestFit;
	}
	return elPupil;
}
