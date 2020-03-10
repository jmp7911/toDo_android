package com.jmp.todo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.jmp.todo.R;
import com.jmp.todo.iface.APIService;
import com.jmp.todo.iface.OnDeleteTaskListener;
import com.jmp.todo.iface.OnImagePostExecuteListener;
import com.jmp.todo.iface.OnPutTaskListener;
import com.jmp.todo.iface.OnSetTasksListener;
import com.jmp.todo.iface.OnPostTaskListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TaskManager {
    private Context context;
    private DbManager dbManager;
    private ImageFileManager imageFileManager;
    private ArrayList<Task> tasks;
    private Retrofit retrofit;
    private APIService apiService;
    private final String BASE_URL = "http://todo-android-study.herokuapp.com";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public final String UPLOAD = "upload";

    public TaskManager(Context context) {
        this.context = context;
        this.dbManager = new DbManager(context);
        this.imageFileManager = new ImageFileManager(context);
        this.tasks = new ArrayList<>();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.apiService = retrofit.create(APIService.class);
    }
    public ArrayList<Task> getTasks() {
        return tasks;
    }
    public Task getTask(int pos) {
       if (tasks.size() <= pos) {
           return null;
       }
       return tasks.get(pos);
    }
    public void setTask(Task task, int pos, OnPutTaskListener listener) {
        if (tasks.size() <= pos) {
            return;
        }
        tasks.set(pos, task);
        dbManager.updateTask(task);
        PUTTaskService(task, listener);
    }
    public void addTask(Task task, OnPostTaskListener listener) {
        tasks.add(task);
        dbManager.insertTask(task);
        POSTTaskService(task, listener);
    }

    public void updateDoneTask(Task task) {
        dbManager.updateDoneTask(task);
        PUTTaskService(task);
    }
    public void deleteDoneTask(OnDeleteTaskListener listener) {
        dbManager.deleteDoneTask(tasks);
        for (Task task : tasks) {
            if (task.isDone()) {
                DELETETaskService(task.getTaskId(), listener);
            }
        }
        for (Iterator<Task> task = tasks.iterator(); task.hasNext();) {
            if (task.next().isDone()) {
                task.remove();
            }
        }

    }
    public void getTasksFromDB() {
        tasks = dbManager.selectTasks();

    }

    public void POSTImageService(final Task task, final OnImagePostExecuteListener onImagePostExecuteListener) {
        File image = new File(context.getFilesDir(), task.getImageContent());
        final String FILE_NAME = "/Users/nathanpark/Desktop/" + image.getName();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload",FILE_NAME, RequestBody.create(MultipartBody.FORM, image))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/upload")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try {
                    JSONObject fileName = new JSONObject(response.body().string());
                    onImagePostExecuteListener.onPostExecute(fileName.getString("filename"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void POSTTaskService(final Task task, final OnPostTaskListener onPostTaskListener) {
        Call<Task> postTask = apiService.postTask(task.getTaskId(), task);
        postTask.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                onPostTaskListener.onPostTask();
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void GETTasksService(final OnSetTasksListener onSetTasksListener) {
        Call<ArrayList<Task>> getTasks = apiService.getTasks();
        getTasks.enqueue(new Callback<ArrayList<Task>>() {
            @Override
            public void onResponse(Call<ArrayList<Task>> call, Response<ArrayList<Task>> response) {
                tasks = response.body();
                onSetTasksListener.onSetTasks();
                for (Task task : tasks) {
                    if (!task.getImageContent().equals("null")){
                        ServerImageManager serverImageManager = new ServerImageManager(context);
                        serverImageManager.execute(GET, task.getImageContent());
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Task>> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void PUTTaskService(Task task, final OnPutTaskListener onPutTaskListener) {
        Call<Task> putTask = apiService.putTask(task.getTaskId(), task);
        putTask.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                onPutTaskListener.onPutTask();
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void DELETETaskService(String taskId, final OnDeleteTaskListener onDeleteTaskListener) {
        Call<Task> deleteDoneTask = apiService.deleteDoneTask(taskId);
        deleteDoneTask.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                onDeleteTaskListener.onDeleteTask();
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {

            }
        });
    }

    private void PUTTaskService(Task task) {
        Call<Task> putTask = apiService.putTask(task.getTaskId(), task);
        putTask.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {

            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
