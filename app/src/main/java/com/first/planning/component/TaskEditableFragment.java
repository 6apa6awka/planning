package com.first.planning.component;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.first.planning.AddTaskShortcutActivity;
import com.first.planning.R;
import com.first.planning.persistent.room.entity.TaskEntity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TaskEditableFragment extends DialogFragment {

    private TaskListAdapter taskListAdapter;
    private TaskEntity task;
    private AddTaskShortcutActivity parentActivity;

    public TaskEditableFragment(TaskListAdapter taskListAdapter) {
        this.taskListAdapter = taskListAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        View newTaskView = requireActivity().getLayoutInflater().inflate(R.layout.add_new_task_fragment, null);
        builder.setView(newTaskView);
        EditText editText = newTaskView.findViewById(R.id.editText);

        int positiveButtonLabel;
        if (isNew()) {
            positiveButtonLabel = R.string.create;
        } else {
            editText.setText(task.getTitle());
            positiveButtonLabel = R.string.update;
        }

        builder.setPositiveButton(positiveButtonLabel, (dialog, id) -> {
            String taskName = editText.getText().toString();
            if (isNew()) {
                taskListAdapter.add(taskName);
            } else {
                task.setTitle(taskName);
                taskListAdapter.update(task);
                taskListAdapter.notifyDataSetChanged();
            }
            if (parentActivity != null) {
                getActivity().finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            this.dismiss();
            if (parentActivity != null) {
                getActivity().finish();
            }
        });
        editText.requestFocus();
        return builder.create();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        super.onActivityCreated(savedInstanceState);
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    private boolean isNew() {
        return task == null;
    }

    public void setParentActivity(AddTaskShortcutActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {

        super.show(manager, tag);
    }
}
