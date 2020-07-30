package com.first.planning;

import android.os.Bundle;

import com.first.planning.component.TaskEditableFragment;
import com.first.planning.component.TaskListAdapter;

public class AddTaskShortcutActivity extends DataActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskListAdapter adapter = new TaskListAdapter(taskService, this);
        adapter.setCurrentProject(inboxProject);
        TaskEditableFragment fragment = new TaskEditableFragment(adapter);
        fragment.setParentActivity(this);
        fragment.show(getSupportFragmentManager(), "Shortcut");
    }
}
