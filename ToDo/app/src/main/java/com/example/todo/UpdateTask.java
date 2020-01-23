package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class UpdateTask extends AppCompatActivity {

    EditText update_text_task;
    EditText update_text_place;
    Button update_submit;
    Button update_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        update_text_task = findViewById(R.id.update_text_task);
        update_text_place = findViewById(R.id.update_text_place);
        update_submit = findViewById(R.id.update_submit);
        update_cancel = findViewById(R.id.update_cancel);
        Intent intent = getIntent();
        String text_task = intent.getStringExtra("text_task");
        String text_place = intent.getStringExtra("text_place");
        update_text_task.setText(text_task);
        update_text_place.setText(text_place);
        update_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(UpdateTask.this, Task.class);
                String update_text_taskToString = update_text_task.getText().toString();
                String update_text_placeToString = update_text_place.getText().toString();
                intent1.putExtra("update_text_task", update_text_taskToString);
                intent1.putExtra("update_text_place", update_text_placeToString);
                startActivity(intent1);

                finish();
            }
        });
        update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
