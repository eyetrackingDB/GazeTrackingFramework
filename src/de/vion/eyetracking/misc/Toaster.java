package de.vion.eyetracking.misc;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Helper class showing toasts
 * 
 * @author André Pomp
 * 
 */
public class Toaster {

	private Toaster() {
		// no instance
	}

	/**
	 * makes a Toast with centered text and the duration Toast.LENGTH_SHORT
	 * 
	 * @param context
	 * @param text
	 */
	public static void makeToast(final Context context, final CharSequence text) {
		makeToast(context, text, Toast.LENGTH_SHORT);
	}

	public static void makeToast(final Context context, int textResID) {
		makeToast(context, textResID, Toast.LENGTH_SHORT);
	}

	/**
	 * makes a Toast with centered text
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 *            Toast.LENGTH_LONG / Toast.LENGTH_SHORT
	 */
	public static void makeToast(final Context context,
			final CharSequence text, final int duration) {
		// create toast
		Toast toast = Toast.makeText(context, text, duration);
		// center text
		((TextView) ((LinearLayout) toast.getView())
				.findViewById(android.R.id.message))
				.setGravity(Gravity.CENTER_HORIZONTAL);

		// show it
		toast.show();
	}

	/**
	 * makes a Toast with centered text
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 *            Toast.LENGTH_LONG / Toast.LENGTH_SHORT
	 */
	public static void makeToast(final Context context, int textResID,
			final int duration) {
		// create toast
		Toast toast = Toast.makeText(context, context.getString(textResID),
				duration);
		// center text
		((TextView) ((LinearLayout) toast.getView())
				.findViewById(android.R.id.message))
				.setGravity(Gravity.CENTER_HORIZONTAL);

		// show it
		toast.show();
	}

}
