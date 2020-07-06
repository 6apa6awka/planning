package com.first.planning.persistent.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.first.planning.persistent.room.entity.ProjectEntity;

import java.util.List;

@Dao
public interface ProjectDao {

    @Query("SELECT title FROM ProjectEntity WHERE title <> 'Inbox'")
    List<String> getAllProjectNames();

    @Query("SELECT * FROM ProjectEntity")
    List<ProjectEntity> getProjects();

    @Query("SELECT id FROM ProjectEntity WHERE title = :name")
    long findProjectIdByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(ProjectEntity projectEntity);

    @Query("SELECT * FROM ProjectEntity WHERE id = :id")
    ProjectEntity findProjectById(long id);

    @Query("DELETE FROM ProjectEntity WHERE title = :name")
    void deleteByName(String name);

    @Delete
    void delete(ProjectEntity project);
}
