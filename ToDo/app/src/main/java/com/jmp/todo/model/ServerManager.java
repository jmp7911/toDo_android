package com.jmp.todo.model;

import android.content.Context;
import android.os.AsyncTask;
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

import static com.jmp.todo.model.TaskManager.DELETE_URL;
import static com.jmp.todo.model.TaskManager.GET_URL;
import static com.jmp.todo.model.TaskManager.POST_URL;
import static com.jmp.todo.model.TaskManager.PUT_URL;


public class ServerManager extends AsyncTask<String, String, String> {
    private Context context;
    private String requestURL;
    private String requestMethod;
    private String mJsonString;
    private ArrayList<Task> tasks;
    private OnSetTasksListener onSetTasksListener;
    private OnPostTaskListener onPostTaskListener;
    private OnPutTaskListener onPutTaskListener;
    private OnDeleteTaskListener onDeleteTaskListener;
    public final String TASK_ID = "id";
    public final String CONTENT = "content";
    public final String IS_DONE = "is_done";
    public final String DUE_DATE = "due_date";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String ERROR = "Error";

    public ServerManager(Context context, OnPostTaskListener onPostTaskListener) {
        this.context = context;
        this.requestURL = "";
        this.requestMethod = "";
        this.mJsonString = "";
        this.tasks = new ArrayList<>();
        this.onPostTaskListener = onPostTaskListener;
    }

    public ServerManager(Context context, OnSetTasksListener onSetTasksListener) {
        this.context = context;
        requestURL = "";
        requestMethod = "";
        mJsonString = "";
        tasks = new ArrayList<>();
        this.onSetTasksListener = onSetTasksListener;
    }

    public ServerManager(Context context, OnPutTaskListener onPutTaskListener) {
        this.context = context;
        this.requestURL = "";
        this.requestMethod = "";
        this.mJsonString = "";
        this.tasks = new ArrayList<>();
        this.onPutTaskListener = onPutTaskListener;
    }

    public ServerManager(Context context, OnDeleteTaskListener onDeleteTaskListener) {
        this.context = context;
        this.requestURL = "";
        this.requestMethod = "";
        this.mJsonString = "";
        this.tasks = new ArrayList<>();
        this.onDeleteTaskListener = onDeleteTaskListener;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.startsWith(ERROR)) {
            mJsonString = result;
            Toast.makeText(context, mJsonString, Toast.LENGTH_SHORT).show();
        } else {
            mJsonString = result;
            if (requestMethod.equals(GET)) {
                showTasks();
                onSetTasksListener.onSetTasks(tasks);
            } else if (requestMethod.equals(POST)) {
                onPostTaskListener.onPostTask();
            } else if (requestMethod.equals(PUT)) {
                onPutTaskListener.onPutTask();
            } else if (requestMethod.equals(DELETE)) {
                onDeleteTaskListener.onDeleteTask();
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
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(requestURL).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod(requestMethod);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            if (requestMethod.equals(GET)) {
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
            return "Error " + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();
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

                Task task1 = new Task();
                task1.setTaskId(taskId);
                task1.setContent(content);
                task1.setIsDone(isDone);
                task1.setDueDate(dueDate);
                tasks1.add(task1);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tasks = tasks1;

    }



}