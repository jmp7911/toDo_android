package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView list_item;
    ArrayList<TaskItem> item_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTask.class);
                startActivity(intent);
            }
        });
        item_list.add(new TaskItem("Todo", "library"));
        item_list.add(new TaskItem("list", "library"));
        list_item = findViewById(R.id.item_container);
        TaskAdapter taskAdapter = new TaskAdapter(item_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        list_item.setLayoutManager(layoutManager);
        list_item.setAdapter(taskAdapter);
        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(MainActivity.this, Task.class);
                intent.putExtra("text_task", item_list.get(pos).getTask());
                intent.putExtra("text_place", item_list.get(pos).getPlace());
                intent.putExtra("position", pos);
                startActivity(intent);
            }
        });



    }

}
