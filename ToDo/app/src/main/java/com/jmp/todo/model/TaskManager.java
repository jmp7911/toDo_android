package com.jmp.todo.model;

import android.content.Context;

import com.jmp.todo.iface.OnDeleteTaskListener;
import com.jmp.todo.iface.OnPutTaskListener;
import com.jmp.todo.iface.OnSetTasksListener;
import com.jmp.todo.iface.OnPostTaskListener;

import java.util.ArrayList;
import java.util.Iterator;

import static com.jmp.todo.model.ServerManager.DELETE;
import static com.jmp.todo.model.ServerManager.GET;
import static com.jmp.todo.model.ServerManager.POST;
import static com.jmp.todo.model.ServerManager.PUT;

public class TaskManager {
    private Context context;
    private DbManager dbManager;
    private ArrayList<Task> tasks;
    public static final String GET_URL = "http://todo-android-study.herokuapp.com/tasks";
    public static final String POST_URL = "http://todo-android-study.herokuapp.com/task/";
    public static final String PUT_URL = POST_URL;
    public static final String DELETE_URL = POST_URL;
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
    public void setTask(Task task, int pos, OnPutTaskListener listener) {
        if (tasks.size() <= pos) return;
        tasks.set(pos, task);
        dbManager.updateTask(task);
        ServerManager serverManager = new ServerManager(context, listener);
        serverManager.execute(PUT, task.getTaskId(), task.getContent(), Boolean.toString(task.isDone())
                , Long.toString(task.getDueDate()));
    }
    public void addTask(Task task, OnPostTaskListener listener) {
        tasks.add(task);
        dbManager.insertTask(task);
        ServerManager serverManager = new ServerManager(context, listener);
        serverManager.execute(POST, task.getTaskId(), task.getContent(), Boolean.toString(task.isDone())
                , Long.toString(task.getDueDate()));

    }
    public void updateDoneTask(Task task) {
        dbManager.updateDoneTask(task);
    }
    public void deleteDoneTask(OnDeleteTaskListener listener) {
        dbManager.deleteDoneTask(tasks);
        for (Task task : tasks) {
            if (task.isDone()) {
                ServerManager serverManager = new ServerManager(context, listener);
                serverManager.execute(DELETE, task.getTaskId());
            }
        }
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
        serverManager.execute(GET);
    }
}
