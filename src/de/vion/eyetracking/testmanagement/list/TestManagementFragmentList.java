package de.vion.eyetracking.testmanagement.list;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.vion.eyetracking.R;
import de.vion.eyetracking.gazedetection.post.PostProcessingService;
import de.vion.eyetracking.misc.Toaster;
import de.vion.eyetracking.misc.ZipTask;
import de.vion.eyetracking.misc.ZipTaskFinishedCallback;
import de.vion.eyetracking.testframework.TestType;
import de.vion.eyetracking.testmanagement.TestManagementStorage;
import de.vion.eyetracking.testmanagement.list.TestManagementListItemAdapter.RowType;

/**
 * 
 * The fragment that shows all conducted experiments
 * 
 * @author André Pomp
 * 
 */
public class TestManagementFragmentList extends Fragment implements
		ZipTaskFinishedCallback {

	private TestManagementListItemAdapter adapter;
	private ListView listView;
	private TestManagementListItemCallback callback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(
				R.layout.test_management_fragment_list, container, false);
		this.listView = (ListView) rootView
				.findViewById(R.id.test_management_fragment_list_lv);
		this.adapter = new TestManagementListItemAdapter(getActivity(),
				R.layout.list_item_fragment_testmanagement_row);
		this.listView.setAdapter(this.adapter);
		this.listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (TestManagementFragmentList.this.adapter
						.getItemViewType(arg2) == RowType.LIST_ITEM.ordinal()) {
					TestManagementListItemRow item = (TestManagementListItemRow) TestManagementFragmentList.this.adapter
							.getItem(arg2);
					TestManagementFragmentList.this.callback.onTestItemClicked(
							item.getType(), item.getDirectory());
				}
			}
		});
		// Set the choice mode
		this.listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		this.listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onCreateActionMode(final ActionMode mode,
					final Menu menu) {
				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.fragment_tm_contextual_menu, menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(final ActionMode mode,
					final Menu menu) {
				// do nothing
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(final ActionMode mode,
					final int position, final long id, final boolean checked) {

				// only list items can be checked!
				if ((TestManagementFragmentList.this.adapter
						.getItemViewType(position) != RowType.LIST_ITEM
						.ordinal())
						&& checked) {
					TestManagementFragmentList.this.listView.setItemChecked(
							position, false);
				}
				TestManagementFragmentList.this.adapter.notifyDataSetChanged();
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.action_delete:
					delete();
					mode.finish();
					break;
				case R.id.action_share:
					share();
					mode.finish();
					break;
				case R.id.action_select_all:
					selectAll();
					break;
				case R.id.action_postproc:
					postProcess();
					mode.finish();
					break;
				}
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// do nothing
			}
		});
		fillAdapter();
		return rootView;
	}

	private void postProcess() {
		// Check if the service is not already running => then the user has to
		// stop it first
		if (isMyServiceRunning(PostProcessingService.class)) {
			Toaster.makeToast(
					getActivity(),
					R.string.fragment_testmanagement_toast_postproc_already_running);
			return;
		}

		// Add all checked items to the directories list
		ArrayList<String> directories = new ArrayList<String>();
		SparseBooleanArray checkedItems = this.listView
				.getCheckedItemPositions();
		for (int i = 0; i < this.listView.getCount(); i++) {
			if (checkedItems.get(i)) {
				TestManagementListItemRow row = (TestManagementListItemRow) this.adapter
						.getItem(i);
				directories.add(row.getDirectory());
			}
		}

		// Create the fragment
		DialogFragment dialog = TestManagementDialogPostProc
				.getInstance(directories);
		dialog.show(getFragmentManager(), "DIALOG");
	}

	private void delete() {
		// Delete all the files
		SparseBooleanArray checkedItems = this.listView
				.getCheckedItemPositions();
		for (int i = 0; i < this.listView.getCount(); i++) {
			if (checkedItems.get(i)) {
				TestManagementListItemRow row = (TestManagementListItemRow) this.adapter
						.getItem(i);
				deleteRecursive(new File(row.getDirectory()));
			}
		}

		// Clear the adapter
		this.adapter.clearMenuItems();
		this.adapter.notifyDataSetChanged();

		// Reinit the local storage
		TestManagementStorage.initStorage();

		// Fill the adapter again
		fillAdapter();
	}

	/**
	 * Recursively deletes a folder and all its content
	 * 
	 * @param file
	 */
	private void deleteRecursive(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				deleteRecursive(child);
			}
		}
		file.delete();
	}

	private void share() {
		// Zip all the files
		SparseBooleanArray checkedItems = this.listView
				.getCheckedItemPositions();
		ArrayList<File> listOfInputFiles = new ArrayList<File>();
		for (int i = 0; i < this.listView.getCount(); i++) {
			if (checkedItems.get(i)) {
				TestManagementListItemRow row = (TestManagementListItemRow) this.adapter
						.getItem(i);
				listOfInputFiles.add(new File(row.getDirectory()));
			}
		}
		new ZipTask(getActivity(), listOfInputFiles,
				TestManagementFragmentList.this).execute();
	}

	private void selectAll() {
		for (int i = 0; i < this.listView.getCount(); i++) {
			if (this.adapter.getItemViewType(i) == RowType.LIST_ITEM.ordinal()) {
				this.listView.setItemChecked(i, true);
			} else {
				this.listView.setItemChecked(i, false);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.callback = (TestManagementListItemCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TestManagementListItemCallback");
		}
	}

	private void fillAdapter() {
		List<TestManagementListItemInterface> finalItems = new ArrayList<TestManagementListItemInterface>();
		Set<TestType> allTestTypes = TestManagementStorage.getAllTestTypes();
		for (TestType type : allTestTypes) {
			addItemsToList(finalItems,
					TestManagementStorage.getAllTestsForTestType(type), type);
		}
		this.adapter.addMenuItems(finalItems);
		this.adapter.notifyDataSetChanged();
	}

	protected void addItemsToList(
			List<TestManagementListItemInterface> finalItems,
			List<String> allItems, TestType testType) {
		finalItems.add(new TestManagementListItemHeader(testType));
		for (String directory : allItems) {
			finalItems.add(new TestManagementListItemRow(testType, directory));
		}
	}

	@Override
	public void onTaskFinished(ArrayList<Uri> uris) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		sendIntent.setType("application/zip");
		sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		startActivity(Intent.createChooser(sendIntent,
				getString(R.string.fragment_testmanagement_intent_share)));
	}

	// from:
	// http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-in-android
	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}