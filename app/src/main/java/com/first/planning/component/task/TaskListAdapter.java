package com.first.planning.component.task;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.first.planning.R;
import com.first.planning.component.project.MoveToProjectOnClickListener;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.entity.TaskEntity;
import com.first.planning.persistent.room.service.TaskService;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListViewHolder> {

    private List<TaskEntity> data;
    private MoveToProjectOnClickListener listener;
    private TaskService taskService;
    private ProjectEntity currentProject;

    private int collapsedCurPosition = -1;
    private int collapsedPrevPosition = -1;

    public TaskListAdapter(TaskService taskService) {
        this.taskService = taskService;
        data = new ArrayList<>();
    }

    public void add(String taskName) {
        TaskEntity task = new TaskEntity();
        task.setTitle(taskName);
        task.setProjectId(currentProject.getId());
        data.add(taskService.save(task));
        notifyDataSetChanged();
    }

    public void update(TaskEntity taskEntity) {
        taskService.updateTask(taskEntity);
    }

    public void remove(int index) {
        taskService.deleteTask(data.remove(index));
        notifyDataSetChanged();
    }

    public void setCurrentProject(ProjectEntity currentProject) {
        this.currentProject = currentProject;
        data.clear();
        data.addAll(taskService.getTasksByProjectId(currentProject.getId()));
        notifyDataSetChanged();
    }

    public void setMoveToProjectListener(MoveToProjectOnClickListener listener) {
        listener.setCallback(this::moveTaskToOtherProject);
        this.listener = listener;
    }

    private void moveTaskToOtherProject(TaskEntity taskEntity) {
        update(taskEntity);
        data.remove(taskEntity);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskListViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        holder.setTask(data.get(position));
        holder.setMoveToProjectListener(listener);
        holder.itemView.setOnClickListener(v -> {
            if (collapsedPrevPosition != -1) {
                notifyItemChanged(collapsedPrevPosition);
            }
            collapsedCurPosition = position;
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == collapsedPrevPosition) {
            collapsedPrevPosition = -1;
            return R.layout.task_component_layout;
        }
        if (position == collapsedCurPosition) {
            collapsedPrevPosition = position;
            return R.layout.task_component_layout_alternative;
        }
        return R.layout.task_component_layout;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
