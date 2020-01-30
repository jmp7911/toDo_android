package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Task extends AppCompatActivity {
    TextView text_task;
    Button update;
    Button cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        text_task = findViewById(R.id.text_task);
        update = findViewById(R.id.update);
        cancel = findViewById(R.id.cancel);
        final Intent intent = getIntent();
        final int position;
        position = intent.getIntExtra("position", 0);
        if (intent.getStringExtra("text_task") != null) {
            String text_taskToString = intent.getStringExtra("text_task");
            text_task.setText(text_taskToString);
        }
        if (intent.getStringExtra("update_text_task") != null) {
            String update_text_task = intent.getStringExtra("update_text_task");
            text_task.setText(update_text_task);
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
                Intent intent1 = new Intent(Task.this, FragmentUpdateTask.class);
                intent1.putExtra("text_task", text_task.getText().toString());
                intent1.putExtra("position", position);
                startActivity(intent1);
                finish();
            }
        });
    }
}
