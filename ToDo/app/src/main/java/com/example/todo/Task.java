package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Task extends AppCompatActivity {
    TextView text_task;
    TextView text_place;
    Button update;
    Button cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        text_task = findViewById(R.id.text_task);
        text_place = findViewById(R.id.text_place);
        update = findViewById(R.id.update);
        cancel = findViewById(R.id.cancel);
        final Intent intent = getIntent();
        final int position;
        position = intent.getIntExtra("position", 0);
        if (intent.getStringExtra("text_task") != null ||
                intent.getStringExtra("text_place") != null) {
            String text_taskToString = intent.getStringExtra("text_task");
            String text_placeToString = intent.getStringExtra("text_place");
            text_task.setText(text_taskToString);
            text_place.setText(text_placeToString);
        }
        if (intent.getStringExtra("update_text_task") != null ||
        intent.getStringExtra("update_text_place") != null) {
            String update_text_task = intent.getStringExtra("update_text_task");
            String update_text_place = intent.getStringExtra("update_text_place");
            text_task.setText(update_text_task);
            text_place.setText(update_text_place);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Task.this, UpdateTask.class);
                intent1.putExtra("text_task", text_task.getText().toString());
                intent1.putExtra("text_place", text_place.getText().toString());
                intent1.putExtra("position", position);
                startActivity(intent1);
                finish();
            }
        });
    }
}
