package de.vion.eyetracking.cameracalib.calibration;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import de.vion.eyetracking.R;
import de.vion.eyetracking.cameracalib.calibration.callback.CalibrationAsyncTaskCallback;
import de.vion.eyetracking.cameracalib.calibration.opencv.CameraCalibrator;
import de.vion.eyetracking.misc.Toaster;
import de.vion.eyetracking.settings.GazeTrackingPreferences;

/**
 * 
 * The Task for performing the camera calibration based on the captured pictures
 * 
 * @author André Pomp
 * 
 */
public class CalibrationAsyncTask extends AsyncTask<Void, Void, Void> {

	private Context context;
	private CameraCalibrator calibrator;
	private ProgressDialog dialog;
	private CalibrationAsyncTaskCallback callback;

	public CalibrationAsyncTask(Context context, CameraCalibrator calibrator,
			CalibrationAsyncTaskCallback callback) {
		super();
		this.context = context;
		this.calibrator = calibrator;
		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
		this.dialog = new ProgressDialog(this.context);
		this.dialog.setTitle(this.context
				.getString(R.string.asynctask_calibration_dialog_title));
		this.dialog.setMessage(this.context
				.getString(R.string.asynctask_calibration_dialog_message));
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		this.calibrator.calibrate();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		try {
			this.dialog.dismiss();
			this.dialog = null;
		} catch (Exception e) {
			// nothing
		}
		this.calibrator.clearCorners();
		String resultMessage = (this.calibrator.isCalibrated()) ? this.context
				.getString(R.string.asynctask_calibration_successfulL_calibration)
				: this.context
						.getString(R.string.asynctask_calibration_unsuccessfulL_calibration);
		Toaster.makeToast(this.context, resultMessage);

		if (this.calibrator.isCalibrated()) {
			// mark camera as calibrated
			GazeTrackingPreferences.setIsCalibrated(this.context, true);

			// See openCV camera calibration example for more information
			double[] cameraMatrixArray = new double[9];
			this.calibrator.getCameraMatrix().get(0, 0, cameraMatrixArray);

			float focalLenX = (float) cameraMatrixArray[0];
			float focalLenY = (float) cameraMatrixArray[4];
			float prinPointX = (float) cameraMatrixArray[2];
			float prinPointY = (float) cameraMatrixArray[5];
			GazeTrackingPreferences.setIntrinsicCameraValues(this.context,
					focalLenX, focalLenY, prinPointX, prinPointY);

			double[] distortionCoefficientsArray = new double[5];
			this.calibrator.getDistortionCoefficients().get(0, 0,
					distortionCoefficientsArray);

			float value0 = (float) distortionCoefficientsArray[0];
			float value1 = (float) distortionCoefficientsArray[1];
			float value2 = (float) distortionCoefficientsArray[2];
			float value3 = (float) distortionCoefficientsArray[3];
			float value4 = (float) distortionCoefficientsArray[4];
			GazeTrackingPreferences.setCameraDistortionValues(this.context,
					value0, value1, value2, value3, value4);
		}
		this.callback.onTaskFinished();
	}
}