package de.vion.eyetracking.misc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 
 * Helper class for managing the zipping //Adopted from:
 * http://stackoverflow.com
 * /questions/6683600/zip-compress-a-folder-full-of-files-on-android
 * 
 */
public class ZipManager {

	public static boolean zipFileAtPath(String sourcePath, String toLocation) {
		// ArrayList<String> contentList = new ArrayList<String>();
		final int BUFFER = 2048;

		File sourceFile = new File(sourcePath);
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(toLocation);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			if (sourceFile.isDirectory()) {
				zipSubFolder(out, sourceFile, sourceFile.getParent().length());
			} else {
				byte data[] = new byte[BUFFER];
				FileInputStream fi = new FileInputStream(sourcePath);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * 
	 * Zips a subfolder
	 */

	private static void zipSubFolder(ZipOutputStream out, File folder,
			int basePathLength) throws IOException {

		final int BUFFER = 2048;

		File[] fileList = folder.listFiles();
		BufferedInputStream origin = null;
		for (File file : fileList) {
			if (file.isDirectory()) {
				zipSubFolder(out, file, basePathLength);
			} else {
				byte data[] = new byte[BUFFER];
				String unmodifiedFilePath = file.getPath();
				String relativePath = unmodifiedFilePath
						.substring(basePathLength);
				FileInputStream fi = new FileInputStream(unmodifiedFilePath);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(relativePath);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
		}
	}

	/*
	 * gets the last path component
	 * 
	 * Example: getLastPathComponent("downloads/example/fileToZip"); Result:
	 * "fileToZip"
	 */
	public static String getLastPathComponent(String filePath) {
		String[] segments = filePath.split("/");
		String lastPathComponent = segments[segments.length - 1];
		return lastPathComponent;
	}

	public static boolean compareZipAndFolder(String zipFilePath,
			String directoryFilePath) {

		try {
			ZipFile zipFile = new ZipFile(zipFilePath);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			int counter = 0;
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String rawName = entry.getName().substring(
						entry.getName().lastIndexOf("/") + 1,
						entry.getName().length());
				if (!containsFileAndIsTheSame(new File(directoryFilePath),
						rawName, entry.getSize())) {
					zipFile.close();
					return false;
				}
				counter++;
			}

			// If the folder contains at least the same files as the zip file
			// However, we have to check if the folder does not contain more
			// files
			if (counter != countNumberOfFiles(new File(directoryFilePath))) {
				zipFile.close();
				return false;
			}
			zipFile.close();
			return true;
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Zip Files not equal");
		return false;
	}

	/**
	 * Checks if a file is available in a directory structure
	 * 
	 * @param root
	 * @param fileName
	 * @return
	 */
	private static boolean containsFileAndIsTheSame(File root, String fileName,
			long fileSize) {
		if (root == null || fileName == null || fileName.isEmpty()) {
			return false;
		}

		if (root.isDirectory()) {
			for (File file : root.listFiles()) {
				if (containsFileAndIsTheSame(file, fileName, fileSize)) {
					return true;
				}
			}
			// Check if the files have the same name and the same length
		} else if (root.isFile() && root.getName().equals(fileName)
				&& fileSize == root.length()) {
			return true;
		}
		return false;
	}

	public static int countNumberOfFiles(File root) {
		if (root == null) {
			return 0;
		}

		int counter = 0;
		if (root.isDirectory()) {
			for (File file : root.listFiles()) {
				counter += countNumberOfFiles(file);
			}
		} else {
			counter++;
		}
		return counter;
	}
}