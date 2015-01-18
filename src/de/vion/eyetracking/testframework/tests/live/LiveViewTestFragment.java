package de.vion.eyetracking.testframework.tests.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testframework.generic.GenericTestFragment;

/**
 * The Fragment for the live view test
 * 
 * @author André Pomp
 * 
 */
public class LiveViewTestFragment extends GenericTestFragment {

	public static LiveViewTestFragment createInstance(TestType type,
			String abbreviation, String subdir, boolean demo) {
		LiveViewTestFragment fragment = new LiveViewTestFragment();
		fragment.setArguments(createArgs(type, abbreviation, subdir, demo));
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.test_fragment_liveview,
				container, false);
		return rootView;
	}
}
