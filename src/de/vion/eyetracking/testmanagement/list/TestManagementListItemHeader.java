package de.vion.eyetracking.testmanagement.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testmanagement.list.TestManagementListItemAdapter.RowType;

/**
 * 
 * The Header item in the list
 * 
 * @author André Pomp
 * 
 */
public class TestManagementListItemHeader implements
		TestManagementListItemInterface {

	private TestType type;

	public TestManagementListItemHeader(TestType type) {
		super();
		this.type = type;
	}

	@Override
	public int getViewType() {
		return RowType.HEADER_ITEM.ordinal();
	}

	@Override
	public View getView(int position, Context context, LayoutInflater inflater,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(
					R.layout.list_item_fragment_testmanagement_header, null);
		}
		view.setClickable(false);

		TextView text = (TextView) view
				.findViewById(R.id.list_item_fragment_testmanagement_header_tv_title);
		text.setText(context.getString(this.type.getNameResource()));

		return view;
	}
}