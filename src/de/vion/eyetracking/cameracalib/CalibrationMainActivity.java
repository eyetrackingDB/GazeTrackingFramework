package de.vion.eyetracking.cameracalib;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.vion.eyetracking.R;
import de.vion.eyetracking.cameracalib.adapter.CalibrationMainAdapter;
import de.vion.eyetracking.cameracalib.calibration.CalibrationActivity;
import de.vion.eyetracking.cameracalib.tutorial.TutorialActivity;
import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.settings.GazeTrackingPreferences;

/**
 * The main activity for calibrating the camera
 * 
 * @author André Pomp
 * 
 */
public class CalibrationMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(GazeTrackingPreferences
				.getDeviceOrientationAsActivityInfo(this));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.calibration_main_activity);

		// Initialie the adapter menu
		CalibrationMainAdapter adapter = new CalibrationMainAdapter(this,
				R.layout.calibration_main_list_item);
		ListView listview = (ListView) findViewById(R.id.activity_calibration_main_listview);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch ((Integer) (arg0.getAdapter().getItem(arg2))) {
				case R.string.calibration_main_activity_btn_start_stop:
					startActivity(new Intent(CalibrationMainActivity.this,
							CalibrationActivity.class));
					break;
				case R.string.calibration_main_activity_btn_read_tutorial:
					startActivity(new Intent(CalibrationMainActivity.this,
							TutorialActivity.class));
					break;
				case R.string.calibration_main_activity_btn_print_pattern:
					sendPrintIntent();
					break;
				case R.string.calibration_main_activity_btn_store_pattern:
					sendShareIntent();
					break;
				}
			}
		});
		// Set the ListView adapter
		listview.setAdapter(adapter);

		if (GazeTrackingPreferences.startTutorial(CalibrationMainActivity.this)
				&& savedInstanceState == null) {
			startActivity(new Intent(CalibrationMainActivity.this,
					TutorialActivity.class));
		}
	}

	/**
	 * Share the pattern via CloudStorage
	 */
	private void sendShareIntent() {
		File file = FileManager.copyAssetFileToSdcard(this,
				FileManager.getCalibrationFilePath(),
				FileManager.getCalibrationFileName());

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("image/png");
		Uri uri = Uri.fromFile(file);
		sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
		sharingIntent
				.putExtra(
						Intent.EXTRA_TEXT,
						getString(R.string.calibration_main_activity_intent_sharing_text));
		startActivity(Intent
				.createChooser(
						sharingIntent,
						getString(R.string.calibration_main_activity_intent_sharing_title)));
	}

	/**
	 * Print the pattern via CloudPrint
	 */
	private void sendPrintIntent() {
		File file = FileManager.copyAssetFileToSdcard(this,
				FileManager.getCalibrationFilePath(),
				FileManager.getCalibrationFileName());

		PrintHelper photoPrinter = new PrintHelper(this);
		photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		photoPrinter.printBitmap("calibration_pattern.png", bitmap);
	}
}