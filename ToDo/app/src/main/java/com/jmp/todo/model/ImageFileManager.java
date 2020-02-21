package com.jmp.todo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageFileManager {
    Context context;
    public ImageFileManager(Context context) {
        this.context = context;
    }

    public void writeToInternalStorage(Task task) {
        try {
            if (fileExist(task)) {
                return;
            }
            FileOutputStream fos = null;
            fos = context.openFileOutput(getName(task.getImageContent()), Context.MODE_PRIVATE);
            InputStream is = context.getContentResolver().openInputStream(Uri.parse(task.getImageContent()));
            Bitmap image = BitmapFactory.decodeStream(is);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getPath(Task task) {
        try{
            File directory = context.getFilesDir();
            File image = new File(directory, getName(task.getImageContent()));
            return image.toString();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return "null";
    }
    public void deleteImage(Task task) {
        try{
            File directory = context.getFilesDir();
            File image = new File(directory, getName(task.getImageContent()));
            image.delete();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public String getName (String imageContent) {
        String name = imageContent.replace(":","");
        name = name.replace("/","");
        name = name.replace(".","") + ".jpg";
        return name;
    }
    public Boolean fileExist(Task task) {
        File directory = context.getFilesDir();
        File image = new File(directory, getName(task.getImageContent()));
        if (image.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
