package com.first.planning.component.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.first.planning.R;
import com.first.planning.component.project.MoveToProjectOnClickListener;
import com.first.planning.persistent.room.entity.TaskEntity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

public class TaskListViewHolder extends RecyclerView.ViewHolder {
    private TextView editText;
    private TaskEntity taskEntity;
    private Button changeProjectButton;

    public TaskListViewHolder(ViewGroup parent, int layoutId) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    private TaskListViewHolder(@NonNull View itemView) {
        super(itemView);
        editText = itemView.findViewById(R.id.taskText);
        initImageView();
        initMoveToProjectButton();
    }

    private void initImageView() {
        ShapeableImageView imageView = itemView.findViewById(R.id.taskStatusColor);
        if (imageView != null) {
            imageView.setShapeAppearanceModel(imageView.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, 12)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 12)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 5)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 5)
                    .build());
        }
    }

    private void initMoveToProjectButton() {
        changeProjectButton = itemView.findViewById(R.id.buttonMoveTo);
    }

    public void setTask(TaskEntity taskEntity) {
        this.taskEntity = taskEntity;
        editText.setText(taskEntity.getTitle());
    }

    public void setMoveToProjectListener(MoveToProjectOnClickListener listener) {
        if (changeProjectButton != null) {
            listener.setCurrentTask(taskEntity);
            changeProjectButton.setOnClickListener(listener);
        }
    }
}
