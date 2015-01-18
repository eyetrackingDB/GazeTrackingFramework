package de.vion.eyetracking.gazedetection.post.misc;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2RGBA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGBA2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.graphics.Bitmap;


/**
 * The converter class for the frame 
 * 
 * @author André Pomp
 * 
 */
public class FrameConverter {

	// Images that are relevant for the source
	private IplImage ipl2bitmap_frame;
	private Bitmap sourceBitmap;

	// The openCV mat object is important for source and target
	// It has to get the dimensions of the source (can be modified later)
	private Mat openCVMat;

	// Images that are relevant for the target
	private IplImage bitmap2ipl_frame_3CH;
	private IplImage bitmap2ipl_frame_4CH;
	private Bitmap finalBitmap;

	public FrameConverter(int sourceWidth, int sourceHeight, int targetWidth,
			int targetHeight) {
		super();
		// Source images
		this.ipl2bitmap_frame = IplImage.create(sourceWidth, sourceHeight,
				IPL_DEPTH_8U, 4);
		this.sourceBitmap = Bitmap.createBitmap(sourceWidth, sourceHeight,
				Bitmap.Config.ARGB_8888);

		// OpenCV
		this.openCVMat = new Mat(sourceWidth, sourceHeight, CvType.CV_8UC4);

		// Target Images
		this.bitmap2ipl_frame_3CH = IplImage.create(targetWidth, targetHeight,
				IPL_DEPTH_8U, 3);
		this.bitmap2ipl_frame_4CH = IplImage.create(targetWidth, targetHeight,
				IPL_DEPTH_8U, 4);
		this.finalBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);
	}

	public Mat convertJavaCVtoOpenCV(IplImage frame) {
		// Covert to IPLImage with 3 channels and BGR format to
		// IPLImage with 4 channels and RGBA format
		cvCvtColor(frame, this.ipl2bitmap_frame, CV_BGR2RGBA);

		// Convert IPLImage with 4 channels to Bitmap
		this.sourceBitmap.copyPixelsFromBuffer(this.ipl2bitmap_frame
				.getByteBuffer());

		// Convert Bitmap to Mat (plain openCV)
		Utils.bitmapToMat(this.sourceBitmap, this.openCVMat);

		return this.openCVMat;
	}

	public IplImage convertOpenCVtoJavaCV(Mat frame) {
		// Convert Mat (plain openCV) to Bitmap
		Utils.matToBitmap(this.openCVMat, this.finalBitmap);

		// Convert the Bitmap to IPLImage with 4 channels
		this.finalBitmap.copyPixelsToBuffer(this.bitmap2ipl_frame_4CH
				.getByteBuffer());

		// Covert to IPLImage with 4 channels and RGBA format to
		// IPLImage with 3 channels and BGR format
		cvCvtColor(this.bitmap2ipl_frame_4CH, this.bitmap2ipl_frame_3CH,
				CV_RGBA2BGR);

		return this.bitmap2ipl_frame_3CH;
	}
}