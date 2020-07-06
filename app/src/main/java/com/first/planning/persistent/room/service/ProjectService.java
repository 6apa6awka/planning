package com.first.planning.persistent.room.service;

import com.first.planning.persistent.room.dao.ProjectDao;
import com.first.planning.persistent.room.entity.ProjectEntity;

import java.util.List;

public class ProjectService {
    private ProjectDao projectDao;

    public ProjectService(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    public List<String> getProjectsTitles() {
        return projectDao.getAllProjectNames();
    }

    public List<ProjectEntity> getProjects() {
        return projectDao.getProjects();
    }

    public ProjectEntity saveProject(ProjectEntity projectEntity) {
        return projectDao.findProjectById(projectDao.save(projectEntity));
    }

    public void deleteProject(ProjectEntity project) {
        projectDao.delete(project);
    }
}
