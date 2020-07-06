package com.first.planning.persistent.room;

import androidx.room.Database;

import com.first.planning.persistent.room.dao.ProjectDao;
import com.first.planning.persistent.room.dao.TaskDao;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.entity.TaskEntity;

@Database(entities = {TaskEntity.class, ProjectEntity.class}, version = 1, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract ProjectDao projectDao();
}
