package de.vion.eyetracking.testframework.tests.word.settings;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.TableRow;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;

/**
 * The Settings that important for the word single test
 * 
 * @author André Pomp
 * 
 */
public class WordTestSingleSettings extends WordTestGeneralSettings {

	public static final String PREF_FONT_SIZE = "pref_word_test_single_font_size";

	public WordTestSingleSettings() {
		super(TestType.WORD_SINGLE_TEST);
	}

	public int getFontSize(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_FONT_SIZE,
								context.getString(R.string.test_word_single_pref_font_size_default)));
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		int fontSize = Integer.valueOf(listOfSettings.get(PREF_FONT_SIZE));
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_word_test_single_font_size),
						fontSize + context.getString(R.string.unit_sp), context));

		return listOfTableRows;
	}
}
