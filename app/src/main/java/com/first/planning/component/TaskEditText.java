package com.first.planning.component;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.first.planning.persistent.room.entity.TaskEntity;

public class TaskEditText extends AppCompatEditText {

    private TaskEntity taskEntity;
    private TaskListAdapter adapter;

    public TaskEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        /*setOnClickListener(v -> {
            //((EditText) v).setInputType(InputType.TYPE_CLASS_TEXT);
        });
        setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveText();
            }
        });
        setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveText();
            }
            return false;
        });*/
    }

   /* @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            saveText();
        }
        return super.onKeyPreIme(keyCode, event);
    }*/

    private void saveText(){
        setInputType(InputType.TYPE_NULL);
        taskEntity.setTitle(getText().toString());
        adapter.update(taskEntity);
    }

    public void setTaskEntity(TaskEntity taskEntity) {
        this.taskEntity = taskEntity;
        setText(taskEntity.getTitle());
    }

    public void setAdapter(TaskListAdapter adapter) {
        this.adapter = adapter;
    }
}
