package de.vion.eyetracking.testframework.tests.video;

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
 * The settings for the video test
 * 
 * @author André Pomp
 * 
 */
public class VideoTestSettings extends GenericTestSettings {

	public static final String PREF_VIDEO_FILE_PATH = "pref_video_test_general_file_path";

	public static final String PREF_VIDEO_PLAY_LOOP = "pref_video_test_general_loop_video";

	public VideoTestSettings() {
		super(TestType.VIDEO_TEST);
	}

	public String getVideoFilePath(Context context) {
		return getSharedPreferences(context)
				.getString(PREF_VIDEO_FILE_PATH, "");
	}

	public boolean isLooping(Context context) {
		return getSharedPreferences(context).getBoolean(PREF_VIDEO_PLAY_LOOP,
				false);
	}

	@Override
	public boolean isValid(Context context) {
		File file = new File(getVideoFilePath(context));
		if (!file.exists()) {
			Toaster.makeToast(
					context,
					R.string.preferences_video_test_toast_error_chosen_video_not_found);
			return false;
		}
		return super.isValid(context);
	}

	@Override
	public List<TableRow> getTableRows(Map<String, String> listOfSettings,
			Context context) {

		List<TableRow> listOfTableRows = super.getTableRows(listOfSettings,
				context);

		String rawString = listOfSettings.get(PREF_VIDEO_FILE_PATH);
		String fileName = rawString.substring(rawString.lastIndexOf("/") + 1,
				rawString.length());
		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_video_test_file),
						fileName, context));

		listOfTableRows
				.add(computeTableRow(
						context.getString(R.string.fragment_testmanagement_detail_tv_video_test_loop_video),
						listOfSettings.get(PREF_VIDEO_PLAY_LOOP), context));

		return listOfTableRows;
	}
}