package com.jmp.todo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ImageFileManager {
    Context context;
    public ImageFileManager(Context context) {
        this.context = context;
    }

    public String writeToInternalStorage(String imageContent) {
        String imageName = createName();
        try {
            if (isFileExist(imageContent)) {
                return imageContent;
            }
            FileOutputStream fos = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            InputStream is = context.getContentResolver().openInputStream(Uri.parse(imageContent));
            Bitmap image = BitmapFactory.decodeStream(is);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageName;
    }
    public String getPath(String imageContent) {
        try{
            File directory = context.getFilesDir();
            File image = new File(directory, imageContent);
            return image.toString();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return "null";
    }
    public void deleteDoneImage(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            if (!task.isDone()) continue;
            try{
                File directory = context.getFilesDir();
                File image = new File(directory, task.getImageContent());
                image.delete();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
    public String createName() {
        return UUID.randomUUID().toString().replace("-","") + ".jpg";
    }
    public Boolean isFileExist(String imageContent) {
        File directory = context.getFilesDir();
        File image = new File(directory, imageContent);
        if (image.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
