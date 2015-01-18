package de.vion.eyetracking.misc;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 
 * Helper class for handling device specific parameters
 * 
 * @author André Pomp
 * 
 */
public class Device {

	public static String getDeviceModel() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return model;
		}
		return manufacturer + " " + model;
	}

	// http://stackoverflow.com/questions/2193457/is-there-a-way-to-determine-android-physical-screen-height-in-cm-or-inches
	public static int[] getDeviceParameters(Context context) {
		// Get the current Display metrics
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		display.getMetrics(displayMetrics);

		// Raw width/height on < 14 devices. >= 14 the status bar is missing!
		int widthPixels = displayMetrics.widthPixels;
		int heightPixels = displayMetrics.heightPixels;

		try {
			// includes window decorations (statusbar bar/menu bar) on >= 14
			if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
				widthPixels = (Integer) Display.class.getMethod("getRawWidth")
						.invoke(display);
				heightPixels = (Integer) Display.class
						.getMethod("getRawHeight").invoke(display);
			} else if (Build.VERSION.SDK_INT >= 17) {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(
						display, realSize);
				widthPixels = realSize.x;
				heightPixels = realSize.y;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Calculate the physical screen size and store
		int physicalWidth = convertPixelToMM(widthPixels, displayMetrics.xdpi);
		int physicalHeight = convertPixelToMM(heightPixels, displayMetrics.ydpi);

		int[] result = new int[4];
		result[0] = heightPixels;
		result[1] = widthPixels;
		result[2] = physicalHeight;
		result[3] = physicalWidth;
		return result;
	}

	// http://www.din-formate.de/pixelrechner-bildaufloesung-druckmasse-mm-cm-dpi-ppi-pixelkalkulator-fotogroesse-berechnen-fotos-bilder-poster-drucken.html
	private static int convertPixelToMM(float pixel, float dpi) {
		float result = (pixel / dpi) * 25.4f;
		return (int) result;
	}
}