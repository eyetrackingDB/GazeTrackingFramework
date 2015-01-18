package de.vion.eyetracking.misc;

import java.util.ArrayList;

import android.net.Uri;

/**
 * 
 * The callback that is called when the zip task is finished
 * 
 * @author André Pomp
 * 
 */
public interface ZipTaskFinishedCallback {

	public void onTaskFinished(ArrayList<Uri> uris);
}
