package com.example.todo;

public class TaskItem {
    String task;

    TaskItem () {
        this.task = "";
    }

    public TaskItem(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }
}
