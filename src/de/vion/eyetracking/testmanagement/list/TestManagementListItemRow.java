package de.vion.eyetracking.testmanagement.list;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import de.vion.eyetracking.R;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testmanagement.list.TestManagementListItemAdapter.RowType;

/**
 * 
 * The Row item that represents a conducted test
 * 
 * @author André Pomp
 * 
 */
public class TestManagementListItemRow implements
		TestManagementListItemInterface {

	private TestType type;
	private String directory;
	private String date;
	private String time;
	private String abbreviation;

	public TestManagementListItemRow(TestType type, String directory) {
		super();
		this.type = type;
		this.directory = directory;
		parseDirectoryName();
	}

	// form: yyyyMMdd_HHmmss_abbreviation
	@SuppressLint("SimpleDateFormat")
	private void parseDirectoryName() {
		String fileName = new File(this.directory).getName();
		this.abbreviation = fileName.substring(fileName.lastIndexOf("_") + 1);
		String dateString = fileName.substring(0, fileName.lastIndexOf("_"));
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		try {
			// Parse the string to a date
			Date currentDate = df.parse(dateString);

			// Parse the date to a date string
			DateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy");
			this.date = dfDate.format(currentDate);

			// Parse the date to atime
			DateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
			this.time = dfTime.format(currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	@Override
	public View getView(int position, Context context, LayoutInflater inflater,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(
					R.layout.list_item_fragment_testmanagement_row, null);
		}

		// Label textviews
		TextView tvAbbreviationLabel = (TextView) view
				.findViewById(R.id.list_item_fragment_testmanagement_row_tv_abbreviation_label);
		TextView tvDateLabel = (TextView) view
				.findViewById(R.id.list_item_fragment_testmanagement_row_tv_date_label);
		TextView tvTimeLabel = (TextView) view
				.findViewById(R.id.list_item_fragment_testmanagement_row_tv_time_label);

		TextView tvAbbreviation = (TextView) view
				.findViewById(R.id.list_item_fragment_testmanagement_row_tv_abbreviation);
		tvAbbreviation.setText(this.abbreviation);

		TextView tvDate = (TextView) view
				.findViewById(R.id.list_item_fragment_testmanagement_row_tv_date);
		tvDate.setText(this.date);

		TextView tvTime = (TextView) view
				.findViewById(R.id.list_item_fragment_testmanagement_row_tv_time);
		tvTime.setText(this.time);

		// Get the listview
		ListView lv = (ListView) parent;
		SparseBooleanArray checked = lv.getCheckedItemPositions();

		if (checked.get(position)) {
			view.setBackgroundColor(context.getResources().getColor(
					android.R.color.holo_blue_bright));
			tvAbbreviation.setTextColor(context.getResources().getColor(
					android.R.color.background_dark));
			tvDate.setTextColor(context.getResources().getColor(
					android.R.color.background_dark));
			tvTime.setTextColor(context.getResources().getColor(
					android.R.color.background_dark));
			tvAbbreviationLabel.setTextColor(context.getResources().getColor(
					android.R.color.background_dark));
			tvDateLabel.setTextColor(context.getResources().getColor(
					android.R.color.background_dark));
			tvTimeLabel.setTextColor(context.getResources().getColor(
					android.R.color.background_dark));
		} else {
			view.setBackgroundColor(context.getResources().getColor(
					android.R.color.background_dark));
			tvAbbreviation.setTextColor(context.getResources().getColor(
					android.R.color.white));
			tvDate.setTextColor(context.getResources().getColor(
					android.R.color.white));
			tvTime.setTextColor(context.getResources().getColor(
					android.R.color.white));
			tvAbbreviationLabel.setTextColor(context.getResources().getColor(
					android.R.color.white));
			tvDateLabel.setTextColor(context.getResources().getColor(
					android.R.color.white));
			tvTimeLabel.setTextColor(context.getResources().getColor(
					android.R.color.white));
		}

		return view;
	}

	public TestType getType() {
		return this.type;
	}

	public String getDirectory() {
		return this.directory;
	}
}