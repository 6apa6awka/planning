package com.first.planning.component.project;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.core.content.ContextCompat;

import com.first.planning.R;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.entity.TaskEntity;

import java.util.List;
import java.util.function.Consumer;

public class MoveToProjectListener implements View.OnClickListener {
    private List<ProjectEntity> projectEntities;
    private TaskEntity currentTask;
    private Consumer<TaskEntity> callback;

    @Override
    public void onClick(View v) {
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
            currentTask.setProjectId(item.getItemId());
            callback.accept(currentTask);
            popup.dismiss();
            return false;
        });
        popup.getMenuInflater().inflate(R.menu.project_menu, popupMenu);
        popup.show();
    }

    public void setProjectEntities(List<ProjectEntity> projectEntities) {
        this.projectEntities = projectEntities;
    }

    public void setCurrentTask(TaskEntity currentTask) {
        this.currentTask = currentTask;
    }

    public void setCallback(Consumer<TaskEntity> callback) {
        this.callback = callback;
    }
}
