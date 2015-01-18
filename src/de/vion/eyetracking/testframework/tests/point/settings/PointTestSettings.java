package de.vion.eyetracking.testframework.tests.point.settings;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.TableRow;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;

/**
 * The Settings for the point test
 * 
 * @author André Pomp
 * 
 */
public class PointTestSettings extends GenericTestSettings {

	public static final String PREF_TIME_PER_POINT = "pref_point_test_general_time_per_point";
	public static final String PREF_NUMBER_OF_ROWS = "pref_point_test_number_of_rows";
	public static final String PREF_NUMBER_OF_COLMUNS = "pref_point_test_number_of_columns";
	public static final String PREF_NUMBER_OF_REPETITIONS = "pref_point_test_number_of_repetitions";

	public PointTestSettings() {
		super(TestType.POINT_TEST);
	}

	public int getTimePerPoint(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_TIME_PER_POINT,
								context.getString(R.string.test_point_pref_time_per_point_default)));
	}

	public int getNumberOfRows(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_NUMBER_OF_ROWS,
								context.getString(R.string.test_point_pref_number_of_rows_default)));
	}

	public int getNumberOfColumns(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_NUMBER_OF_COLMUNS,
								context.getString(R.string.test_point_pref_number_of_columns_default)));
	}

	public int getNumberOfRepetitions(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_NUMBER_OF_REPETITIONS,
								context.getString(R.string.test_point_pref_number_of_repetitions_default)));
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		int timePerRectangle = Integer.valueOf(listOfSettings
				.get(PREF_TIME_PER_POINT)) / 1000;
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_point_test_general_time_per_point),
						timePerRectangle + context.getString(R.string.unit_sec),
						context));

		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_point_test_number_of_rows),
						listOfSettings.get(PREF_NUMBER_OF_ROWS), context));

		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_point_test_number_of_columns),
						listOfSettings.get(PREF_NUMBER_OF_COLMUNS), context));

		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_point_test_number_of_repetitions),
						listOfSettings.get(PREF_NUMBER_OF_REPETITIONS), context));

		return listOfTableRows;
	}
}