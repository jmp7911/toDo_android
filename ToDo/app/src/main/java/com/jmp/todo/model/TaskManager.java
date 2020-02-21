package com.jmp.todo.model;

import java.util.ArrayList;


public class TaskManager {
    private ArrayList<Task> tasks;

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public TaskManager(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
    public Task getTask(int pos) {
       return tasks.get(pos);

    }
    public void setTask(Task task, int pos) {
        tasks.set(pos, task);
    }
    public void addTask(Task task) {
        tasks.add(task);
    }
    public void deleteTask(int pos) {
        tasks.remove(pos);
    }
}
