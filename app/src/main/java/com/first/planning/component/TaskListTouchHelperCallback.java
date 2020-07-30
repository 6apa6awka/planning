package com.first.planning.component;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.first.planning.R;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

public class TaskListTouchHelperCallback extends ItemTouchHelper.Callback {

    private TaskListAdapter adapter;

    public TaskListTouchHelperCallback(TaskListAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeFlag(ACTION_STATE_IDLE, RIGHT) | makeFlag(ACTION_STATE_SWIPE, RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (RIGHT == direction) {
            adapter.remove(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            int part = (itemView.getBottom() - itemView.getTop()) / 4;

            if (dX > 0) {
                ColorDrawable background = new ColorDrawable(recyclerView.getResources().getColor(R.color.red, null));
                background.setBounds(0, itemView.getTop(), (int) (itemView.getLeft() + dX), itemView.getBottom());
                background.draw(c);
                // Draw Rect with varying right side, equal to displacement dX
                Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_delete_white_24);
                icon.setBounds(part, itemView.getTop() + part, part*3, itemView.getTop() + (part*3));
                icon.draw(c);
                //c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), p);
            } else {
                /* Set your color for negative displacement */

                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX

            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
