package com.jmp.todo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jmp.todo.iface.OnSetTasksListener;

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
import java.util.Iterator;
import java.util.Map;

public class TaskManager {
    private ArrayList<Task> tasks;
    private Context context;
    private DbManager dbManager;
    private String mJsonString;
    private OnSetTasksListener onSetTasksListener = null;
    public static final String BASE_URL = "http://todo-android-study.herokuapp.com";
    public static final String POST_URL = "http://todo-android-study.herokuapp.com/task";
    public static final String GET_URL = "http://todo-android-study.herokuapp.com/tasks";
    public final String TASK_ID = "id";
    public final String CONTENT = "content";
    public final String IS_DONE = "is_done";
    public final String DUE_DATE = "due_date";
    public final String POST = "POST";
    public final String GET = "GET";
    public class DbManager {
        DbHelper dbHelper;
        SQLiteDatabase db;
        private DbManager() {
            this.dbHelper = new DbHelper(context);
            this.db = dbHelper.getWritableDatabase();
        }

        private void insertTask(Task task) {
            db.execSQL("INSERT INTO task VALUES(?,?,?,?,?)"
                    , new Object[]{
                            task.getTaskId(), task.getContent(), task.isDone(), task.getDueDate(),
                            task.getImageContent()
                    });
        }
        private void updateTask(Task task) {
            db.execSQL("UPDATE task SET content = ?, dueDate = ?, imageContent = ? WHERE taskId = ?", new Object[]{
                    task.getContent(), task.getDueDate(), task.getImageContent(),task.getTaskId()
            });
        }
        private ArrayList<Task> selectTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM task", null);
            while (cursor.moveToNext()) {
                Task item = new Task();
                item.setTaskId(cursor.getString(0));
                item.setContent(cursor.getString(1));
                item.setIsDone(cursor.getInt(2) == 1);
                item.setDueDate(cursor.getLong(3));
                item.setImageContent(cursor.getString(4));
                tasks.add(item);
            }
            return tasks;
        }
        private void deleteDoneTask() {
            for (Task task : tasks) {
                if (task.isDone()) {
                    db.execSQL("DELETE FROM task WHERE taskId = ?", new String[]{
                            task.getTaskId()
                    });
                }
            }
        }
        private void updateDoneTask(Task task) {
            db.execSQL("UPDATE task SET isDone = ? WHERE taskId = ?", new Object[]{
                    task.isDone(), task.getTaskId()
            });
        }
    }
    public class ServerManager extends AsyncTask<String,String,String> {
        String requestURL;
        String requestMethod;
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("tasktest", "onPostExecute: "+result);
            if (result.startsWith("Error")) {
                mJsonString = result;
                Toast.makeText(context, mJsonString, Toast.LENGTH_SHORT).show();
                onSetTasksListener.onSetTasks(tasks);
            }  else {
                mJsonString = result;
                if (requestMethod.equals("GET")) {
                    showTasks();
                    onSetTasksListener.onSetTasks(tasks);
                    mJsonString = null;
                } else if (requestMethod.equals("POST")) {

                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            Map<String, Object> task = new HashMap<>();
            if (strings[0].equals(POST_URL)) {
                requestURL = POST_URL + task.get(TASK_ID);
                requestMethod = POST;
                task.put(TASK_ID, strings[1]);
                task.put(CONTENT, strings[2]);
                task.put(IS_DONE, Boolean.parseBoolean(strings[3]));
                task.put(DUE_DATE, Long.parseLong(strings[4]));

            } else if (strings[0].equals(GET_URL)) {
                requestURL = GET_URL;
                requestMethod = GET;
            }

            try{
                HttpURLConnection connection = (HttpURLConnection)new URL(requestURL).openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod(requestMethod);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                JSONObject taskJson = new JSONObject();
                for (Map.Entry<String,Object> entry : task.entrySet()) {
                    taskJson.put(entry.getKey(), entry.getValue());
                }
//                OutputStream os = connection.getOutputStream();
//                os.write(taskJson.toString().getBytes(StandardCharsets.UTF_8));
//
//                StringBuilder postTaskId = new StringBuilder();
//                if (requestMethod.equals("POST")) {
//                    postTaskId.append(task.get(TASK_ID));
//                } else if (requestURL.equals(GET_URL)) {
//
//                }
//                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
//                wr.write(postTaskId.toString().trim());
//                wr.flush();
//                wr.close();

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
                    return "Error " + connection.getResponseMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public TaskManager(Context context) {
        this.context = context;
        this.tasks = new ArrayList<>();
        this.dbManager = new DbManager();

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
    public void setTask(Task task, int pos) {
        if (tasks.size() <= pos) {
            return;
        }
        tasks.set(pos, task);
        dbManager.updateTask(task);
    }
    public void addTask(Task task) {
        tasks.add(task);
        dbManager.insertTask(task);
        ServerManager serverManager = new ServerManager();
        serverManager.execute(POST_URL, task.getTaskId(), task.getContent(), Boolean.toString(task.isDone())
                , Long.toString(task.getDueDate()));
    }
    public void deleteDoneTask() {
        dbManager.deleteDoneTask();
        for (Iterator<Task> task = tasks.iterator(); task.hasNext();) {
            if (task.next().isDone()) {
                task.remove();
            }
        }
    }
    public void updateDoneTask(Task task) {
        dbManager.updateDoneTask(task);
    }
    public void getTasksFromDB(OnSetTasksListener listener) {
        onSetTasksListener = listener;
        tasks = dbManager.selectTasks();
        onSetTasksListener.onSetTasks(tasks);
    }
    public void getTasksFromServer(OnSetTasksListener listener) {
        onSetTasksListener = listener;
        ServerManager serverManager = new ServerManager();
        serverManager.execute(GET_URL);
    }

    private void showTasks() {
        ArrayList<Task> tasks1 = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);
            for(int i = 0; i < jsonArray.length(); i++) {
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
