package de.vion.eyetracking.testframework.tests.video;

import java.io.File;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.vion.eyetracking.R;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.misc.Toaster;

/**
 * The preference for the list of available videos
 * 
 * @author André Pomp
 * 
 */
public class VideoTestListPreference extends ListPreference {

	public VideoTestListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoTestListPreference(Context context) {
		super(context);
	}

	@Override
	protected View onCreateDialogView() {
		ListView view = new ListView(getContext());
		view.setAdapter(adapter());

		// Get the test directory and check if it exists
		String testDirectory = FileManager.getTestVideoDir();
		File file = new File(testDirectory);

		// Load the files from the directory
		File[] allFiles = file.listFiles();
		CharSequence[] entries = new String[allFiles.length];
		CharSequence[] values = new String[allFiles.length];

		// Show an error if no videos are available
		if (allFiles.length == 0) {
			Toaster.makeToast(
					getContext(),
					getContext()
							.getString(
									R.string.preferences_video_test_toast_error_no_videos_found)
							+ " " + file.getAbsolutePath());
		} else {
			for (int i = 0; i < allFiles.length; i++) {
				entries[i] = allFiles[i].getName();
				values[i] = allFiles[i].getAbsolutePath();
			}
		}

		setEntries(entries);
		setEntryValues(values);
		return view;
	}

	private ListAdapter adapter() {
		return new ArrayAdapter<String>(getContext(),
				android.R.layout.select_dialog_singlechoice);
	}
}