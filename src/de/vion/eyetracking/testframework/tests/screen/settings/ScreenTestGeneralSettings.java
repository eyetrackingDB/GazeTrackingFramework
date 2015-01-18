package de.vion.eyetracking.testframework.tests.screen.settings;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.TableRow;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;

/**
 * The Settings for the second screen test
 * 
 * @author André Pomp
 * 
 */
public class ScreenTestGeneralSettings extends GenericTestSettings {

	public static final String PREF_TIME_GAZING_ON_TABLET = "pref_screen_test_time_gazing_on_tablet";
	public static final String PREF_TIME_GAZING_BESIDE_TABLET = "pref_screen_test_time_gazing_beside_tablet";
	public static final String PREF_NUMBER_OF_REPETITIONS = "pref_screen_test_number_of_repetitions";

	public ScreenTestGeneralSettings() {
		super(TestType.SCREEN_TEST);
	}

	public int getTimeOnTablet(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_TIME_GAZING_ON_TABLET,
								context.getString(R.string.test_screen_pref_time_gazing_on_tablet_default)));
	}

	public int getTimeBesideTablet(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_TIME_GAZING_BESIDE_TABLET,
								context.getString(R.string.test_screen_pref_time_gazing_beside_tablet_default)));
	}

	public int getNumberOfRepetitions(Context context) {
		return Integer
				.valueOf(getSharedPreferences(context)
						.getString(
								PREF_NUMBER_OF_REPETITIONS,
								context.getString(R.string.test_screen_pref_number_of_repetitions_default)));
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		int timeOnTablet = Integer.valueOf(listOfSettings
				.get(PREF_TIME_GAZING_ON_TABLET)) / 1000;
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_screen_test_gazing_time_on_tablet),
						timeOnTablet + " "
								+ context.getString(R.string.unit_sec), context));

		int timeBesideTablet = Integer.valueOf(listOfSettings
				.get(PREF_TIME_GAZING_BESIDE_TABLET)) / 1000;
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_screen_test_gazing_time_beside_tablet),
						timeBesideTablet + " "
								+ context.getString(R.string.unit_sec), context));

		String numberOfRepetitions = listOfSettings
				.get(PREF_NUMBER_OF_REPETITIONS);
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_screen_test_number_of_repetitions),
						numberOfRepetitions, context));

		return listOfTableRows;
	}
}