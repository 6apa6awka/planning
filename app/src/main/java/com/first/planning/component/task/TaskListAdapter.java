package com.first.planning.component.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.first.planning.R;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.entity.TaskEntity;
import com.first.planning.persistent.room.service.TaskService;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {

    private List<TaskEntity> data;
    private TaskService taskService;
    private ProjectEntity currentProject;

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

    public static class TaskListViewHolder extends RecyclerView.ViewHolder {
        private TaskEditText editText;

        public TaskListViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.taskText);
        }

        public void setText(String text) {
            editText.setText(text);
        }
    }

    @NonNull
    @Override
    public TaskListAdapter.TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View returnedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new TaskListViewHolder(returnedView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        holder.editText.setAdapter(this);
        holder.editText.setTaskEntity(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
