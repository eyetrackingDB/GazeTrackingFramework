package de.vion.eyetracking.testframework.tests.rectangle.settings;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.TableRow;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;

/**
 * The Settings for the rectangle single test
 * 
 * @author André Pomp
 * 
 */
public class RectangleTestSingleSettings extends RectangleTestGeneralSettings {

	public static final String PREF_NUMBER_OF_ROWS = "pref_rectangle_test_single_number_of_rows";
	public static final String PREF_NUMBER_OF_COLMUNS = "pref_rectangle_test_single_number_of_columns";

	public RectangleTestSingleSettings() {
		super(TestType.RECTANGLE_SINGLE_TEST);
	}

	public int getNumberOfRows(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_NUMBER_OF_ROWS,
								context.getString(R.string.test_rectangle_single_pref_number_of_rows_default)));
	}

	public int getNumberOfColumns(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_NUMBER_OF_COLMUNS,
								context.getString(R.string.test_rectangle_single_pref_number_of_columns_default)));
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_rectangle_test_single_number_of_rows),
						listOfSettings.get(PREF_NUMBER_OF_ROWS), context));

		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_rectangle_test_single_number_of_columns),
						listOfSettings.get(PREF_NUMBER_OF_COLMUNS), context));

		return listOfTableRows;
	}
}