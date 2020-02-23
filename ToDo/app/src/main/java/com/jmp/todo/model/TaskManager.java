package com.jmp.todo.model;

import android.content.Context;

import com.jmp.todo.iface.OnSetTasksListener;
import com.jmp.todo.iface.OnTaskChangedListener;

import java.util.ArrayList;
import java.util.Iterator;

public class TaskManager {
    private Context context;
    private DbManager dbManager;
    private ArrayList<Task> tasks;
    public static final String GET_URL = "http://todo-android-study.herokuapp.com/tasks";
    public static final String POST_URL = "http://todo-android-study.herokuapp.com/task/";
    public TaskManager(Context context) {
        this.context = context;
        this.dbManager = new DbManager(context);
        this.tasks = new ArrayList<>();
    }
    public ArrayList<Task> getTasks() {
        return tasks;
    }
    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
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
    public void addTask(Task task, OnTaskChangedListener listener) {
        tasks.add(task);
        dbManager.insertTask(task);
        ServerManager serverManager = new ServerManager(context, listener);
        serverManager.execute(POST_URL, task.getTaskId(), task.getContent(), Boolean.toString(task.isDone())
                , Long.toString(task.getDueDate()));

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
    public void getTasksFromServer(Context context, OnSetTasksListener listener) {
        ServerManager serverManager = new ServerManager(context, listener);
        serverManager.execute(GET_URL);
    }
}
