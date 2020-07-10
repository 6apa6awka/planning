package com.first.planning.persistent.common.service.impl;

import android.content.Context;

import androidx.room.Room;

import com.first.planning.persistent.room.RoomDatabase;
import com.first.planning.persistent.room.service.ProjectService;
import com.first.planning.persistent.room.service.TaskService;

public class RoomDataService {
    private TaskService taskService;
    private ProjectService projectService;

    public void init(Context context) {
        RoomDatabase db = Room.databaseBuilder(context,
                RoomDatabase.class, "planning-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() //ToDo change to support migrations
                .build();

        taskService = new TaskService(db.taskDao());
        projectService = new ProjectService(db.projectDao());
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public ProjectService getProjectService() {
        return projectService;
    }
}
