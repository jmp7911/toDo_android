package com.jmp.todo.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class InsertData extends AsyncTask<String, Void, String> {
    private ProgressDialog progressDialog;
    private Context context;
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();

    }

    public InsertData(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Please Wait", null, true, true);
    }

    @Override
    protected String doInBackground(String... strings) {
        String taskId = strings[1];
        String content = strings[2];
        int isDone = Integer.parseInt(strings[3]);
        int dueDateYear = Integer.parseInt(strings[4]);
        int dueDateMonth = Integer.parseInt(strings[5]);
        int dueDateDayOfMonth = Integer.parseInt(strings[6]);

        String serverURL = strings[0];
        String postParameters = "taskId=" + taskId + "&content=" + content + "&isDone=" + isDone
                + "&dueDateYear=" + dueDateYear + "&dueDateMonth=" + dueDateMonth + "&dueDateDayOfMonth=" + dueDateDayOfMonth;

        try {
            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
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
            String line = null;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }


            bufferedReader.close();


            return sb.toString();
        } catch (Exception e) {
            Log.d("..", "InsertData: Error ", e);

            return new String("Error: " + e.getMessage());
        }
    }
}
