package de.vion.eyetracking.cameracalib.adapter;

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
 * An adapter class for managing the main menu elements in the corresponding
 * list view
 * 
 * @author André Pomp
 * 
 */
public class CalibrationMainAdapter extends ArrayAdapter<Integer> {

	/**
	 * The list of main menu items
	 */
	private List<Integer> menuItems = new ArrayList<Integer>();

	/**
	 * Textview for displaying the current menu item
	 */
	private TextView tvMenuItem;

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
	public CalibrationMainAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
		this.resourceID = textViewResourceId;
		this.menuItems.add(R.string.calibration_main_activity_btn_start_stop);
		this.menuItems
				.add(R.string.calibration_main_activity_btn_read_tutorial);
		this.menuItems
				.add(R.string.calibration_main_activity_btn_print_pattern);
		this.menuItems
				.add(R.string.calibration_main_activity_btn_store_pattern);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			// ROW INFLATION
			LayoutInflater inflater = LayoutInflater.from(this.context);
			row = inflater.inflate(this.resourceID, parent, false);
		}

		// Get reference to TextView - country_abbrev
		this.tvMenuItem = (TextView) row
				.findViewById(R.id.calibration_main_listitem_tv_name);
		this.tvMenuItem.setText(this.context.getResources().getString(
				getItem(position)));
		switch (getItem(position)) {
		case R.string.calibration_main_activity_btn_start_stop:
			this.tvMenuItem.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_action_play, 0, 0, 0);
			break;
		case R.string.calibration_main_activity_btn_read_tutorial:
			this.tvMenuItem.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_action_read, 0, 0, 0);
			break;
		case R.string.calibration_main_activity_btn_print_pattern:
			this.tvMenuItem.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_action_print, 0, 0, 0);
			break;
		case R.string.calibration_main_activity_btn_store_pattern:
			this.tvMenuItem.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_action_share, 0, 0, 0);
			break;
		}
		return row;
	}
}