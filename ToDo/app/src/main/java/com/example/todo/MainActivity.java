package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ItemListener {
    @Override
    public ArrayList<TaskItem> getItemList() {
        return item_list;
    }
    FloatingActionButton fab;
    final static ArrayList<TaskItem> item_list = new ArrayList<>();
    FragmentManager fragmentManager;
    ToggleButton update;
    Button delete;

    @Override
    protected void onStart() {
        super.onStart();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new FragmentTaskList()).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);;
        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTask.class);
                startActivity(intent);
            }
        });
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (update.isChecked()) {
                    delete.setVisibility(View.INVISIBLE);
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new FragmentUpdateTaskList()).commit();
                } else {
                    delete.setVisibility(View.VISIBLE);
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new FragmentTaskList()).commit();
                }
            }
        });
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < item_list.size(); i++) {
                    if (item_list.get(i).getDone() == 1) {
                        item_list.remove(i);
                    }
                }
                onStart();
            }
        });
        Intent intent = getIntent();
        TaskItem item = intent.getParcelableExtra("taskitem");

        if (item != null) {
            int defaltValue = item_list.size();
            int position = intent.getIntExtra("position", defaltValue);
            if (position == defaltValue) {
                item_list.add(item);
            } else {
                item_list.set(position, item);
            }
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new FragmentTaskList()).commit();
        } else {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.container, new FragmentTaskList()).commit();
        }
    }

}
