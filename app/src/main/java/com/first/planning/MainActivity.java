package com.first.planning;

import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.first.planning.component.project.MoveToProjectOnClickListener;
import com.first.planning.component.project.ProjectEditableFragment;
import com.first.planning.component.task.NewTaskDialogFragment;
import com.first.planning.component.task.TaskListAdapter;
import com.first.planning.component.task.TaskListTouchHelperCallback;
import com.first.planning.databinding.MainLayoutBinding;
import com.first.planning.persistent.common.service.DataServiceResolver;
import com.first.planning.persistent.common.service.impl.RoomDataService;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.service.ProjectService;
import com.first.planning.persistent.room.service.TaskService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    //Db services
    private TaskService taskService;
    private ProjectService projectService;

    //Components
    private MainLayoutBinding mainLayout;
    private NavigationView projectNavigationView;
    private RecyclerView taskRecyclerView;
    private Toolbar toolbar;

    //Entities
    private List<ProjectEntity> projects;
    private ProjectEntity inboxProject;
    private ProjectEntity currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = MainLayoutBinding.inflate(getLayoutInflater());
        projectNavigationView = mainLayout.mainNavigationView;
        setContentView(mainLayout.getRoot());

        //initialization
        initData();
        initComponents();
        fillTabWithNewData(inboxProject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getString(R.string.inbox).equals(currentProject.getTitle())) {
            menu.findItem(R.id.action_delete_project).setEnabled(false);
            menu.findItem(R.id.action_edit_project).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mainLayout.mainDrawer.openDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.action_delete_project) {
            projectService.deleteProject(currentProject);
            int order = projectNavigationView.getMenu().findItem(currentProject.getId()).getOrder();
            projectNavigationView.getMenu().removeItem(currentProject.getId());
            projects.remove(order);
            fillTabWithNewData(projects.get(order - 1));
            return true;
        } else if (id == R.id.action_edit_project) {
            ProjectEditableFragment projectEditableFragment = new ProjectEditableFragment(projectNavigationView.getMenu(), projectService, projects, getApplicationContext(), currentProject, null);
            projectEditableFragment.show(getSupportFragmentManager(), "Update Project");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        RoomDataService dataService = DataServiceResolver.resolve(loadAppProperties(), getApplicationContext());
        projectService = dataService.getProjectService();
        taskService = dataService.getTaskService();

        projects = projectService.getProjects();
        inboxProject = projects.stream()
                .filter(project -> getString(R.string.inbox).equals(project.getTitle()))
                .findFirst().orElseGet(this::initInbox);

        currentProject = inboxProject;
    }

    private void initComponents() {
        initToolbar();
        initDrawer();
        initAddNewTaskButton();
    }

    private void initToolbar() {
        toolbar = mainLayout.mainLayoutBody.taskListToolbar;
        setSupportActionBar(toolbar);
        toolbar.getOverflowIcon().setColorFilter(new BlendModeColorFilter(Color.WHITE, BlendMode.SRC_IN));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawer() {
        Menu projectMenu = projectNavigationView.getMenu();
        MenuItem inboxMenuItem = projectMenu.findItem(R.id.inbox_item);
        inboxMenuItem.setActionView(R.layout.add_project_button_layout);
        projectNavigationView.setItemIconTintList(null);
        initProjects();
        Button button = inboxMenuItem.getActionView().findViewById(R.id.button_add);
        button.setOnClickListener(v ->
                new ProjectEditableFragment(projectMenu, projectService, projects, getApplicationContext(), null, this::fillTabWithNewData)
                .show(getSupportFragmentManager(), "New project fragment"));
        projectNavigationView.setNavigationItemSelectedListener(item -> {
            fillTabWithNewData(item);
            mainLayout.mainDrawer.closeDrawer(GravityCompat.START);
            return true;
        });
        initMenu();
    }

    private void initMenu() {
        taskRecyclerView = mainLayout.mainLayoutBody.taskListLayout.listView;
        taskRecyclerView.setHasFixedSize(true);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TaskListAdapter adapter = new TaskListAdapter(taskService);
        taskRecyclerView.setAdapter(adapter);
        TaskListTouchHelperCallback taskListTouchHelperCallback = new TaskListTouchHelperCallback(adapter);
        new ItemTouchHelper(taskListTouchHelperCallback).attachToRecyclerView(taskRecyclerView);
        taskRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    private void fillTabWithNewData(MenuItem item) {
        ProjectEntity project = item.getGroupId() == R.id.categories ? projects.get(item.getOrder()) : inboxProject;
        fillTabWithNewData(project);
    }

    private void fillTabWithNewData(ProjectEntity projectEntity) {
        currentProject = projectEntity;
        TaskListAdapter adapter = (TaskListAdapter) taskRecyclerView.getAdapter();
        adapter.setCurrentProject(projectEntity);
        adapter.setMoveToProjectListener(getMoveToProjectListener());
        getSupportActionBar().setTitle(projectEntity.getTitle());
        invalidateOptionsMenu();
    }

    private void initAddNewTaskButton() {
        FloatingActionButton newTaskButton = findViewById(R.id.addTaskButton);
        NewTaskDialogFragment fragment = new NewTaskDialogFragment((TaskListAdapter) taskRecyclerView.getAdapter());
        newTaskButton.setOnClickListener(view -> {
            fragment.show(getSupportFragmentManager(), "testDialog");
        });
    }

    private ProjectEntity initInbox() {
        ProjectEntity inbox = new ProjectEntity();
        inbox.setTitle(getString(R.string.inbox));
        ProjectEntity inboxProject = projectService.saveProject(inbox);
        projects.add(inbox);
        return inboxProject;
    }

    private void initProjects() {
        Menu projectMenu = projectNavigationView.getMenu();
        for (int i = 0; i < projects.size(); i++) {
            ProjectEntity project = projects.get(i);
            String title = project.getTitle();
            if (!getString(R.string.inbox).equals(title)) {
                projectMenu.add(R.id.categories, project.getId(), i, title);
                MenuItem item = projectMenu.findItem(project.getId());
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_icon);
                item.setIcon(drawable);
                item.setIconTintList(ColorStateList.valueOf(project.getColor()));
            }
        }
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

    private MoveToProjectOnClickListener getMoveToProjectListener() {
        return new MoveToProjectOnClickListener(projects.stream()
                .filter(pe -> pe.getId() != currentProject.getId())
                .collect(Collectors.toList()));
    }
}
