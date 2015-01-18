package de.vion.eyetracking.testframework.tests.rectangle.settings;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.TableRow;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;

/**
 * The Settings for the rectangle sequence test
 * 
 * @author André Pomp
 * 
 */
public class RectangleTestSequenceSettings extends RectangleTestGeneralSettings {

	public static final String PREF_TIME_PER_SEQUENCE = "pref_rectangle_test_sequence_time_per_sequence";

	public RectangleTestSequenceSettings() {
		super(TestType.RECTANGLE_SEQUENCE_TEST);
	}

	public int getTimePerSequence(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_TIME_PER_SEQUENCE,
								context.getString(R.string.test_rectangle_sequence_pref_time_per_sequence_default)));
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		int timePerSequence = Integer.valueOf(listOfSettings
				.get(PREF_TIME_PER_SEQUENCE)) / 1000;
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_rectangle_test_sequence_time_per_sequence),
						timePerSequence + context.getString(R.string.unit_sec),
						context));

		return listOfTableRows;
	}
}