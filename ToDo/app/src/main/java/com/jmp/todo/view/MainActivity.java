package com.jmp.todo.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmp.todo.R;
import com.jmp.todo.iface.OnCheckDoneListener;
import com.jmp.todo.iface.OnDeleteTaskListener;
import com.jmp.todo.iface.OnImagePostExecuteListener;
import com.jmp.todo.iface.OnItemClickListener;
import com.jmp.todo.iface.OnPutTaskListener;
import com.jmp.todo.iface.OnSetTasksListener;
import com.jmp.todo.iface.OnPostTaskListener;
import com.jmp.todo.model.ImageFileManager;
import com.jmp.todo.model.ServerImageManager;
import com.jmp.todo.model.Task;
import com.jmp.todo.model.TaskManager;

import static com.jmp.todo.model.TaskManager.POST;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskManager taskManager;
    private TaskAdapter taskAdapter;
    private ImageFileManager fileManager;
    final int REQUEST_CODE_UPDATE = 1001;
    final int REQUEST_CODE_ADD = 1002;
    public static final String EXTRA_TASK = "taskItem";
    public static final String EXTRA_POSITION = "position";
    public static final int NO_EXTRA_DATA = -1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode == RESULT_OK) {
                final int position = data.getIntExtra(EXTRA_POSITION, NO_EXTRA_DATA);
                final Task task = data.getParcelableExtra(EXTRA_TASK);
                if (!task.getImageContent().equals("null")) {
                    String imageContent = fileManager.writeToInternalStorage(task.getImageContent());
                    task.setImageContent(imageContent);
                }
                taskManager.POSTImageService(task, new OnImagePostExecuteListener() {
                    @Override
                    public void onPostExecute(String fileName) {
                        fileManager.renameFile(task.getImageContent(), fileName);
                        task.setImageContent(fileName);
                        taskManager.setTask(task, position, new OnPutTaskListener() {
                            @Override
                            public void onPutTask() {
                                taskAdapter.notifyItemChanged(position);
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_ADD) {
            if (resultCode == RESULT_OK) {
                final Task task = data.getParcelableExtra(EXTRA_TASK);
                if (!task.getImageContent().equals("null")) {
                    String imageContent = fileManager.writeToInternalStorage(task.getImageContent());
                    task.setImageContent(imageContent);
                }
                taskManager.POSTImageService(task, new OnImagePostExecuteListener() {
                    @Override
                    public void onPostExecute(String fileName) {
                        if (!fileName.equals("null")) {
                            fileManager.renameFile(task.getImageContent(), fileName);
                            task.setImageContent(fileName);
                        }
                        taskManager.addTask(task, new OnPostTaskListener() {
                            @Override
                            public void onPostTask() {
                                taskAdapter.notifyItemInserted(taskManager.getTasks().size());
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileManager = new ImageFileManager(getApplicationContext());
        taskManager = new TaskManager(this);
        recyclerView = findViewById(R.id.container);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //taskManager.getTasksFromDB();
        taskManager.GETTasksService(new OnSetTasksListener() {
            @Override
            public void onSetTasks() {
                taskAdapter = new TaskAdapter(getApplicationContext(), taskManager.getTasks());
                taskAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos) {
                        Task task = taskManager.getTask(pos);
                        Intent intent = new Intent(getApplicationContext(), TaskEditorActivity.class);
                        intent.putExtra(EXTRA_TASK, task);
                        intent.putExtra(EXTRA_POSITION, pos);
                        startActivityForResult(intent, REQUEST_CODE_UPDATE);
                    }
                });
                taskAdapter.setOnCheckListener(new OnCheckDoneListener() {
                    @Override
                    public void onCheckDone(Task task) {
                        taskManager.updateDoneTask(task);
                    }
                });
                recyclerView.setAdapter(taskAdapter);

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
                fileManager.deleteDoneImage(taskManager.getTasks());
                taskManager.deleteDoneTask(new OnDeleteTaskListener() {
                    @Override
                    public void onDeleteTask() {
                        taskAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

}
