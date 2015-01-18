package de.vion.eyetracking.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.vion.eyetracking.R;

/**
 * An adapter class for managing the test menu elements in the corresponding
 * list view
 * 
 * @author André Pomp
 * 
 */
public class TestAdapter extends ArrayAdapter<Integer> {

	/**
	 * The list of main menu items
	 */
	private List<Integer> menuItems = new ArrayList<Integer>();

	/**
	 * Indicates if we are running in demo mode
	 */
	private boolean missingAbbreviation;

	/**
	 * The current context
	 */
	private Context context;

	/**
	 * The current used resource ID for the used list item
	 */
	private int resourceID;

	/**
	 * @param context
	 *            the current context
	 * @param textViewResourceId
	 *            resource ID for the used list item
	 */
	public TestAdapter(Context context, int textViewResourceId,
			boolean missingAbbreviation) {
		super(context, textViewResourceId);
		this.context = context;
		this.resourceID = textViewResourceId;
		this.missingAbbreviation = missingAbbreviation;
		this.menuItems.add(R.string.test_activity_listitem_test_start);
		this.menuItems.add(R.string.test_activity_listitem_test_show_demo);
		this.menuItems
				.add(R.string.test_activity_listitem_test_camera_alignment);
	}

	@Override
	public int getCount() {
		return this.menuItems.size();
	}

	@Override
	public Integer getItem(int index) {
		return this.menuItems.get(index);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			// ROW INFLATION
			LayoutInflater inflater = LayoutInflater.from(this.context);
			row = inflater.inflate(this.resourceID, parent, false);
		}

		// Get reference to TextView
		TextView tvMenuItem = (TextView) row
				.findViewById(R.id.test_listitem_tv_name);
		tvMenuItem.setText(this.context.getResources().getString(
				getItem(position)));
		switch (getItem(position)) {
		case R.string.test_activity_listitem_test_start:
			tvMenuItem.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_action_play, 0, 0, 0);
			if (this.missingAbbreviation) {
				tvMenuItem.setEnabled(false);
			}
			break;
		case R.string.test_activity_listitem_test_show_demo:
			tvMenuItem.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_action_video, 0, 0, 0);
			break;
		case R.string.test_activity_listitem_test_camera_alignment:
			tvMenuItem.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_action_face, 0, 0, 0);
			break;
		}

		return row;
	}
}