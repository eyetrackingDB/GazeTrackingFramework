package de.vion.eyetracking.testframework.logging;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

/**
 * The logger that stores the genereic test log object.
 * 
 * @author André Pomp
 * 
 */
public class TestLogger {

	private TestLogger() {
		// do not allow an instance
	}

	public static void storeTestData(String subDirectory, String fileName,
			JSONObject object) {
		String filePath = subDirectory + fileName;
		try {
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.append(object.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}