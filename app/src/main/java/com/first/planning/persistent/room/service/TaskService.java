package com.first.planning.persistent.room.service;

import com.first.planning.persistent.room.dao.TaskDao;
import com.first.planning.persistent.room.entity.TaskEntity;

import java.util.List;

public class TaskService {
    private TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public List<String> getTasksDataByTab(String tab) {
        return taskDao.getTasksDataByProjectName(tab);
    }

    public List<TaskEntity> getTasksByProjectId(int id) {
        return taskDao.getTasksByProjectId(id);
    }

    public TaskEntity save(TaskEntity taskEntity) {
        taskDao.save(taskEntity);
        return taskEntity;
    }

    public void updateTask(TaskEntity taskEntity) {
        taskDao.updateTask(taskEntity);
    }

    public void deleteTask(TaskEntity task) {
        taskDao.deleteTask(task);
    }
}
