package de.vion.eyetracking.testframework.tests.line.settings;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.TableRow;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;

/**
 * The Settings for the line test
 * 
 * @author André Pomp
 * 
 */
public class LineTestGeneralSettings extends GenericTestSettings {

	public static final String PREF_VELOCITY = "pref_line_test_general_velocity";
	public static final String PREF_NUMBER_OF_LINES = "pref_line_test_general_number_of_lines";
	public static final String PREF_NUMBER_OF_REPETITIONS = "pref_line_test_number_of_repetitions";

	public LineTestGeneralSettings() {
		super(TestType.LINE_TEST);
	}

	public int getVelocity(Context context) {
		return Integer.valueOf(getSharedPreferences(context).getString(
				PREF_VELOCITY,
				context.getString(R.string.test_line_pref_velocity_default)));
	}

	public int getNumberOfLines(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_NUMBER_OF_LINES,
								context.getString(R.string.test_line_pref_number_of_lines_default)));
	}

	public int getNumberOfRepetitions(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_NUMBER_OF_REPETITIONS,
								context.getString(R.string.test_line_pref_number_of_repetitions_default)));
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		String velocity = listOfSettings.get(PREF_VELOCITY);
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_line_test_general_velocity),
						velocity, context));

		String numberOfLines = listOfSettings.get(PREF_NUMBER_OF_LINES);
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_line_test_general_number_of_lines),
						numberOfLines, context));

		String numberOfRepetitions = listOfSettings
				.get(PREF_NUMBER_OF_REPETITIONS);
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_line_test_number_of_repetitions),
						numberOfRepetitions, context));

		return listOfTableRows;
	}
}