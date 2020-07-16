package com.first.planning.component.task;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.first.planning.R;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.entity.TaskEntity;
import com.first.planning.persistent.room.service.TaskService;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {

    private List<TaskEntity> data;
    private List<ProjectEntity> projectEntities;
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

    public void setProjectEntities(List<ProjectEntity> projectEntities) {
        this.projectEntities = projectEntities.stream().filter(pe -> pe.getId() != currentProject.getId()).collect(Collectors.toList());
    }

    public static class TaskListViewHolder extends RecyclerView.ViewHolder {
        private TextView editText;


        public TaskListViewHolder(ViewGroup parent, int layoutId, List<ProjectEntity> projectEntities) {
            this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false), projectEntities);
        }

        public TaskListViewHolder(@NonNull View itemView, List<ProjectEntity> projectEntities) {
            super(itemView);
            editText = itemView.findViewById(R.id.taskText);
            ShapeableImageView imageView = itemView.findViewById(R.id.taskStatusColor);
            if (imageView != null) {
                //imageView.setBackgroundColor(Color.argb(255, 255, 128, 0));
                imageView.setShapeAppearanceModel(imageView.getShapeAppearanceModel()
                        .toBuilder()
                        //.setAllCorners(CornerFamily.ROUNDED, 10)
                        .setTopLeftCorner(CornerFamily.ROUNDED, 12)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 12)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 5)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 5)
                        .build());
            }
            Button changeProjectButton = itemView.findViewById(R.id.buttonMoveTo);
            if (changeProjectButton != null) {
                changeProjectButton.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    popup.setForceShowIcon(true);
                    Menu popupMenu = popup.getMenu();
                    for (int i = 0; i < projectEntities.size(); i++) {
                        ProjectEntity project = projectEntities.get(i);
                        popupMenu.add(Menu.NONE, project.getId(), i, project.getTitle());
                        MenuItem item = popupMenu.findItem(project.getId());
                        if (v.getContext().getResources().getString(R.string.inbox).equals(project.getTitle())) {
                            Drawable drawable = ContextCompat.getDrawable(v.getContext(), R.drawable.ic_mail_outline_black_24dp);
                            item.setIcon(drawable);
                            item.setIconTintList(ColorStateList.valueOf(Color.BLACK));
                        } else {
                            Drawable drawable = ContextCompat.getDrawable(v.getContext(), R.drawable.circle_icon);
                            item.setIcon(drawable);
                            item.setIconTintList(ColorStateList.valueOf(project.getColor()));
                        }
                    }
                    popup.setOnMenuItemClickListener(item -> {

                        popup.dismiss();
                        return false;
                    });
                    popup.getMenuInflater().inflate(R.menu.project_menu, popupMenu);
                    popup.show();
                });
            }
        }

        public void setText(String text) {
            editText.setText(text);
        }
    }

    @NonNull
    @Override
    public TaskListAdapter.TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskListViewHolder(parent, viewType, projectEntities);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        //holder.editText.setAdapter(this);
        //holder.editText.setTaskEntity(data.get(position));
        holder.setText(data.get(position).getTitle());
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
