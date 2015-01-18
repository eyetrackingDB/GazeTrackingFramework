package de.vion.eyetracking.testframework.tests.rectangle.settings;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.TableRow;
import de.vion.eyetracking.R;
import de.vion.eyetracking.misc.Toaster;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;

/**
 * The general settings that are important for both rectangle tests
 * 
 * @author André Pomp
 * 
 */
public abstract class RectangleTestGeneralSettings extends GenericTestSettings {

	public static final String PREF_TIME_PER_RECTANGLE = "pref_rectangle_test_general_time_per_rectangle";
	public static final String PREF_PLAY_TONE = "pref_rectangle_test_general_play_tone";
	public static final String PREF_PAUSE_BETWEEN_RECTANGLES = "pref_rectangle_test_general_pause_between_rectangles";
	public static final String PREF_PLAY_MUSIC = "pref_rectangle_test_general_play_music";
	public static final String PREF_MUSIC_FILE_PATH = "pref_rectangle_test_general_music_file_path";

	public RectangleTestGeneralSettings(TestType type) {
		super(type);
	}

	public int getTimePerRectangle(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_TIME_PER_RECTANGLE,
								context.getString(R.string.test_rectangle_pref_time_per_rectangle_default)));
	}

	public int getPauseBetweenRectangles(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_PAUSE_BETWEEN_RECTANGLES,
								context.getString(R.string.test_rectangle_pref_pause_between_rectangles_default)));
	}

	public boolean isPlayTone(Context context) {
		return getSharedPreferences(context).getBoolean(PREF_PLAY_TONE, true);
	}

	public boolean isPlayMusic(Context context) {
		return getSharedPreferences(context).getBoolean(PREF_PLAY_MUSIC, true);
	}

	public String getMusicFilePath(Context context) {
		return getSharedPreferences(context)
				.getString(PREF_MUSIC_FILE_PATH, "");
	}

	@Override
	public boolean isValid(Context context) {
		File file = new File(getMusicFilePath(context));
		if (isPlayMusic(context) && !file.exists()) {
			Toaster.makeToast(
					context,
					R.string.preferences_rectangle_test_toast_error_chosen_music_not_found);
			return false;
		}
		return super.isValid(context);
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		int timePerRectangle = Integer.valueOf(listOfSettings
				.get(PREF_TIME_PER_RECTANGLE)) / 1000;
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_rectangle_test_general_time_per_rectangle),
						timePerRectangle + context.getString(R.string.unit_sec),
						context));
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_rectangle_test_general_play_tone),
						listOfSettings.get(PREF_PLAY_TONE), context));

		int pauseBetweenRectangles = Integer.valueOf(listOfSettings
				.get(PREF_PAUSE_BETWEEN_RECTANGLES)) / 1000;
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_rectangle_test_general_pause_between_rectangles),
						pauseBetweenRectangles
								+ context.getString(R.string.unit_sec), context));

		boolean playMusic = Boolean
				.valueOf(listOfSettings.get(PREF_PLAY_MUSIC));
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_rectangle_test_general_play_music),
						listOfSettings.get(PREF_PLAY_MUSIC), context));
		if (playMusic) {
			String rawString = listOfSettings.get(PREF_MUSIC_FILE_PATH);
			String fileName = rawString.substring(
					rawString.lastIndexOf("/") + 1, rawString.length());
			listOfTableRows
					.add(computeTableRow(
							context.getString(R.string.fragment_testmanagement_detail_tv_rectangle_test_general_music_file),
							fileName, context));
		}
		return listOfTableRows;
	}
}