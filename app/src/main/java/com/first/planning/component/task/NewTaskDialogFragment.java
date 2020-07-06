package com.first.planning.component.task;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.first.planning.R;

public class NewTaskDialogFragment extends DialogFragment {

    private TaskListAdapter taskListAdapter;

    public NewTaskDialogFragment(TaskListAdapter taskListAdapter) {
        this.taskListAdapter = taskListAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View newTaskView = requireActivity().getLayoutInflater().inflate(R.layout.add_new_task_fragment, null);
        builder.setView(newTaskView);
        builder.setPositiveButton(R.string.create, (dialog, id) -> {
            String taskName = ((EditText) newTaskView.findViewById(R.id.editText)).getText().toString();
            taskListAdapter.add(taskName);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            this.dismiss();
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
