package com.jmp.todo.iface;

import com.jmp.todo.model.Task;

import java.util.ArrayList;

public interface OnSetTasksListener {
    void onSetTasks(ArrayList<Task> tasks);

}
