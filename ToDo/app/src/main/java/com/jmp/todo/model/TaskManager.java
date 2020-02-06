package com.jmp.todo.model;

import java.util.ArrayList;


public class TaskManager {
    private ArrayList<Task> itemList = new ArrayList<>();

    public ArrayList<Task> getItemList() {
        return itemList;
    }

    public TaskManager() {
    }
    public Task getItem(int pos) {
        if (pos < 0) {

        }
        return itemList.get(pos);
    }
    public void setItem(Task item, int pos) {
        itemList.set(pos, item);
    }
    public void addItem(Task item) {
        itemList.add(item);
    }
}
