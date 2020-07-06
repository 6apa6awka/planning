package com.first.planning;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.first.planning.component.project.NewProjectDialogFragment;
import com.first.planning.component.task.NewTaskDialogFragment;
import com.first.planning.component.task.TaskListAdapter;
import com.first.planning.component.task.TaskListTouchHelperCallback;
import com.first.planning.persistent.common.service.DataServiceResolver;
import com.first.planning.databinding.MainActivityLayoutBinding;
import com.first.planning.persistent.common.service.impl.RoomDataService;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.service.ProjectService;
import com.first.planning.persistent.room.service.TaskService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TaskService taskService;
    private ProjectService projectService;

    private MainActivityLayoutBinding mainActivityLayout;
    private NavigationView projectNavigationView;
    private RecyclerView taskRecyclerView;

    private List<ProjectEntity> projects;
    private ProjectEntity inboxProject;
    private ProjectEntity currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityLayout = MainActivityLayoutBinding.inflate(getLayoutInflater());
        projectNavigationView = mainActivityLayout.mainNavigationView;
        setContentView(mainActivityLayout.getRoot());

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
            menu.removeItem(R.id.action_delete_project);
            menu.removeItem(R.id.action_edit_project);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            mainActivityLayout.mainDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if(id == R.id.action_delete_project) {
            projectService.deleteProject(currentProject);
            projectNavigationView.getMenu().removeItem(currentProject.getId());
            fillTabWithNewData(inboxProject);
        }
        return super.onOptionsItemSelected(item);
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
        setSupportActionBar(mainActivityLayout.taskListLayout.taskListToolbar);
        mainActivityLayout.taskListLayout.taskListToolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawer() {
        Menu projectMenu = projectNavigationView.getMenu();
        MenuItem inboxMenuItem = projectMenu.findItem(R.id.inbox_item);
        inboxMenuItem.setActionView(R.layout.project_menu_item);
        projectNavigationView.setItemIconTintList(null);
        initProjects();
        Button button = inboxMenuItem.getActionView().findViewById(R.id.button_add);
        button.setOnClickListener(v -> new NewProjectDialogFragment(projectMenu, projectService, projects, getApplicationContext()).show(getSupportFragmentManager(), "New project fragment"));
        projectNavigationView.setNavigationItemSelectedListener(item -> {
            fillTabWithNewData(item);
            mainActivityLayout.mainDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        initMenu();
    }

    private void initMenu() {
        taskRecyclerView = mainActivityLayout.taskListLayout.taskListContentLayout.listView;
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
        TaskListAdapter adapter = (TaskListAdapter) taskRecyclerView.getAdapter();
        adapter.setCurrentProject(projectEntity);
        getSupportActionBar().setTitle(projectEntity.getTitle());
        currentProject = projectEntity;
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
        return projectService.saveProject(inbox);
    }

    private void initProjects() {
        Menu projectMenu = projectNavigationView.getMenu();
        Random rnd = new Random();
        for (int i = 0; i < projects.size(); i++) {
            ProjectEntity project = projects.get(i);
            String title = project.getTitle();
            if (!getString(R.string.inbox).equals(title)) {
                projectMenu.add(R.id.categories, project.getId(), i, title);
                MenuItem item = projectMenu.findItem(project.getId());
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_icon);
                item.setIcon(drawable);
                item.setIconTintList(ColorStateList.valueOf(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))));
            }
        }
    }

    /*private void initToDoBut() {
        Button toDoBut = projectNavigationView.getMenu().findItem(R.id.inbox_item).getActionView().findViewById(R.id.todo_req);
        toDoBut.setOnClickListener(v -> {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url ="https://todoist.com/oauth/authorize?client_id=36158d9d0fbb4e5b8ed8b53376cda087&scope=data:read,data:delete&state=e2a53584d7fe4fff8f1aa4189127044f";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        // Display the first 500 characters of the response string.
                        //textView.setText("Response is: "+ response.substring(0,500));
                        Log.i("gg", response);
                    }, error -> {
                        Log.d("err", "err: ");//textView.setText("That didn't work!");
                    });

            queue.add(stringRequest);
        });
    }*/
}
