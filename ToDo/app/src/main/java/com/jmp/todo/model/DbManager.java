package com.jmp.todo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;

public class DbManager {
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbManager (Context context) {
        this.dbHelper = new DbHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public void insertTask(Task task) {
        db.execSQL("INSERT INTO task VALUES(?,?,?,?,?)"
                , new Object[]{
                        task.getTaskId(), task.getContent(), task.isDone(), task.getDueDate().getTime(),
                        task.getImageContent()
                });
    }
    public void updateTask(Task task) {
        db.execSQL("UPDATE task SET content = ?, dueDate = ?, imageContent = ? WHERE taskId = ?", new Object[]{
                task.getContent(), task.getDueDate().getTime(), task.getImageContent(),task.getTaskId()
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
            item.setDueDate(new Timestamp(cursor.getLong(3)));
            item.setImageContent(cursor.getString(4));
            tasks.add(item);
        }
        return tasks;
    }
    public void deleteTask(ArrayList<Task> tasks) {
        int pos = tasks.size();
        while (pos-- > 0) {
            if (tasks.get(pos).isDone()) {
                db.execSQL("DELETE FROM task WHERE taskId = ?", new String[]{
                        tasks.get(pos).getTaskId()
                });
            }
        }
    }
    public void updateDone(Boolean isDone, String taskId) {
        db.execSQL("UPDATE task SET isDone = ? WHERE taskId = ?", new Object[]{
                isDone, taskId
        });
    }
}
