package de.vion.eyetracking.cameracalib.calibration.opencv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import android.util.Log;

/**
 * 
 * The class that performs the camera calibration. Adopted from OpenCV Camera
 * Calibration Application example (for the license, see the OpenCV license
 * in the licenses folder)
 * 
 */
public class CameraCalibrator {
	private static final String TAG = "OCVSample::CameraCalibrator";

	private final Size mPatternSize = new Size(4, 11);
	private final int mCornersSize = (int) (this.mPatternSize.width * this.mPatternSize.height);
	private boolean mPatternWasFound = false;
	private MatOfPoint2f mCorners = new MatOfPoint2f();
	private List<Mat> mCornersBuffer = new ArrayList<Mat>();
	private boolean mIsCalibrated = false;

	private Mat mCameraMatrix = new Mat();
	private Mat mDistortionCoefficients = new Mat();
	private int mFlags;
	private double mRms;
	private double mSquareSize = 0.0181;
	private Size mImageSize;

	public CameraCalibrator(int width, int height) {
		this.mImageSize = new Size(width, height);
		this.mFlags = Calib3d.CALIB_FIX_PRINCIPAL_POINT
				+ Calib3d.CALIB_ZERO_TANGENT_DIST
				+ Calib3d.CALIB_FIX_ASPECT_RATIO + Calib3d.CALIB_FIX_K4
				+ Calib3d.CALIB_FIX_K5;
		Mat.eye(3, 3, CvType.CV_64FC1).copyTo(this.mCameraMatrix);
		this.mCameraMatrix.put(0, 0, 1.0);
		Mat.zeros(5, 1, CvType.CV_64FC1).copyTo(this.mDistortionCoefficients);
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	public void processFrame(Mat grayFrame, Mat rgbaFrame) {
		findPattern(grayFrame);
		renderFrame(rgbaFrame);
	}

	public void calibrate() {
		ArrayList<Mat> rvecs = new ArrayList<Mat>();
		ArrayList<Mat> tvecs = new ArrayList<Mat>();
		Mat reprojectionErrors = new Mat();
		ArrayList<Mat> objectPoints = new ArrayList<Mat>();
		objectPoints.add(Mat.zeros(this.mCornersSize, 1, CvType.CV_32FC3));
		calcBoardCornerPositions(objectPoints.get(0));
		for (int i = 1; i < this.mCornersBuffer.size(); i++) {
			objectPoints.add(objectPoints.get(0));
		}

		Calib3d.calibrateCamera(objectPoints, this.mCornersBuffer,
				this.mImageSize, this.mCameraMatrix,
				this.mDistortionCoefficients, rvecs, tvecs, this.mFlags);

		this.mIsCalibrated = Core.checkRange(this.mCameraMatrix)
				&& Core.checkRange(this.mDistortionCoefficients);

		this.mRms = computeReprojectionErrors(objectPoints, rvecs, tvecs,
				reprojectionErrors);
	}

	public void clearCorners() {
		this.mCornersBuffer.clear();
	}

	private void calcBoardCornerPositions(Mat corners) {
		final int cn = 3;
		float positions[] = new float[this.mCornersSize * cn];

		for (int i = 0; i < this.mPatternSize.height; i++) {
			for (int j = 0; j < this.mPatternSize.width * cn; j += cn) {
				positions[(int) (i * this.mPatternSize.width * cn + j + 0)] = (2 * (j / cn) + i % 2)
						* (float) this.mSquareSize;
				positions[(int) (i * this.mPatternSize.width * cn + j + 1)] = i
						* (float) this.mSquareSize;
				positions[(int) (i * this.mPatternSize.width * cn + j + 2)] = 0;
			}
		}
		corners.create(this.mCornersSize, 1, CvType.CV_32FC3);
		corners.put(0, 0, positions);
	}

	private double computeReprojectionErrors(List<Mat> objectPoints,
			List<Mat> rvecs, List<Mat> tvecs, Mat perViewErrors) {
		MatOfPoint2f cornersProjected = new MatOfPoint2f();
		double totalError = 0;
		double error;
		float viewErrors[] = new float[objectPoints.size()];

		MatOfDouble distortionCoefficients = new MatOfDouble(
				this.mDistortionCoefficients);
		int totalPoints = 0;
		for (int i = 0; i < objectPoints.size(); i++) {
			MatOfPoint3f points = new MatOfPoint3f(objectPoints.get(i));
			Calib3d.projectPoints(points, rvecs.get(i), tvecs.get(i),
					this.mCameraMatrix, distortionCoefficients,
					cornersProjected);
			error = Core.norm(this.mCornersBuffer.get(i), cornersProjected,
					Core.NORM_L2);

			int n = objectPoints.get(i).rows();
			viewErrors[i] = (float) Math.sqrt(error * error / n);
			totalError += error * error;
			totalPoints += n;
		}
		perViewErrors.create(objectPoints.size(), 1, CvType.CV_32FC1);
		perViewErrors.put(0, 0, viewErrors);

		return Math.sqrt(totalError / totalPoints);
	}

	private void findPattern(Mat grayFrame) {
		this.mPatternWasFound = Calib3d.findCirclesGridDefault(grayFrame,
				this.mPatternSize, this.mCorners,
				Calib3d.CALIB_CB_ASYMMETRIC_GRID);
	}

	public void addCorners() {
		if (this.mPatternWasFound) {
			this.mCornersBuffer.add(this.mCorners.clone());
		}
	}

	private void drawPoints(Mat rgbaFrame) {
		Calib3d.drawChessboardCorners(rgbaFrame, this.mPatternSize,
				this.mCorners, this.mPatternWasFound);
	}

	private void renderFrame(Mat rgbaFrame) {
		drawPoints(rgbaFrame);

		Core.putText(rgbaFrame, "Captured: " + this.mCornersBuffer.size(),
				new Point(rgbaFrame.cols() / 3 * 2, rgbaFrame.rows() * 0.1),
				Core.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(255, 255, 0));
	}

	public Mat getCameraMatrix() {
		return this.mCameraMatrix;
	}

	public Mat getDistortionCoefficients() {
		return this.mDistortionCoefficients;
	}

	public int getCornersBufferSize() {
		return this.mCornersBuffer.size();
	}

	public double getAvgReprojectionError() {
		return this.mRms;
	}

	public boolean isCalibrated() {
		return this.mIsCalibrated;
	}

	public void setCalibrated() {
		this.mIsCalibrated = true;
	}
}