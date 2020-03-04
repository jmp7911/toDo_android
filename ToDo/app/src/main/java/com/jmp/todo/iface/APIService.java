package com.jmp.todo.iface;

import com.jmp.todo.model.Task;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {

    @GET("/tasks")
    Call<ArrayList<Task>> getTasks();
    @POST("/task/{task-id}")
    Call<Task> postTask(@Path("task-id") String taskId, @Body Task task);
    @PUT("/task/{task-id}")
    Call<Task> putTask(@Path("task-id") String taskId, @Body Task task);
    @DELETE("/task/{task-id}")
    Call<Task> deleteDoneTask(@Path("task-id") String taskId);
    @GET("/images/{filename}")
    Call<ResponseBody> getImage(@Path("filename") String imageContent);
    @Multipart
    @POST("/upload")
    Call<String> postImage(@Part MultipartBody.Part image);
}
