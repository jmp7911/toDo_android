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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmp.todo.R;
import com.jmp.todo.iface.OnItemClickListener;
import com.jmp.todo.model.Task;
import com.jmp.todo.model.TaskManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static TaskManager taskManager = new TaskManager();
    protected TaskAdapter taskAdapter = new TaskAdapter();
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
                int position = data.getIntExtra(EXTRA_POSITION, NO_EXTRA_DATA);
                Task item = data.getParcelableExtra(EXTRA_TASK);
                taskManager.setItem(item, position);
                taskAdapter.notifyItemChanged(position);
            } else {
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_ADD) {
            if (resultCode == RESULT_OK) {
                Task item = data.getParcelableExtra(EXTRA_TASK);
                taskManager.addItem(item);
                taskAdapter.notifyItemInserted(taskManager.getItemList().size());
            } else {
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    if (taskManager.getItem(index).getIsDone() == 1) {
                        taskManager.getItemList().remove(index);
                        taskAdapter.notifyItemRemoved(index);
                    }
                }

            }
        });

    }
}
