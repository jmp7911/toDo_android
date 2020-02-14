package com.jmp.todo.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmp.todo.R;
import com.jmp.todo.iface.OnCheckListener;
import com.jmp.todo.iface.OnItemClickListener;
import com.jmp.todo.model.DbHelper;
import com.jmp.todo.model.DeleteData;
import com.jmp.todo.model.GetData;
import com.jmp.todo.model.InsertData;
import com.jmp.todo.model.Task;
import com.jmp.todo.model.TaskManager;
import com.jmp.todo.model.UpdateData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public static String IP_ADDRESS = "10.0.2.2";
    public static TaskManager taskManager = new TaskManager();
    public static TaskAdapter taskAdapter = new TaskAdapter();
    final int REQUEST_CODE_UPDATE = 1001;
    final int REQUEST_CODE_ADD = 1002;
    public static final String EXTRA_TASK = "taskItem";
    public static final String EXTRA_POSITION = "position";
    public static final int NO_EXTRA_DATA = -1;
    final String USER_FILE = "task.png";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(EXTRA_POSITION, NO_EXTRA_DATA);
                Task item = data.getParcelableExtra(EXTRA_TASK);
                taskManager.setItem(item, position);
                taskAdapter.notifyItemChanged(position);

                DbHelper dbHelper = new DbHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE task SET content = ?, dueDateYear = ?, dueDateMonth = ?" +
                        ", dueDateDayOfMonth = ? WHERE taskId = ?", new Object[]{
                        item.getContent(), item.getDueDateYear(), item.getDueDateMonth()
                        , item.getDueDateDayOfMonth(), item.getTaskId()
                });
                db.close();
                UpdateData updateData = new UpdateData();
                updateData.execute("http://" + IP_ADDRESS + "/update.php", item.getTaskId(), item.getContent()
                        , Integer.toString(item.getDueDateYear()), Integer.toString(item.getDueDateMonth())
                        , Integer.toString(item.getDueDateDayOfMonth()));


            } else {
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_ADD) {
            if (resultCode == RESULT_OK) {
                Task item = data.getParcelableExtra(EXTRA_TASK);
                taskManager.addItem(item);
                taskAdapter.notifyItemInserted(taskManager.getItemList().size());

                DbHelper dbHelper = new DbHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO task VALUES(?,?,?,?,?,?)"
                        , new Object[]{
                                item.getTaskId(), item.getContent(), item.isDone(), item.getDueDateYear()
                                , item.getDueDateMonth(), item.getDueDateDayOfMonth()
                        });
                db.close();
                InsertData insertData = new InsertData(this);
                insertData.execute("http://" + IP_ADDRESS + "/insert.php", item.getTaskId(), item.getContent()
                        , Boolean.toString(item.isDone()), Integer.toString(item.getDueDateYear())
                        , Integer.toString(item.getDueDateMonth())
                        , Integer.toString(item.getDueDateDayOfMonth()));
            } else {
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM task", null);
        while (cursor.moveToNext()) {
            Task item = new Task();
            item.setTaskId(cursor.getString(0));
            item.setContent(cursor.getString(1));
            item.setIsDone(cursor.getInt(2) == 1);
            item.setDueDateYear(cursor.getInt(3));
            item.setDueDateMonth(cursor.getInt(4));
            item.setDueDateDayOfMonth(cursor.getInt(5));

            taskManager.addItem(item);
        }
        db.close();
        GetData getData = new GetData();
        getData.execute("http://" + IP_ADDRESS + "/getJson.php", "");
        RecyclerView recyclerView = findViewById(R.id.container);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(taskAdapter);
        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Task task = taskManager.getItem(pos);
                Intent intent = new Intent(getApplicationContext(), TaskEditorActivity.class);
                intent.putExtra(EXTRA_TASK, task);
                intent.putExtra(EXTRA_POSITION, pos);
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            }
        });
        taskAdapter.setOnCheckListener(new OnCheckListener() {
            @Override
            public void onCheckDone(Boolean isDone, String taskId) {
                DbHelper dbHelper = new DbHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE task SET isDone = ? WHERE taskId = ?", new Object[]{
                        isDone, taskId
                });
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TaskEditorActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }
        });
        Button deleteButton = findViewById(R.id.delete_item);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = taskManager.getItemList().size();
                while (index-- > 0) {
                    if (taskManager.getItem(index).isDone()) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("DELETE FROM task WHERE taskId = ?", new String[]{
                                taskManager.getItem(index).getTaskId()
                        });
                        DeleteData deleteData = new DeleteData();
                        deleteData.execute("http://" + IP_ADDRESS + "/delete.php", taskManager.getItem(index).getTaskId());
                        taskManager.getItemList().remove(index);
                        taskAdapter.notifyItemRemoved(index);
                    }
                }

            }
        });
        Button readButton = findViewById(R.id.read);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File directory = getFilesDir();
                File mypath = new File(directory, USER_FILE);
                ImageView imageTest = findViewById(R.id.image_test);
                imageTest.setImageDrawable(Drawable.createFromPath(mypath.toString()));
            }
        });

        Button writeButton = findViewById(R.id.write);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream fos = null;
                    fos = openFileOutput(USER_FILE, Context.MODE_PRIVATE);
                    Drawable drawable = getDrawable(R.drawable.item_image);
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
