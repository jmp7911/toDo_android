package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class FragmentUpdateTask extends Fragment {

    View view;
    RecyclerView listView;
    ArrayList<TaskItem> list_item = new ArrayList<>();
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_task, container, false);
        listView = view.findViewById(R.id.update_task_item_container);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        list_item.add(new TaskItem("Todo"));
        list_item.add(new TaskItem("list"));
        TaskAdapter taskAdapter = new TaskAdapter(list_item);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(taskAdapter);
        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

            }
        });
        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_update_task);
//        update_text_task = findViewById(R.id.update_text_task);
//        update_submit = findViewById(R.id.update_submit);
//        update_cancel = findViewById(R.id.update_cancel);
//        Intent intent = getIntent();
//        String text_task = intent.getStringExtra("text_task");
//        update_text_task.setText(text_task);
//        update_submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent1 = new Intent(FragmentUpdateTask.this, Task.class);
//                String update_text_taskToString = update_text_task.getText().toString();
//                intent1.putExtra("update_text_task", update_text_taskToString);
//                startActivity(intent1);
//
//                finish();
//            }
//        });
//        update_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//    }
}
