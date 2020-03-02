package com.jmp.todo.iface;

import com.jmp.todo.model.ResponseTask;
import com.jmp.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {

    @GET("/tasks")
    Call<ArrayList<ResponseTask>> getTasks();
    @POST("/task/{task-id}")
    Call<ResponseTask> postTask(@Path("task-id") String taskId, @Body Task task);
    @PUT("/task/{task-id}")
    Call<ResponseTask> putTask(@Path("task-id") String taskId);
    @DELETE("/task/{task-id}")
    Call<ResponseTask> deleteTask(@Path("task-id") String taskId);
    @GET("/images/{filename}")
    Call<ResponseTask> getImage(@Path("filename") String imageContent);
    @POST("/upload")
    Call<ResponseTask> postImage();
}
