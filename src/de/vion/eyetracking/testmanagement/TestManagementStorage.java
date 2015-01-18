package de.vion.eyetracking.testmanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.testframework.TestType;

/**
 * 
 * An in-memory storage for all conducted experiments
 * 
 * @author André Pomp
 * 
 */
public class TestManagementStorage {

	private static HashMap<TestType, List<String>> mapOfTests = new HashMap<TestType, List<String>>();

	public static void initStorage() {
		mapOfTests = new HashMap<TestType, List<String>>();

		// Get all test types
		TestType[] allTestTypes = TestType.values();
		for (TestType testype : allTestTypes) {
			// Create a list for the tests in this directory
			List<String> testFilePaths = new ArrayList<String>();

			// Get the current directory
			File testDirectory = new File(
					FileManager.getTestTypeDirectory(testype));

			// Get all files in the directory
			File[] listOfFiles = testDirectory.listFiles();

			// Add the tests to the array list
			for (File file : listOfFiles) {
				if (file.isDirectory()) {
					testFilePaths.add(file.getAbsolutePath() + "/");
				}
			}

			// Add the test type and the file paths to the map
			mapOfTests.put(testype, testFilePaths);
		}
	}

	public static Set<TestType> getAllTestTypes() {
		return mapOfTests.keySet();
	}

	public static List<String> getAllTestsForTestType(TestType type) {
		return mapOfTests.get(type);
	}
}
