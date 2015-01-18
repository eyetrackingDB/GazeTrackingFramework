package de.vion.eyetracking.logging;

import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import de.vion.eyetracking.misc.FileManager;

/**
 * 
 * This class represents a sensor of a specified type and handles its logging
 * 
 * @author André Pomp
 * 
 */
public class SensorLogger implements SensorEventListener {

	private int sensorType;
	private SensorManager sensorManager;
	private Sensor sensor;
	private String subDirectory;
	private FileWriter fileWriter;

	public SensorLogger(int sensorType, String subDirectory, Context context) {
		super();
		this.sensorType = sensorType;
		this.subDirectory = subDirectory;
		this.sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		this.sensor = this.sensorManager.getDefaultSensor(sensorType);
	}

	public void startLogging() {
		String filePath = FileManager.getSensorFilePath(this.subDirectory,
				this.sensorType);
		try {
			this.fileWriter = new FileWriter(filePath);
			this.sensorManager.registerListener(this, this.sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopLogging() {
		try {
			this.sensorManager.unregisterListener(this, this.sensor);
			this.fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing todo
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == this.sensorType) {

			String result = event.timestamp + ";";
			if (this.sensorType == Sensor.TYPE_ROTATION_VECTOR) {
				float[] rotMat = new float[9];
				float[] orientValues = new float[3];

				// Convert the rotation-vector to a 4x4 matrix.
				SensorManager.getRotationMatrixFromVector(rotMat, event.values);
				SensorManager.remapCoordinateSystem(rotMat,
						SensorManager.AXIS_X, SensorManager.AXIS_Z, rotMat);
				SensorManager.getOrientation(rotMat, orientValues);

				// Optionally convert the result from radians to degrees
				orientValues[0] = (float) Math.toDegrees(orientValues[0]); // azimuth
				orientValues[1] = (float) Math.toDegrees(orientValues[1]); // pitch
				orientValues[2] = (float) Math.toDegrees(orientValues[2]); // roll
				for (float orientValue : orientValues) {
					result += orientValue + ";";
				}
			} else {
				for (float value : event.values) {
					result += value + ";";
				}
			}
			result += "\n";
			try {
				this.fileWriter.append(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}