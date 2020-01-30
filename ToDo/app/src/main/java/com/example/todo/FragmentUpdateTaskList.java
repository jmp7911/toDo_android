package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentUpdateTaskList extends Fragment {

    View view;
    RecyclerView listView;
    ArrayList<TaskItem> list_item = new ArrayList<>();
    ItemListener itemListener = null;
    int position;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context.getClass().getName().equals("com.example.todo.MainActivity")) {
            itemListener = (ItemListener) context;
            list_item = itemListener.getItemList();
        }
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
        TaskAdapterForUpdate taskAdapter = new TaskAdapterForUpdate(list_item);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(taskAdapter);
        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(getActivity().getApplicationContext(), UpdateTask.class);
                intent.putExtra("position", pos);
                startActivity(intent);
            }
        });

        super.onActivityCreated(savedInstanceState);
    }


}
