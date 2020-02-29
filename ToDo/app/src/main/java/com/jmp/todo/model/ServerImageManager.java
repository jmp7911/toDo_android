package com.jmp.todo.model;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.jmp.todo.model.ServerTaskManager.DELETE;
import static com.jmp.todo.model.ServerTaskManager.ERROR;
import static com.jmp.todo.model.ServerTaskManager.GET;
import static com.jmp.todo.model.ServerTaskManager.GET_URL;
import static com.jmp.todo.model.ServerTaskManager.POST;
import static com.jmp.todo.model.ServerTaskManager.PUT;

public class ServerImageManager extends AsyncTask<String, Void, String> {
    private Context context;
    private String requestURL;
    private String requestMethod;
    private final String UPLOAD_IMAGE_URL = "http://todo-android-study.herokuapp.com/upload";
    private final String GET_IMAGE_URL = "http://todo-android-study.herokuapp.com/images/";

    public final String UPLOAD = "upload";
    public final String DOWNLOAD = "download";
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
                Log.d("uploadmessage", "onPostExecute: "+result);
            } else if (requestMethod.equals(POST)) {
                Log.d("uploadmessage", "onPostExecute: "+result);
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        Map<String, Object> task = new HashMap<>();
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String lineEnd = "\r\n";
        String twoHypens = "--";
        String internalStoragePath = context.getFilesDir().toString();
        switch (strings[0]) {
            case GET:
                requestURL = GET_IMAGE_URL + strings[1];
                requestMethod = GET;
                task.put(DOWNLOAD, strings[1]);
                break;
            case POST:
                requestURL = UPLOAD_IMAGE_URL;
                requestMethod = POST;
                task.put(UPLOAD, strings[1]);
                break;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(requestURL).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod(requestMethod);
            connection.setDoInput(true);
            if (requestMethod.equals(GET)) {
                connection.connect();
            } else {
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
            }

            if (connection.getResponseCode() == connection.HTTP_OK) {
                if (requestMethod.equals(GET)) {
                    File image = new File(internalStoragePath, task.get(DOWNLOAD).toString());
                    InputStream is = connection.getInputStream();
                    OutputStream outStream = new FileOutputStream(image);

                    byte[] buf = new byte[1024];
                    int len = 0;

                    while ((len = is.read(buf)) > 0) {
                        outStream.write(buf, 0, len);
                    }
                    outStream.close();
                    is.close();
                    connection.disconnect();

                    return connection.getResponseMessage();
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString().trim();
                }

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