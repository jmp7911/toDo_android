package com.jmp.todo.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jmp.todo.iface.OnSetTasksListener;
import com.jmp.todo.iface.OnTaskChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.jmp.todo.model.TaskManager.GET_URL;
import static com.jmp.todo.model.TaskManager.POST_URL;


public class ServerManager extends AsyncTask<String, String, String> {
    private Context context;
    private String requestURL;
    private String requestMethod;
    private String mJsonString;
    private ArrayList<Task> tasks;
    private OnSetTasksListener onSetTasksListener;
    private OnTaskChangedListener onTaskChangedListener;
    public final String TASK_ID = "id";
    public final String CONTENT = "content";
    public final String IS_DONE = "is_done";
    public final String DUE_DATE = "due_date";
    public static final String POST = "POST";
    public static final String GET = "GET";

    public ServerManager(Context context, OnTaskChangedListener onTaskChangedListener) {
        this.context = context;
        this.requestURL = "";
        this.requestMethod = "";
        this.mJsonString = "";
        this.tasks = new ArrayList<>();
        this.onTaskChangedListener = onTaskChangedListener;
    }

    public ServerManager(Context context, OnSetTasksListener onSetTasksListener) {
        this.context = context;
        requestURL = "";
        requestMethod = "";
        mJsonString = "";
        tasks = new ArrayList<>();
        this.onSetTasksListener = onSetTasksListener;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.startsWith("Error")) {
            mJsonString = result;
            Toast.makeText(context, mJsonString, Toast.LENGTH_SHORT).show();
        } else {
            mJsonString = result;
            if (requestMethod.equals("GET")) {
                showTasks();
                onSetTasksListener.onSetTasks(tasks);
            } else if (requestMethod.equals("POST")) {
                onTaskChangedListener.onPostTask();
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (requestMethod.equals(POST)) {
            Task task = new Task();
            task.setTaskId(values[0]);
            task.setContent(values[1]);
            task.setIsDone(Boolean.parseBoolean(values[2]));
            task.setDueDate(Long.parseLong(values[3]));
            tasks.add(task);
        }
    }


    @Override
    protected String doInBackground(String... strings) {
        Map<String, Object> task = new HashMap<>();
        if (strings[0].equals(POST_URL)) {
            requestURL = POST_URL + strings[1];
            requestMethod = POST;
            task.put(TASK_ID, strings[1]);
            task.put(CONTENT, strings[2]);
            task.put(IS_DONE, Boolean.parseBoolean(strings[3]));
            task.put(DUE_DATE, Long.parseLong(strings[4]));

        } else if (strings[0].equals(GET_URL)) {
            requestURL = GET_URL;
            requestMethod = GET;
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
            } else if (requestMethod.equals(POST)) {
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

                publishProgress(strings[1], strings[2], strings[3], strings[4]);
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