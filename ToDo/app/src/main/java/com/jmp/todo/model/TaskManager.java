package com.jmp.todo.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;


public class TaskManager {
    private Context context;
    private DbManager dbManager;
    private ArrayList<Task> tasks;

    public ArrayList<Task> getTasks() {
        return tasks;
    }


    public TaskManager(Context context) {
        this.context = context;
        this.dbManager = new DbManager(context);
        this.tasks = new ArrayList<>();
    }
    public Task getTask(int pos) {
       if (tasks.size() <= pos) return null;
       return tasks.get(pos);

    }
    public void setTask(Task task, int pos) {
        if (tasks.size() <= pos) return;
        tasks.set(pos, task);
        dbManager.updateTask(task);
    }
    public void addTask(Task task) {
        tasks.add(task);
        dbManager.insertTask(task);
    }
    public void updateDoneTask(Task task) {
        dbManager.updateDoneTask(task);
    }
    public void deleteDoneTask() {
        dbManager.deleteDoneTask(tasks);
        for (Iterator<Task> task = tasks.iterator(); task.hasNext();) {
            if (task.next().isDone()) {
                task.remove();
            }
        }
    }
    public void getTasksFromDB() {
        tasks = dbManager.selectTasks();
    }
}
