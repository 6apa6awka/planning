package com.first.planning.component.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.first.planning.R;
import com.first.planning.persistent.room.entity.ProjectEntity;
import com.first.planning.persistent.room.service.ProjectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewProjectDialogFragment extends DialogFragment {

    private Menu projectMenu;
    private ProjectService projectService;
    private List<ProjectEntity> projects;
    private Context context;

    public NewProjectDialogFragment(Menu projectMenu, ProjectService projectService, List<ProjectEntity> projects, Context context) {
        this.projectMenu = projectMenu;
        this.projectService = projectService;
        this.context = context;
        this.projects = projects;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View newProjectView = requireActivity().getLayoutInflater().inflate(R.layout.add_new_project_fragment, null);
        Spinner projectColorSpinner = newProjectView.findViewById(R.id.project_color_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(context, R.array.projectColorArray, android.R.layout.simple_spinner_item);
        String[] colorList = context.getResources().getStringArray(R.array.projectColorArray);
        ArrayList<Map<String, Integer>> adapterList = new ArrayList<>();
        for (String color : colorList) {
            Map<String, Integer> colorMap = new HashMap<>();
            colorMap.put("Color", Color.parseColor(color));
            adapterList.add(colorMap);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectColorSpinner.setAdapter(adapter);
        builder.setView(newProjectView);
        builder.setPositiveButton(R.string.create, (dialog, id) -> {
            String projectName = ((EditText) newProjectView.findViewById(R.id.editText)).getText().toString();
            ProjectEntity project = new ProjectEntity();
            project.setTitle(projectName);
            project = projectService.saveProject(project);
            projects.add(projectMenu.size(), project);
            projectMenu.add(R.id.categories, project.getId(), projectMenu.size(), project.getTitle());
            MenuItem item = projectMenu.findItem(project.getId());
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle_icon);
            Random rnd = new Random();
            item.setIcon(drawable);
            item.setIconTintList(ColorStateList.valueOf(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))));
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            this.dismiss();
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
