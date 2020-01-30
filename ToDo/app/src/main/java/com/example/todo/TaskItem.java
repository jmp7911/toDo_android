package com.example.todo;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskItem implements Parcelable {
    String task;
    int isDone;
    public TaskItem(String task, int isDone) {
        this.task = task;
        this.isDone = isDone;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setDone(int done) {
        isDone = done;
    }

    public int getDone() {
        return isDone;
    }

    protected TaskItem(Parcel in) {
        task = in.readString();
        isDone = in.readInt();
    }

    public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
        @Override
        public TaskItem createFromParcel(Parcel in) {
            return new TaskItem(in);
        }

        @Override
        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };

    public String getTask() {
        return task;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(task);
        parcel.writeInt(isDone);
    }
}
