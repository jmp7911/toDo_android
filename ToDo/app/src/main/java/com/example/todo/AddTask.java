package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTask extends AppCompatActivity {
    Button submit;
    Button cancel;
    EditText add_text_task;
    EditText add_text_place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        add_text_task = findViewById(R.id.add_text_task);
        add_text_place = findViewById(R.id.add_text_place);
        submit = findViewById(R.id.add_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = add_text_task.getText().toString();
                String place = add_text_place.getText().toString();
                TaskItem taskItem = new TaskItem(task, place);
                finish();
            }
        });
        cancel = findViewById(R.id.add_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
