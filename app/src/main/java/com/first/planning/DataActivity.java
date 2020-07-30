package com.first.planning;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.first.planning.persistent.common.service.DataServiceResolver;
import com.first.planning.persistent.common.service.impl.RoomDataService;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.service.ProjectService;
import com.first.planning.persistent.room.service.TaskService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class DataActivity extends AppCompatActivity {
    //Db services
    protected TaskService taskService;
    protected ProjectService projectService;

    //Entities
    protected List<ProjectEntity> projects;
    protected ProjectEntity inboxProject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    protected void initData() {
        RoomDataService dataService = DataServiceResolver.resolve(loadAppProperties(), getApplicationContext());
        projectService = dataService.getProjectService();
        taskService = dataService.getTaskService();
        projects = projectService.getProjects();
        inboxProject = projects.stream()
                .filter(project -> getString(R.string.inbox).equals(project.getTitle()))
                .findFirst().orElseGet(this::initInbox);
    }

    private Properties loadAppProperties() {
        try {
            InputStream ip = getAssets().open("app.properties");
            Properties properties = new Properties();
            properties.load(ip);
            ip.close();
            return properties;
        } catch (IOException e) {
            Log.e("CREATION", "onCreate: can't load appProperties file. Default values will be used", e);
            return new Properties();
        }

    }

    private ProjectEntity initInbox() {
        ProjectEntity inbox = new ProjectEntity();
        inbox.setTitle(getString(R.string.inbox));
        ProjectEntity inboxProject = projectService.saveProject(inbox);
        projects.add(inbox);
        return inboxProject;
    }
}
