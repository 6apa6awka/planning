package com.first.planning.component;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.first.planning.R;
import com.first.planning.persistent.room.entity.TaskEntity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

public class TaskListViewHolder extends RecyclerView.ViewHolder {
    private TextView editText;
    private TaskEntity currentTask;
    private Button changeProjectButton;
    private TaskListAdapter adapter;
    private Activity context;
    private MoveToProjectListener moveToProjectListener;
    private int currentLayout;
    private TaskEditableFragment taskDialogFragment;

    public TaskListViewHolder(ViewGroup parent, int layoutId, TaskListAdapter adapter, Activity context) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false), layoutId, adapter, context);
    }

    private TaskListViewHolder(@NonNull View itemView, int layoutId, TaskListAdapter adapter, Activity context) {
        super(itemView);
        editText = itemView.findViewById(R.id.taskText);
        this.adapter = adapter;
        this.context = context;
        currentLayout = layoutId;
        if (!isAlternateLayout()) {
            initImageView();
        } else {
            initMoveToProjectButton();
            initEditTextButton();
        }
    }

    private void initImageView() {
        ShapeableImageView imageView = itemView.findViewById(R.id.taskStatusColor);
        imageView.setShapeAppearanceModel(imageView.getShapeAppearanceModel()
                .toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, 12)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 12)
                .setBottomRightCorner(CornerFamily.ROUNDED, 5)
                .setBottomRightCorner(CornerFamily.ROUNDED, 5)
                .build());
    }

    private void initMoveToProjectButton() {
        changeProjectButton = itemView.findViewById(R.id.buttonMoveTo);
        moveToProjectListener = new MoveToProjectListener();
        changeProjectButton.setOnClickListener(moveToProjectListener);
    }

    private void initEditTextButton() {
        Button editTitleButton = itemView.findViewById(R.id.editTitle);
        taskDialogFragment = new TaskEditableFragment(adapter);
        editTitleButton.setOnClickListener(v -> {
            taskDialogFragment.show(((FragmentActivity)context).getSupportFragmentManager(), "Test");
        });
    }

    public void setTask(TaskEntity taskEntity) {
        editText.setText(taskEntity.getTitle());
        currentTask = taskEntity;
        if (currentLayout == R.layout.task_component_layout_alternative) {
            taskDialogFragment.setTask(taskEntity);
            moveToProjectListener.setCurrentTask(taskEntity);
        }
    }

    public MoveToProjectListener getMoveToProjectListener() {
        return moveToProjectListener;
    }

    public boolean isAlternateLayout() {
        return currentLayout == R.layout.task_component_layout_alternative;
    }
}
