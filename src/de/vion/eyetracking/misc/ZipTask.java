package de.vion.eyetracking.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import de.vion.eyetracking.R;

/**
 * 
 * The Task for zipping a number of files
 * 
 * @author André Pomp
 * 
 */
public class ZipTask extends AsyncTask<Void, Integer, Void> {

	private ArrayList<Uri> uris = new ArrayList<Uri>();
	private ProgressDialog dialog;
	private List<File> listOfInputFiles;
	private ZipTaskFinishedCallback callback;
	private Context context;

	public ZipTask(Context context, List<File> listOfInputFiles,
			ZipTaskFinishedCallback callback) {
		super();
		this.listOfInputFiles = listOfInputFiles;
		this.callback = callback;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.dialog = new ProgressDialog(this.context);
		this.dialog.setTitle(this.context
				.getString(R.string.asynctask_zipping_dialog_title));
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		int counter = 1;
		for (File inputFile : this.listOfInputFiles) {
			publishProgress(counter);
			counter++;

			File outputFile = new File(this.context.getExternalCacheDir(),
					inputFile.getName() + ".zip");

			if (ZipManager.compareZipAndFolder(outputFile.getAbsolutePath(),
					inputFile.getAbsolutePath())) {
				// if the zip file and the folder are the same we do not have to
				// zip again
				continue;
			}

			ZipManager.zipFileAtPath(inputFile.getAbsolutePath(),
					outputFile.getAbsolutePath());
			this.uris.add(Uri.fromFile(outputFile));
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		for (Integer value : values) {
			this.dialog.setMessage(this.context
					.getString(R.string.asynctask_zipping_dialog_message)
					+ " "
					+ value + "/" + this.listOfInputFiles.size());
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		try {
			this.dialog.dismiss();
			this.dialog = null;
		} catch (Exception e) {
			// nothing
		}
		this.callback.onTaskFinished(this.uris);
	}
}