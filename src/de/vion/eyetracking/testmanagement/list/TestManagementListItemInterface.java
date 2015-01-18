package de.vion.eyetracking.testmanagement.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * The Interface that must be implemented by all list items
 * 
 * @author André Pomp
 * 
 */
public interface TestManagementListItemInterface {

	public int getViewType();

	public View getView(int position, Context context, LayoutInflater inflater,
			View convertView, ViewGroup parent);
}
