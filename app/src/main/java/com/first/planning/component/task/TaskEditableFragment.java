package com.first.planning.component.task;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.first.planning.R;
import com.first.planning.persistent.room.entity.TaskEntity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TaskEditableFragment extends DialogFragment {

    private TaskListAdapter taskListAdapter;
    private TaskEntity task;

    public TaskEditableFragment(TaskListAdapter taskListAdapter) {
        this.taskListAdapter = taskListAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        View newTaskView = requireActivity().getLayoutInflater().inflate(R.layout.add_new_task_fragment, null);
        builder.setView(newTaskView);
        if (!isNew()) {
            ((EditText) newTaskView.findViewById(R.id.editText)).setText(task.getTitle());
        }
        int positiveButtonLabel = isNew() ? R.string.create : R.string.update;
        builder.setPositiveButton(positiveButtonLabel, (dialog, id) -> {
            String taskName = ((EditText) newTaskView.findViewById(R.id.editText)).getText().toString();
            if (isNew()) {
                taskListAdapter.add(taskName);
            } else {
                task.setTitle(taskName);
                taskListAdapter.update(task);
                taskListAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            this.dismiss();
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    private boolean isNew() {
        return task == null;
    }
}
