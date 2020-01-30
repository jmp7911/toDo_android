package com.example.todo;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    ArrayList<TaskItem> item_list;
    OnItemClickListener itemClickListener = null;

    public TaskAdapter(ArrayList<TaskItem> item_list) {
        this.item_list = item_list;
    }
    void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_task;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_task = itemView.findViewById(R.id.item_task);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(view, pos);
                        if (item_list.get(pos).isDone == 1) {
                            text_task.setPaintFlags(0);
                            item_list.get(pos).setDone(0);
                        } else {
                            text_task.setPaintFlags(text_task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            item_list.get(pos).setDone(1);
                        }
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String task_name = item_list.get(position).getTask();
        viewHolder.text_task.setText(task_name);
        int isDone = item_list.get(position).getDone();
        if (isDone == 1) {
            viewHolder.text_task.setPaintFlags(viewHolder.text_task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.text_task.setPaintFlags(0);
        }
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }
}
