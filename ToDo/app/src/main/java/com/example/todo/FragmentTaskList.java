package com.example.todo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentTaskList extends Fragment {
    View view;
    RecyclerView listView;
    ArrayList<TaskItem> item_list = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.task_list_item_container);
        item_list.add(new TaskItem("Todo"));
        item_list.add(new TaskItem("list"));
        TaskAdapter taskAdapter = new TaskAdapter(item_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(taskAdapter);
        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

//                Intent intent = new Intent(MainActivity.this, Task.class);
//                intent.putExtra("text_task", item_list.get(pos).getTask());
//                intent.putExtra("position", pos);
//                startActivity(intent);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
