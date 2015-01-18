package de.vion.eyetracking.cameracalib.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * The single fragment for each tutorial part
 * 
 * @author André Pomp
 */
public class TutorialFragment extends Fragment {

	private static final String ARGS_LAYOUT = "ARGS_LAYOUT";

	public static TutorialFragment getInstance(int resID) {
		TutorialFragment instance = new TutorialFragment();

		Bundle args = new Bundle();
		args.putInt(ARGS_LAYOUT, resID);
		instance.setArguments(args);

		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Get the database ID from the arguments
		Bundle args = getArguments();
		int resID = args.getInt(ARGS_LAYOUT);

		View rootView = inflater.inflate(resID, container, false);
		return rootView;
	}
}
