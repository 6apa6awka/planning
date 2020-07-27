package com.first.planning.component.task;

import android.app.Activity;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.first.planning.R;
import com.first.planning.component.project.MoveToProjectListener;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.entity.TaskEntity;
import com.first.planning.persistent.room.service.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListViewHolder> {

    private List<TaskEntity> data;
    private TaskService taskService;
    private ProjectEntity currentProject;
    private Activity context;
    private List<ProjectEntity> projects;

    private int collapsedCurPosition = -1;
    private int collapsedPrevPosition = -1;

    public TaskListAdapter(TaskService taskService, Activity context) {
        //((Application)context).
        this.taskService = taskService;
        data = new ArrayList<>();
        this.context = context;
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
        collapsedPrevPosition = -1;
        collapsedCurPosition = -1;
        notifyDataSetChanged();
    }

    public ProjectEntity getCurrentProject() {
        return currentProject;
    }

    public List<ProjectEntity> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectEntity> projects) {
        this.projects = projects;
    }

    public void moveTaskToOtherProject(TaskEntity taskEntity) {
        update(taskEntity);
        data.remove(taskEntity);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskListViewHolder(parent, viewType, this, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        TaskEntity currentTask = data.get(position);
        holder.setTask(currentTask);

        if (holder.isAlternateLayout()) {
            MoveToProjectListener listener = holder.getMoveToProjectListener();
            listener.setCurrentTask(currentTask);
            listener.setProjectEntities(projects
                    .stream()
                    .filter(p -> p.getId() != currentProject.getId())
                    .collect(Collectors.toList()));
            listener.setCallback(this::moveTaskToOtherProject);
        }

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
