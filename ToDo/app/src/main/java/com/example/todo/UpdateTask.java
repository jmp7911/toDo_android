package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UpdateTask extends AppCompatActivity {
    Button update;
    Button cancel;
    EditText task_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        task_text = findViewById(R.id.update_text_task);
        update = findViewById(R.id.update_submit);
        Intent intent1 = getIntent();
        final int position = intent1.getIntExtra("position", 0);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                String text_task = task_text.getText().toString();
                TaskItem item = new TaskItem(text_task, 0);
                intent.putExtra("taskitem", item);
                intent.putExtra("position", position);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
