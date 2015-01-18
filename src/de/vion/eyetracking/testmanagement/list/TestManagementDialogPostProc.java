package de.vion.eyetracking.testmanagement.list;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import de.vion.eyetracking.R;
import de.vion.eyetracking.gazedetection.post.PostProcessingService;
import de.vion.eyetracking.gazedetection.post.PostProcessingTask;

/**
 * 
 * The dialog that allows us to conduct the different post-processing steps
 * 
 * @author André Pomp
 * 
 */
public class TestManagementDialogPostProc extends DialogFragment {

	private static final String INTENT_DIRECTORIES = "INTENT_DIRECTORIES";

	private ArrayList<String> directories = null;

	public static DialogFragment getInstance(ArrayList<String> directories) {
		DialogFragment dialog = new TestManagementDialogPostProc();

		Bundle args = new Bundle();
		args.putStringArrayList(INTENT_DIRECTORIES, directories);
		dialog.setArguments(args);

		return dialog;
	}

	private CheckBox cbConvertFPS;
	private CheckBox cbProcessVideo;
	private CheckBox cbProcessScreen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.directories = getArguments()
				.getStringArrayList(INTENT_DIRECTORIES);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View rootView = inflater.inflate(
				R.layout.test_management_postproc_dialog, null);
		this.cbConvertFPS = (CheckBox) rootView
				.findViewById(R.id.testmanagement_fragment_dialog_postproc_cb_convertfps);
		this.cbConvertFPS
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							TestManagementDialogPostProc.this.cbProcessScreen
									.setEnabled(true);
						} else {
							TestManagementDialogPostProc.this.cbProcessScreen
									.setEnabled(false);
						}
					}
				});
		this.cbProcessVideo = (CheckBox) rootView
				.findViewById(R.id.testmanagement_fragment_dialog_postproc_cb_storegazepoints_video);
		this.cbProcessScreen = (CheckBox) rootView
				.findViewById(R.id.testmanagement_fragment_dialog_postproc_cb_storegazepoints_screen);

		builder.setView(rootView)
				.setTitle(
						R.string.fragment_testmanagement_dialog_postproc_title)

				.setPositiveButton(
						R.string.fragment_testmanagement_dialog_postproc_btn_start,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(getActivity(),
										PostProcessingService.class);

								ArrayList<PostProcessingTask> tasks = new ArrayList<PostProcessingTask>();
								for (String directory : TestManagementDialogPostProc.this.directories) {
									boolean processScreen = TestManagementDialogPostProc.this.cbConvertFPS
											.isChecked()
											&& TestManagementDialogPostProc.this.cbProcessVideo
													.isChecked();
									PostProcessingTask task = new PostProcessingTask(
											TestManagementDialogPostProc.this.cbConvertFPS
													.isChecked(),
											TestManagementDialogPostProc.this.cbProcessVideo
													.isChecked(),
											processScreen, directory);
									tasks.add(task);
								}
								intent.putExtra(
										PostProcessingService.INTENT_ARG_LIST_OF_TASKS,
										tasks);

								getActivity().startService(intent);
								getDialog().dismiss();
							}
						})
				.setNegativeButton(
						R.string.fragment_testmanagement_dialog_postproc_btn_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								getDialog().cancel();
							}
						});

		return builder.create();
	}
}