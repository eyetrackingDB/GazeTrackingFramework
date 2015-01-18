package de.vion.eyetracking.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.vion.eyetracking.R;
import de.vion.eyetracking.TestSettingsActivity;
import de.vion.eyetracking.testframework.TestType;

/**
 * An adapter class for managing the main menu elements in the corresponding
 * list view
 * 
 * @author André Pomp
 * 
 */
public class HomeAdapter extends ArrayAdapter<TestType> {

	/**
	 * The list of main menu items
	 */
	private List<TestType> menuItems = new ArrayList<TestType>();

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
	public HomeAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
		this.resourceID = textViewResourceId;
		this.menuItems.addAll(Arrays.asList(TestType.values()));
	}

	@Override
	public int getCount() {
		return this.menuItems.size();
	}

	@Override
	public TestType getItem(int index) {
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
				.findViewById(R.id.home_listitem_tv_name);
		tvMenuItem.setText(this.context.getString(this.menuItems.get(position)
				.getNameResource()));

		// Get the reference to the ImageView for the Settings
		ImageView ivSettings = (ImageView) row
				.findViewById(R.id.home_listitem_iv_settings);
		ivSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeAdapter.this.context,
						TestSettingsActivity.class);
				intent.putExtra(TestSettingsActivity.INTENT_TESTTYPE,
						HomeAdapter.this.menuItems.get(position));
				HomeAdapter.this.context.startActivity(intent);
			}
		});
		return row;
	}
}