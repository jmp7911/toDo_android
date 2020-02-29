package com.jmp.todo.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.jmp.todo.model.ServerTaskManager.DELETE;
import static com.jmp.todo.model.ServerTaskManager.ERROR;
import static com.jmp.todo.model.ServerTaskManager.GET;
import static com.jmp.todo.model.ServerTaskManager.POST;
import static com.jmp.todo.model.ServerTaskManager.PUT;

public class ServerImageManager extends AsyncTask<String, Void, String> {
    private Context context;
    private String requestURL;
    private String requestMethod;
    public static final String UPLOAD_IMAGE_URL = "http://todo-android-study.herokuapp.com/upload";
    public final String UPLOAD = "upload";
    public final String FILE_NAME = "/Users/nathanpark/Desktop/";

    public ServerImageManager(Context context) {
        this.context = context;
        requestMethod = "";
        requestURL = "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.startsWith(ERROR)) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        } else {
            if (requestMethod.equals(GET)) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            } else if (requestMethod.equals(POST)) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            } else if (requestMethod.equals(PUT)) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            } else if (requestMethod.equals(DELETE)) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        Map<String, Object> task = new HashMap<>();
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String lineEnd = "\r\n";
        String twoHypens = "--";
        switch (strings[0]) {
//            case GET:
//                requestURL = GET_URL;
//                requestMethod = GET;
//                break;
            case POST:
                requestURL = UPLOAD_IMAGE_URL;
                requestMethod = POST;
                break;
//            case PUT:
//                requestURL = PUT_URL + strings[1];
//                requestMethod = PUT;
//                break;
//            case DELETE:
//                requestURL = DELETE_URL + strings[1];
//                requestMethod = DELETE;
//                break;
        }
//        if (requestMethod.equals(GET)) {
//            task.put(UPLOAD, strings[1]);
//        } else if (requestMethod.equals(DELETE)) {
//            task.put(TASK_ID, strings[1]);} else
            if (requestMethod.equals(POST) || requestMethod.equals(PUT)) {
            task.put(UPLOAD, strings[1]);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(requestURL).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod(POST);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            byte[] buffer;
            int maxBufferSize = 5 * 1024 * 1024;
            String fileName = FILE_NAME + task.get(UPLOAD);
            String delimiter = twoHypens + boundary + lineEnd;
            StringBuilder postDataBuilder = new StringBuilder();
            postDataBuilder.append(delimiter);
            if (!task.get(UPLOAD).toString().equals("null")) {
                ImageFileManager imageFileManager = new ImageFileManager(context);
                String filePath = imageFileManager.getPathFromInternalStorage(task.get(UPLOAD).toString());
                File taskImage = new File(filePath);
                postDataBuilder.append("Content-Disposition: form-data; name=\"" + UPLOAD + "\"; filename=\""
                        + fileName + "\"" + lineEnd);
                DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
                ds.write(postDataBuilder.toString().getBytes());

                ds.writeBytes(lineEnd);
                FileInputStream fis = new FileInputStream(taskImage);
                buffer = new byte[maxBufferSize];
                int length = -1;
                while ((length = fis.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(lineEnd);
                ds.writeBytes(lineEnd);
                ds.writeBytes(twoHypens + boundary + twoHypens + lineEnd);
                fis.close();
                ds.flush();
                ds.close();
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
        }

    }
}