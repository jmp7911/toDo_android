package com.example.todo;

public class TaskItem {
    String task;
    String place;
    TaskItem () {
        this.task = "";
        this.place = "";
    }

    public TaskItem(String task, String place) {
        this.task = task;
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public String getTask() {
        return task;
    }
}
