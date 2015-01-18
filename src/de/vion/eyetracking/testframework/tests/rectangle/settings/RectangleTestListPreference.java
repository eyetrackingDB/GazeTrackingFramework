package de.vion.eyetracking.testframework.tests.rectangle.settings;

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
 * The rectangle list preference item for the available music
 * 
 * @author André Pomp
 * 
 */
public class RectangleTestListPreference extends ListPreference {

	public RectangleTestListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RectangleTestListPreference(Context context) {
		super(context);
	}

	@Override
	protected View onCreateDialogView() {
		ListView view = new ListView(getContext());
		view.setAdapter(adapter());

		// Get the test directory and check if it exists
		String testDirectory = FileManager.getTestMusicDir();
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
									R.string.preferences_rectangle_test_toast_error_no_music_found)
							+ file.getAbsolutePath());
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