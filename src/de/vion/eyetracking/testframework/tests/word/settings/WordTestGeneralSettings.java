package de.vion.eyetracking.testframework.tests.word.settings;

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
 * The Settings that important for both word tests
 * 
 * @author André Pomp
 * 
 */
public abstract class WordTestGeneralSettings extends GenericTestSettings {

	public static final String PREF_TIME_PER_WORD = "pref_word_test_general_time_per_word";
	public static final String PREF_TEXT = "pref_word_test_general_text";

	public WordTestGeneralSettings(TestType type) {
		super(type);
	}

	public int getTimePerWord(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_TIME_PER_WORD,
								context.getString(R.string.test_word_pref_time_per_word_default)));
	}

	public String getTextFilePath(Context context) {
		return getSharedPreferences(context).getString(PREF_TEXT, "");
	}

	@Override
	public boolean isValid(Context context) {
		File file = new File(getTextFilePath(context));
		if (!file.exists()) {
			Toaster.makeToast(
					context,
					R.string.preferences_word_test_toast_error_chosen_text_not_found);
			return false;
		}
		return super.isValid(context);
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		String rawString = listOfSettings.get(PREF_TEXT);
		String fileName = rawString.substring(rawString.lastIndexOf("/") + 1,
				rawString.length());
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_word_test_general_text_file),
						fileName, context));

		int timePerWord = Integer.valueOf(listOfSettings
				.get(PREF_TIME_PER_WORD)) / 1000;
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_word_test_general_time_per_word),
						timePerWord + " "
								+ context.getString(R.string.unit_sec), context));

		return listOfTableRows;
	}
}
