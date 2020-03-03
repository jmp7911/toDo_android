package com.jmp.todo.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jmp.todo.iface.OnDeleteTaskListener;
import com.jmp.todo.iface.OnPutTaskListener;
import com.jmp.todo.iface.OnSetTasksListener;
import com.jmp.todo.iface.OnPostTaskListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerTaskManager extends AsyncTask<String, String, String> {
    private Context context;
    private String requestURL;
    private String requestMethod;
    private String mJsonString;
    private ArrayList<Task> tasks;
    private OnSetTasksListener onSetTasksListener = null;
    private OnPostTaskListener onPostTaskListener = null;
    private OnPutTaskListener onPutTaskListener = null;
    private OnDeleteTaskListener onDeleteTaskListener = null;
    public static final String GET_URL = "http://todo-android-study.herokuapp.com/tasks";
    public static final String POST_URL = "http://todo-android-study.herokuapp.com/task/";
    public static final String PUT_URL = POST_URL;
    public static final String DELETE_URL = POST_URL;
    public final String TASK_ID = "id";
    public final String CONTENT = "content";
    public final String IS_DONE = "is_done";
    public final String DUE_DATE = "due_date";
    public final String IMAGE_CONTENT = "image";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String ERROR = "Error ";



    public ServerTaskManager(Context context, OnDeleteTaskListener onDeleteTaskListener) {
        this.context = context;
        this.requestURL = "";
        this.requestMethod = "";
        this.mJsonString = "";
        this.tasks = new ArrayList<>();
        this.onDeleteTaskListener = onDeleteTaskListener;
    }

    public ServerTaskManager(Context context) {
        this.context = context;
        this.requestURL = "";
        this.requestMethod = "";
        this.mJsonString = "";
        this.tasks = new ArrayList<>();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.startsWith(ERROR)) {
            mJsonString = result;
            Toast.makeText(context, mJsonString, Toast.LENGTH_SHORT).show();
        } else {
            mJsonString = result;
            if (requestMethod.equals(DELETE)) {
                if (onDeleteTaskListener != null) {
                    onDeleteTaskListener.onDeleteTask();
                }
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        Map<String, Object> task = new HashMap<>();
        switch (strings[0]) {
            case GET:
                requestURL = GET_URL;
                requestMethod = GET;
                break;
            case POST:
                requestURL = POST_URL + strings[1];
                requestMethod = POST;
                break;
            case PUT:
                requestURL = PUT_URL + strings[1];
                requestMethod = PUT;
                break;
            case DELETE:
                requestURL = DELETE_URL + strings[1];
                requestMethod = DELETE;
                break;

        }

        if (requestMethod.equals(DELETE)) {
            task.put(TASK_ID, strings[1]);
        } else if (requestMethod.equals(POST) || requestMethod.equals(PUT)) {
            task.put(TASK_ID, strings[1]);
            task.put(CONTENT, strings[2]);
            task.put(IS_DONE, Boolean.parseBoolean(strings[3]));
            task.put(DUE_DATE, Long.parseLong(strings[4]));
            task.put(IMAGE_CONTENT, strings[5]);
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(requestURL).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod(requestMethod);
            connection.setDoInput(true);
            if (requestMethod.equals(GET)) {
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();
            } else {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject taskJson = new JSONObject();
                for (Map.Entry<String, Object> entry : task.entrySet()) {
                    taskJson.put(entry.getKey(), entry.getValue());
                }
                OutputStream os = connection.getOutputStream();
                os.write(taskJson.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

            }
            if (connection.getResponseCode() == connection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString().trim();
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            return ERROR + e.getMessage();
        }
    }

    private void showTasks() {
        ArrayList<Task> tasks1 = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject task = jsonArray.getJSONObject(i);

                String taskId = task.getString(TASK_ID);
                String content = task.getString(CONTENT);
                Boolean isDone = task.getBoolean(IS_DONE);
                long dueDate = task.getLong(DUE_DATE);
                String imageContent = task.getString(IMAGE_CONTENT);

                Task task1 = new Task();
                task1.setTaskId(taskId);
                task1.setContent(content);
                task1.setIsDone(isDone);
                task1.setDueDate(dueDate);
                task1.setImageContent(imageContent);
                tasks1.add(task1);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tasks = tasks1;

    }



}