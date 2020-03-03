package com.jmp.todo.model;

import android.content.Context;
import android.util.Log;

import com.jmp.todo.iface.APIService;
import com.jmp.todo.iface.OnDeleteTaskListener;
import com.jmp.todo.iface.OnPutTaskListener;
import com.jmp.todo.iface.OnSetTasksListener;
import com.jmp.todo.iface.OnPostTaskListener;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.jmp.todo.model.ServerTaskManager.DELETE;
import static com.jmp.todo.model.ServerTaskManager.GET;
import static com.jmp.todo.model.ServerTaskManager.PUT;

public class TaskManager {
    private Context context;
    private DbManager dbManager;
    private ArrayList<Task> tasks;
    private Retrofit retrofit;
    private APIService apiService;
    private final String BASE_URL = "http://todo-android-study.herokuapp.com";

    public TaskManager(Context context) {
        this.context = context;
        this.dbManager = new DbManager(context);
        this.tasks = new ArrayList<>();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        this.apiService = retrofit.create(APIService.class);
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
        ServerTaskManager serverTaskManager = new ServerTaskManager(context, listener);
        serverTaskManager.execute(PUT, task.getTaskId(), task.getContent(), Boolean.toString(task.isDone())
                , Long.toString(task.getDueDate()), task.getImageContent());
    }
    public void addTask(Task task, OnPostTaskListener listener) {
        tasks.add(task);
        dbManager.insertTask(task);
        postTaskService(task, listener);
    }
    public void updateDoneTask(Task task) {
        dbManager.updateDoneTask(task);
        ServerTaskManager serverTaskManager = new ServerTaskManager(context);
        serverTaskManager.execute(PUT, task.getTaskId(), task.getContent(), Boolean.toString(task.isDone())
                , Long.toString(task.getDueDate()), task.getImageContent());
    }
    public void deleteDoneTask(OnDeleteTaskListener listener) {
        dbManager.deleteDoneTask(tasks);
        for (Task task : tasks) {
            if (task.isDone()) {
                ServerTaskManager serverTaskManager = new ServerTaskManager(context, listener);
                serverTaskManager.execute(DELETE, task.getTaskId());
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
        ServerTaskManager serverTaskManager = new ServerTaskManager(context, listener);
        serverTaskManager.execute(GET);
    }
    private void postTaskService(Task task, final OnPostTaskListener onPostTaskListener) {
        Call<Task> postTask = apiService.postTask(task.getTaskId(), task);
        postTask.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                onPostTaskListener.onPostTask();

            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {

            }
        });
    }

}
