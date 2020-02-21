package com.jmp.todo.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmp.todo.R;
import com.jmp.todo.model.ImageFileManager;
import com.jmp.todo.model.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TaskEditorActivity extends AppCompatActivity {
    private EditText contentEditText;
    private TextView dueDateTextView;
    private long dueDate = System.currentTimeMillis();
    final int TASK_IMAGE = 1003;
    private ImageView taskImage;
    private String imageContent = "null";
    private Calendar calendar = Calendar.getInstance();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TASK_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {
                    imageContent = data.getDataString();
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    taskImage.setImageBitmap(img);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        taskImage = findViewById(R.id.editor_image);
        contentEditText = findViewById(R.id.content);
        dueDateTextView = findViewById(R.id.due_date);
        Button datePickerDialog = findViewById(R.id.date_picker_dialog);
        datePickerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TaskEditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        dueDateTextView.setText(new StringBuilder().append(year).append("-").append(month + 1).append("-")
                                .append(dayOfMonth));
                        calendar.set(year,month,dayOfMonth);
                        dueDate = calendar.getTimeInMillis();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        Button galleryButton = findViewById(R.id.get_image);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, TASK_IMAGE);
            }
        });

        final Intent intent = getIntent();
        final Task task = intent.getParcelableExtra(MainActivity.EXTRA_TASK);
        if (task != null) {
            contentEditText.setText(task.getContent());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dueDateTextView.setText(simpleDateFormat.format(task.getDueDate()));
            ImageFileManager fileManager = new ImageFileManager(getApplicationContext());
            taskImage.setImageDrawable(Drawable.createFromPath(fileManager.getPath(task.getImageContent())));
        }
        Button submitButton = findViewById(R.id.edit_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = contentEditText.getText().toString();
                Task taskItem;
                if (task == null) {
                    taskItem = new Task(content, false, dueDate, imageContent);
                } else {
                    taskItem = task;
                    taskItem.setContent(content);
                    taskItem.setDueDate(dueDate);
                    if (!imageContent.equals("null")) {
                        taskItem.setImageContent(imageContent);
                    }
                }
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
