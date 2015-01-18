package de.vion.eyetracking.testframework.generic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

import de.vion.eyetracking.R;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.misc.Toaster;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.windowmanager.AppDisplayManager;

/**
 * 
 * The settings class that must be inherited by each test. It specifies settings
 * that are relevant for each test, such as the recording of the screen.
 * 
 * @author André Pomp
 * 
 */
public abstract class GenericTestSettings {

	public static final String PREF_RECORD_SCREEN = "pref_general_record_screen";
	public static final String PREF_SHOW_SECOND_SCREEN = "pref_general_secondscreen";
	public static final String PREF_SECOND_SCREEN_VIDEO = "pref_general_test_video_file_path";
	public static final String PREF_LOG_SENSOR_DATA = "pref_general_log_sensors";
	public static final String PREF_IMMERSIVE_MODE = "pref_general_immersive_mode";

	protected TestType testType;

	public GenericTestSettings(TestType testType) {
		super();
		this.testType = testType;
	}

	protected SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(this.testType.name(),
				Context.MODE_PRIVATE);
	}

	public boolean isRecordScreen(Context context) {
		return getSharedPreferences(context).getBoolean(PREF_RECORD_SCREEN,
				true);
	}

	public boolean isShowSecondScreen(Context context) {
		return getSharedPreferences(context).getBoolean(
				PREF_SHOW_SECOND_SCREEN, true);
	}

	public boolean isLogSensorData(Context context) {
		return getSharedPreferences(context).getBoolean(PREF_LOG_SENSOR_DATA,
				true);
	}

	public boolean isImmersiveMode(Context context) {
		return getSharedPreferences(context).getBoolean(PREF_IMMERSIVE_MODE,
				true);
	}

	public String getSecondScreenVideoFilePath(Context context) {
		return getSharedPreferences(context).getString(
				PREF_SECOND_SCREEN_VIDEO, "");
	}

	public boolean isValid(Context context) {
		if (isShowSecondScreen(context)) {
			File file = new File(getSecondScreenVideoFilePath(context));
			if (!file.exists()) {
				Toaster.makeToast(
						context,
						R.string.preferences_generic_test_toast_error_chosen_video_not_found);
				return false;
			}
		}

		if (isShowSecondScreen(context)
				&& !AppDisplayManager.isSecondScreenAvailable(context)) {
			Toaster.makeToast(
					context,
					R.string.preferences_generic_test_toast_error_missing_second_screen,
					Toast.LENGTH_LONG);
			return false;
		}

		if (isRecordScreen(context)) {
			if (!RootTools.isRootAvailable()) {
				Toaster.makeToast(
						context,
						R.string.preferences_generic_test_toast_error_device_not_rooted,
						Toast.LENGTH_LONG);
				return false;
			}

			if (!RootTools.isAccessGiven()) {
				Toaster.makeToast(
						context,
						R.string.preferences_generic_test_toast_error_no_root_access,
						Toast.LENGTH_LONG);
				return false;
			}
		}

		return true;
	}

	public void copySharedPreferences(Context context, String subdir) {
		File file = new File(FileManager.getTestPreferencesFilePath(subdir));

		// create some junk data to populate the shared preferences
		SharedPreferences prefs = getSharedPreferences(context);

		try {
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);

			Map<String, ?> prefsMap = prefs.getAll();

			for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
				pw.println(entry.getKey() + "=" + entry.getValue().toString());
			}

			pw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> loadSharedPreferences(String subdir) {
		File file = new File(FileManager.getTestPreferencesFilePath(subdir));

		Map<String, String> preferences = new HashMap<String, String>();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				// split only at the first occurence
				String[] splittedLine = line.split("=", 2);
				if (splittedLine.length > 1) {
					preferences.put(splittedLine[0].trim(),
							splittedLine[1].trim());
				} else {
					preferences.put(splittedLine[0].trim(), "");
				}
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return preferences;
	}

	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = new ArrayList<TableRow>();

		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_general_test_record_screen),
						listOfSettings.get(PREF_RECORD_SCREEN), context));

		boolean secondScreen = Boolean.valueOf(listOfSettings
				.get(PREF_SHOW_SECOND_SCREEN));
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_general_test_second_screen),
						listOfSettings.get(PREF_SHOW_SECOND_SCREEN), context));
		if (secondScreen) {
			String rawString = listOfSettings.get(PREF_SECOND_SCREEN_VIDEO);
			String fileName = rawString.substring(
					rawString.lastIndexOf("/") + 1, rawString.length());
			listOfTableRows
					.add(computeTableRow(
							context.getString(R.string.fragment_testmanagement_detail_tv_general_test_video_file),
							fileName, context));
		}

		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_general_test_immersive_mode),
						listOfSettings.get(PREF_IMMERSIVE_MODE), context));
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_general_test_log_sensors),
						listOfSettings.get(PREF_LOG_SENSOR_DATA), context));

		return listOfTableRows;
	}

	protected TableRow computeTableRow(String textRow1, String textRow2,
			Context context) {
		TableRow row = new TableRow(context);
		row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		TextView tvCol1 = new TextView(context);
		tvCol1.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		tvCol1.setPadding(0, 0, 10, 0);
		tvCol1.setText(textRow1);

		TextView tvCol2 = new TextView(context);
		tvCol2.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		tvCol2.setText(textRow2);

		row.addView(tvCol1);
		row.addView(tvCol2);
		return row;
	}
}