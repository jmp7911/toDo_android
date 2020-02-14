package com.jmp.todo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Task implements Parcelable {
    private String taskId;
    private String content;
    private boolean isDone;
    private int dueDateYear;
    private int dueDateMonth;
    private int dueDateDayOfMonth;
    public String getTaskId() {
        return taskId;
    }

    public Task(){

    }
    public Task(String task, boolean isDone, int dueDateYear, int dueDateMonth, int dueDateDayOfMonth) {
        this.taskId = UUID.randomUUID().toString();
        this.content = task;
        this.isDone = isDone;
        this.dueDateYear = dueDateYear;
        this.dueDateMonth = dueDateMonth;
        this.dueDateDayOfMonth = dueDateDayOfMonth;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setDueDateDayOfMonth(int dueDateDayOfMonth) {
        this.dueDateDayOfMonth = dueDateDayOfMonth;
    }

    public void setDueDateMonth(int dueDateMonth) {
        this.dueDateMonth = dueDateMonth;
    }

    public void setDueDateYear(int dueDateYear) {
        this.dueDateYear = dueDateYear;
    }

    public int getDueDateDayOfMonth() {
        return dueDateDayOfMonth;
    }

    public int getDueDateMonth() {
        return dueDateMonth;
    }

    public int getDueDateYear() {
        return dueDateYear;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    protected Task(Parcel in) {
        taskId = in.readString();
        content = in.readString();
        isDone = in.readInt() == 1;
        dueDateYear = in.readInt();
        dueDateMonth = in.readInt();
        dueDateDayOfMonth = in.readInt();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(taskId);
        parcel.writeString(content);
        parcel.writeInt(isDone ? 1 : 0);
        parcel.writeInt(dueDateYear);
        parcel.writeInt(dueDateMonth);
        parcel.writeInt(dueDateDayOfMonth);
    }
}
