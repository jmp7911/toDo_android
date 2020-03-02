package com.jmp.todo.model;

import com.google.gson.annotations.SerializedName;

public class ResponseTask {
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

    public String getContent() {
        return content;
    }

    public boolean isDone() {
        return isDone;
    }

    public long getDueDate() {
        return dueDate;
    }

    public String getImageContent() {
        return imageContent;
    }
}
