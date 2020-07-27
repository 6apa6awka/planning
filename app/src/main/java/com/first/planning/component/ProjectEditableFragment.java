package com.first.planning.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.first.planning.R;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.service.ProjectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ProjectEditableFragment extends DialogFragment {

    private Menu projectMenu;
    private ProjectService projectService;
    private List<ProjectEntity> projects;
    private Context context;
    private ProjectEntity currentProject;
    private Consumer<ProjectEntity> callback;

    int[] colors;

    public static final String COLOR = "color";

    public ProjectEditableFragment(Menu projectMenu, ProjectService projectService, List<ProjectEntity> projects, Context context, ProjectEntity currentProject, Consumer<ProjectEntity> callback) {
        this.projectMenu = projectMenu;
        this.projectService = projectService;
        this.context = context;
        this.projects = projects;
        this.currentProject = currentProject;
        this.callback = callback;

        colors = context.getResources().getIntArray(R.array.projectColorArray);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View mainView = requireActivity().getLayoutInflater().inflate(R.layout.add_new_project_fragment, null);
        EditText titleEditText = mainView.findViewById(R.id.editText);
        Spinner colorChooser = mainView.findViewById(R.id.project_color_spinner);
        colorChooser.setAdapter(new ProjectColorSpinnerAdapter(context, initDataList(new ArrayList<>())));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView);

        int positiveButton;

        if (currentProject == null) {
            positiveButton = R.string.create;
        } else {
            positiveButton = R.string.update;
            titleEditText.setText(currentProject.getTitle());
            changeSpinnerSelectedValue(colorChooser);
        }

        builder.setPositiveButton(positiveButton, (dialog, id) -> {
            int color = (Integer) mainView.findViewById(R.id.color_spinner_item).getTag();
            MenuItem item;
            if (currentProject == null) {
                ProjectEntity project = new ProjectEntity();
                project = updateAndSave(project, color, titleEditText.getText().toString());
                projects.add(project);
                projectMenu.add(R.id.categories, project.getId(), projectMenu.size(), project.getTitle());
                item = projectMenu.findItem(project.getId());
                currentProject = project;
                callback.accept(currentProject);
            } else {
                updateAndSave(currentProject, color, titleEditText.getText().toString());
                item = projectMenu.findItem(currentProject.getId());
                item.setTitle(currentProject.getTitle());
            }
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle_icon);
            item.setIcon(drawable);
            item.setIconTintList(ColorStateList.valueOf(color));
        });


        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            this.dismiss();
        });

        return builder.create();
    }

    private void changeSpinnerSelectedValue(Spinner colorChooser) {
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == currentProject.getColor()) {
                colorChooser.setSelection(i);
                break;
            }
        }
    }

    private ArrayList<Map<String, Object>> initDataList(ArrayList<Map<String, Object>> dataList) {
        for (int color : colors) {
            Map<String, Object> colorMap = new HashMap<>();
            colorMap.put(COLOR, color);
            dataList.add(colorMap);
        }
        return dataList;
    }

    private ProjectEntity updateAndSave(ProjectEntity project, int color, String title) {
        project.setTitle(title);
        project.setColor(color);
        return projectService.saveProject(project);
    }
}
