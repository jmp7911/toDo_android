package com.jmp.todo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.jmp.todo.R;
import com.jmp.todo.model.Task;

import java.util.Calendar;


public class TaskEditorActivity extends AppCompatActivity {
    private EditText contentEditText;
    private TextView dueDateTextView;
    private int mYear;
    private int mMonth;
    private int mDayOfMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        contentEditText = findViewById(R.id.content);
        dueDateTextView = findViewById(R.id.due_date);
        Button datePickerButton = findViewById(R.id.date_picker_dialog);
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TaskEditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        dueDateTextView.setText(new StringBuilder().append(year).append("-").append(month + 1).append("-")
                                .append(dayOfMonth));
                        mYear = year;
                        mMonth = month;
                        mDayOfMonth = dayOfMonth;
                    }
                }, mYear, mMonth, mDayOfMonth);
                datePickerDialog.show();
            }
        });
        final Intent intent = getIntent();
        final Task task = intent.getParcelableExtra(MainActivity.EXTRA_TASK);
        if (task != null) {
            contentEditText.setText(task.getContent());
            mYear = task.getDueDateYear();
            mMonth = task.getDueDateMonth();
            mDayOfMonth = task.getDueDateDayOfMonth();
            dueDateTextView.setText(new StringBuilder().append(mYear).append("-").append(mMonth + 1)
                    .append("-").append(mDayOfMonth));
        }
        Button submitButton = findViewById(R.id.edit_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = contentEditText.getText().toString();
                Task taskItem = new Task(content, 0, mYear, mMonth, mDayOfMonth);
                Intent intent1 = new Intent();
                intent1.putExtra(MainActivity.EXTRA_TASK, taskItem);
                int position = intent.getIntExtra(MainActivity.EXTRA_POSITION, MainActivity.NO_EXTRA_DATA);
                if (position != MainActivity.NO_EXTRA_DATA) {
                    intent1.putExtra(MainActivity.EXTRA_POSITION, position);
                }
                setResult(RESULT_OK, intent1);
                finish();
            }
        });
        Button cancelButton = findViewById(R.id.edit_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
