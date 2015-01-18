package de.vion.eyetracking.logging;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * 
 * This class manages the logging of different sensors
 * 
 * @author André Pomp
 * 
 */
public class SensorLoggerManager {

	private Context context;
	private String subDirectory;

	private List<SensorLogger> listOfLoggers = new ArrayList<SensorLogger>();

	public SensorLoggerManager(Context context, String subDirectory) {
		this.context = context;
		this.subDirectory = subDirectory;
	}

	public void startLogging() {
		for (SensorLogger logger : this.listOfLoggers) {
			logger.startLogging();
		}
	}

	public void stopLogging() {
		for (SensorLogger logger : this.listOfLoggers) {
			logger.stopLogging();
		}
	}

	public void addSensor(int sensorType) {
		SensorLogger logger = new SensorLogger(sensorType, this.subDirectory,
				this.context);
		this.listOfLoggers.add(logger);
	}
}