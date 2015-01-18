package de.vion.eyetracking.misc;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Accesses the shell and executes commands
 * 
 * @author André Pomp
 * 
 */
public class ShellAccess {

	public static final int INVALID_PID = -1;

	/**
	 * Executes a command as root. The result is the output of the console or
	 * null if we do not want any output
	 * 
	 * @param command
	 * @param waitForOutput
	 * @return
	 * @throws Exception
	 */
	public static String executeCommand(String command, boolean waitForOutput)
			throws Exception {

		// Gain root access
		Process p = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		InputStream is = p.getInputStream();

		// Execute the command
		os.writeBytes(command + "\n");
		os.flush();

		// Wait for output if command has output
		String result = null;
		if (waitForOutput) {
			int readed = 0;
			byte[] buff = new byte[4096];
			while (is.available() <= 0) {
				try {
					Thread.sleep(200);
				} catch (Exception ex) {
				}
			}
			while (is.available() > 0) {
				readed = is.read(buff);
				if (readed <= 0) {
					break;
				}
				result = new String(buff, 0, readed);
			}
		}

		// Exit the shell
		os.writeBytes("exit\n");
		os.flush();

		return result;
	}

	/**
	 * Calculates the PID from the console string
	 * 
	 * @param result
	 * @return
	 */
	public static List<Integer> getPIDsByConsoleString(String result) {
		String[] arr = result.split(" ");
		// Remove all strings which are empty or white-spaces
		List<String> filteredStrings = new ArrayList<String>();
		for (String element : arr) {
			String s = element.trim();
			if (s.length() > 0) {
				filteredStrings.add(s);
			}
		}

		// A PS command consists of 9 words per row. Only the first row has less
		// words.
		// Words 8,17 ... are the PIDS
		List<Integer> listOfPIDs = new ArrayList<Integer>();
		if (filteredStrings.size() >= 8) { // We need at least 8 words

			for (int i = 8; i < filteredStrings.size(); i += 9) {
				try { // see PS structure
					int pid = Integer.parseInt(filteredStrings.get(i));
					listOfPIDs.add(pid);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

		}
		return listOfPIDs;
	}
}