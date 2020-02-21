package com.jmp.todo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;


public class TaskManager {
    private Context context;
    private DbManager dbManager;
    private ArrayList<Task> tasks;

    public ArrayList<Task> getTasks() {
        return tasks;
    }
    public class DbManager {
        private DbHelper dbHelper;
        private SQLiteDatabase db;

        public DbManager () {
            this.dbHelper = new DbHelper(context);
            this.db = dbHelper.getWritableDatabase();
        }

        public void insertTask(Task task) {
            db.execSQL("INSERT INTO task VALUES(?,?,?,?,?)"
                    , new Object[]{
                            task.getTaskId(), task.getContent(), task.isDone(), task.getDueDate(),
                            task.getImageContent()
                    });
        }
        public void updateTask(Task task) {
            db.execSQL("UPDATE task SET content = ?, dueDate = ?, imageContent = ? WHERE taskId = ?", new Object[]{
                    task.getContent(), task.getDueDate(), task.getImageContent(),task.getTaskId()
            });
        }
        public ArrayList<Task> selectTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM task", null);
            while (cursor.moveToNext()) {
                Task item = new Task();
                item.setTaskId(cursor.getString(0));
                item.setContent(cursor.getString(1));
                item.setIsDone(cursor.getInt(2) == 1);
                item.setDueDate(cursor.getLong(3));
                item.setImageContent(cursor.getString(4));
                tasks.add(item);
            }
            return tasks;
        }
        public void deleteDoneTask(ArrayList<Task> tasks) {
            int pos = tasks.size();
            while (pos-- > 0) {
                if (tasks.get(pos).isDone()) {
                    db.execSQL("DELETE FROM task WHERE taskId = ?", new String[]{
                            tasks.get(pos).getTaskId()
                    });
                }
            }
        }
        public void updateDoneTask(Task task) {
            db.execSQL("UPDATE task SET isDone = ? WHERE taskId = ?", new Object[]{
                    task.isDone(), task.getTaskId()
            });
        }
    }

    public TaskManager(Context context) {
        this.context = context;
        this.dbManager = new DbManager();
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
