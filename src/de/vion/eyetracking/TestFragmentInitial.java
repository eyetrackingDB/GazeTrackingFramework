package de.vion.eyetracking;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import de.vion.eyetracking.adapter.TestAdapter;
import de.vion.eyetracking.callback.TestFragmentInitialCallback;

/**
 * 
 * The Fragment that is shown before we directly start the test. It shows the
 * face alignment, allows to play a demo or start the test.
 * 
 * @author André Pomp
 * 
 */
public class TestFragmentInitial extends Fragment {

	private static final String BUNDLE_ARGS_ABBREVIATION = "BUNDLE_ARGS_ABBREVIATION";

	public static TestFragmentInitial getInstance(String abbreviation) {
		TestFragmentInitial fragment = new TestFragmentInitial();
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_ARGS_ABBREVIATION, abbreviation);
		fragment.setArguments(bundle);

		return fragment;
	}

	private TestFragmentInitialCallback callback;
	private String testSubjectAbb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		this.testSubjectAbb = args.getString(BUNDLE_ARGS_ABBREVIATION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.test_fragment_initial,
				container, false);

		TestAdapter adapter = new TestAdapter(getActivity(),
				R.layout.test_list_item, !isAbbrevValid());
		ListView view = (ListView) rootView
				.findViewById(R.id.test_fragment_not_running_lv);
		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				switch ((Integer) arg0.getAdapter().getItem(arg2)) {
				case R.string.test_activity_listitem_test_start:
					TestFragmentInitial.this.callback.onStartClicked();
					break;
				case R.string.test_activity_listitem_test_show_demo:
					TestFragmentInitial.this.callback.onShowDemoClicked();
					break;
				case R.string.test_activity_listitem_test_camera_alignment:
					TestFragmentInitial.this.callback.onAlignFaceClicked();
					break;
				}
			}
		});
		view.setAdapter(adapter);

		TextView textView = (TextView) rootView
				.findViewById(R.id.test_fragment_not_running_tv);

		if (isAbbrevValid()) {
			textView.setVisibility(View.GONE);
		}

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.callback = (TestFragmentInitialCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TestFragmentNotRunningCallback");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final View decorView = getActivity().getWindow().getDecorView();
		decorView.setSystemUiVisibility(0);
	}

	private boolean isAbbrevValid() {
		if (this.testSubjectAbb == null || this.testSubjectAbb.isEmpty()) {
			return false;
		}
		return true;
	}
}