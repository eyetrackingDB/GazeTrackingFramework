package de.vion.eyetracking.testmanagement.list;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * 
 * The adapter for the list of conducted experiments
 * 
 * @author André Pomp
 * 
 */
public class TestManagementListItemAdapter extends
		ArrayAdapter<TestManagementListItemInterface> {

	public enum RowType {
		LIST_ITEM, HEADER_ITEM
	}

	private List<TestManagementListItemInterface> menuItems = new ArrayList<TestManagementListItemInterface>();

	private Context context;

	public TestManagementListItemAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;
	}

	@Override
	public int getCount() {
		return this.menuItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getViewType();
	}

	@Override
	public TestManagementListItemInterface getItem(int index) {
		return this.menuItems.get(index);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(this.context);
		return getItem(position).getView(position, this.context, inflater,
				convertView, parent);
	}

	public void addMenuItem(TestManagementListItemInterface menuItem) {
		this.menuItems.add(menuItem);
	}

	public void addMenuItems(List<TestManagementListItemInterface> allItems) {
		this.menuItems.addAll(allItems);
	}

	public void clearMenuItems() {
		this.menuItems.clear();
	}
}