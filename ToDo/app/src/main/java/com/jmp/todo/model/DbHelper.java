package com.jmp.todo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, "task", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String taskSQL = "create table task (" +
                "taskId, " +
                "content, " +
                "isDone, " +
                "dueDate, " +
                "imageContent)";
        sqLiteDatabase.execSQL(taskSQL);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion == DATABASE_VERSION) {
            sqLiteDatabase.execSQL("drop table task");
            onCreate(sqLiteDatabase);
        }
    }
}
