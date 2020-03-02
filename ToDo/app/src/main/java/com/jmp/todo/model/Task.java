package com.jmp.todo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.UUID;

public class Task implements Parcelable {
    @SerializedName("id")
    private String taskId;
    @SerializedName("content")
    private String content;
    @SerializedName("is_done")
    private boolean isDone;
    @SerializedName("due_date")
    private long dueDate;
    @SerializedName("image")
    private String imageContent;

    public String getTaskId() {
        return taskId;
    }

    public Task(){
        this.taskId = "";
        this.content = "";
        this.isDone = false;
        this.dueDate = System.currentTimeMillis();
        this.imageContent = "null";
    }
    public Task(String task, boolean isDone, long dueDate, String imageContent) {
        this.taskId = UUID.randomUUID().toString();
        this.content = task;
        this.isDone = isDone;
        this.dueDate = dueDate;
        this.imageContent = imageContent;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
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

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }

    protected Task(Parcel in) {
        taskId = in.readString();
        content = in.readString();
        isDone = in.readInt() == 1;
        dueDate = in.readLong();
        imageContent = in.readString();
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
        parcel.writeLong(dueDate);
        parcel.writeString(imageContent);
    }
}
