package com.jmp.todo.model;

import android.os.AsyncTask;
import android.util.Log;

import com.jmp.todo.view.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetData extends AsyncTask<String, Void, String> {
    String errorString = null;
    String mJsonString;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null){
            mJsonString = result;
            showResult();

        }

    }


    @Override
    protected String doInBackground(String... params) {

        String serverURL = params[0];
        String postParameters = params[1];


        try {

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();


            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            int responseStatusCode = httpURLConnection.getResponseCode();

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }


            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();

            return sb.toString().trim();


        } catch (Exception e) {
            errorString = e.toString();
            return null;
        }

    }
    private void showResult(){
        String TAG_JSON="webnautes";
        String TAG_TASKID = "taskId";
        String TAG_CONTENT = "content";
        String TAG_ISDONE ="isDone";
        String TAG_YEAR ="dueDateYear";
        String TAG_MONTH ="dueDateMonth";
        String TAG_DAY ="dueDateDayOfMonth";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String taskId = item.getString(TAG_TASKID);
                String content = item.getString(TAG_CONTENT);
                int isDone = item.getInt(TAG_ISDONE);
                int dueDateYear = item.getInt(TAG_YEAR);
                int dueDateMonth = item.getInt(TAG_MONTH);
                int dueDateDayOfMonth = item.getInt(TAG_DAY);

                Task task = new Task();
                task.setTaskId(taskId);
                task.setContent(content);
                task.setIsDone(isDone);
                task.setDueDateYear(dueDateYear);
                task.setDueDateMonth(dueDateMonth);
                task.setDueDateDayOfMonth(dueDateDayOfMonth);

                MainActivity.taskManager.addItem(task);
                MainActivity.taskAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d("..", "showResult : ", e);
        }

    }
}
