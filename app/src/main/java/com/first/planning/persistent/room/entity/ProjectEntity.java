package com.first.planning.persistent.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProjectEntity{

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private int order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
