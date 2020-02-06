package com.jmp.todo.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jmp.todo.R;
import com.jmp.todo.iface.OnItemClickListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private OnItemClickListener itemClickListener = null;
    final long ONE_DAY = 1000 * 3600 * 24;

    void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contentView;
        private TextView dueDateView;
        private CheckBox isDoneCkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.item_content);
            dueDateView = itemView.findViewById(R.id.item_dueDate);
            isDoneCkbox = itemView.findViewById(R.id.ckbox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (isDoneCkbox.isChecked()) {
                            MainActivity.taskManager.getItem(pos).setIsDone(0);
                            isDoneCkbox.setChecked(false);
                        } else {
                            isDoneCkbox.setChecked(true);
                            MainActivity.taskManager.getItem(pos).setIsDone(1);
                        }
                    }

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(view, pos);
                    }
                    return true;
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
        String content = MainActivity.taskManager.getItemList().get(position).getContent();
        long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
        int mYear = MainActivity.taskManager.getItemList().get(position).getDueDateYear();
        int mMonth = MainActivity.taskManager.getItemList().get(position).getDueDateMonth();
        int mDayOfMonth = MainActivity.taskManager.getItemList().get(position).getDueDateDayOfMonth();
        Calendar dDayCal = Calendar.getInstance();
        dDayCal.set(mYear, mMonth, mDayOfMonth);
        long dDay = dDayCal.getTimeInMillis() / ONE_DAY;
        long result = dDay - today;
        String strFormat;
        if (result > 0) {
            strFormat = "D-%d";
        } else if (result == 0) {
            strFormat = "D-day";
        } else {
            result *= -1;
            strFormat = "D+%d";
        }
        String strCount = String.format(strFormat, result);
        viewHolder.contentView.setText(content);
        viewHolder.dueDateView.setText(strCount);

    }

    @Override
    public int getItemCount() {
        return MainActivity.taskManager.getItemList().size();

    }
}
