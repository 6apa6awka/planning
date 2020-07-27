package com.first.planning.component;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import androidx.core.content.ContextCompat;

import com.first.planning.R;

import java.util.ArrayList;
import java.util.Map;

import static com.first.planning.component.ProjectEditableFragment.COLOR;

public class ProjectColorSpinnerAdapter extends SimpleAdapter {
    private Context context;
    private ArrayList<Map<String, Object>> dataList;

    public ProjectColorSpinnerAdapter(Context context, ArrayList<Map<String, Object>> data) {
        super(context, data, R.layout.project_color_spinner_layout, new String[]{COLOR}, new int[]{R.id.color_spinner_item});
        this.context = context;
        dataList = data;
        setViewBinder((ViewBinder) (view, dataToBind, textRepresentation) -> {
            ((ImageView) view).setBackground(getDrawableForColor((Integer) dataToBind));
            return true;
        });
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.project_color_spinner_layout,
                    null);
        }
        ImageView imageView = convertView.findViewById(R.id.color_spinner_item);
        imageView.setBackground(getDrawableForColor((Integer) dataList.get(position).get(COLOR)));
        imageView.setTag(dataList.get(position).get(COLOR));
        return convertView;
    }

    private Drawable getDrawableForColor(int color) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle_icon);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC);
        return drawable;
    }
}
