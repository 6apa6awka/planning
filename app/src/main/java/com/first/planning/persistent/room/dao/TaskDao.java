package com.first.planning.persistent.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.first.planning.persistent.room.entity.TaskEntity;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM TaskEntity WHERE project_id = :projectId")
    List<TaskEntity> getTasksByProjectId(int projectId);

    @Query("SELECT te.title FROM TaskEntity te " +
            "JOIN ProjectEntity pe ON pe.id = te.project_id " +
            "WHERE pe.title = :projectName")
    List<String> getTasksDataByProjectName(String projectName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(TaskEntity taskEntity);

    @Query("SELECT * FROM TaskEntity")
    List<TaskEntity> getAll();

    @Query("DELETE FROM TaskEntity WHERE title = :name")
    void deleteTaskByName(String name);

    @Delete
    void deleteTask(TaskEntity taskEntity);

    @Update
    void updateTask(TaskEntity taskEntity);

}

