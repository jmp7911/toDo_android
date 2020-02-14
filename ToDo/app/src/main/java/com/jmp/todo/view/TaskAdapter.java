package com.jmp.todo.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmp.todo.R;
import com.jmp.todo.iface.OnCheckListener;
import com.jmp.todo.iface.OnItemClickListener;
import com.jmp.todo.model.DataDone;
import com.jmp.todo.model.Task;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private OnItemClickListener itemClickListener = null;
    private OnCheckListener checkListener = null;

    final long ONE_DAY = 1000 * 3600 * 24;

    void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    void setOnCheckListener(OnCheckListener listener) {
        this.checkListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contentView;
        private TextView dueDateView;
        private CheckBox isDoneCkbox;
        private ImageView itemImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.item_content);
            dueDateView = itemView.findViewById(R.id.item_dueDate);
            itemImage = itemView.findViewById(R.id.item_image);

            isDoneCkbox = itemView.findViewById(R.id.ckbox);
            isDoneCkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (isDoneCkbox.isChecked()) {
                            Task item = MainActivity.taskManager.getItem(pos);
                            item.setIsDone(true);
                            checkListener.onCheckDone(item.isDone(), item.getTaskId());
                            DataDone dataDone = new DataDone();
                            dataDone.execute("http://" + MainActivity.IP_ADDRESS + "/dataDone.php", item.getTaskId()
                                    , Boolean.toString(item.isDone()));
                        } else {
                            Task item = MainActivity.taskManager.getItem(pos);
                            item.setIsDone(false);
                            checkListener.onCheckDone(item.isDone(), item.getTaskId());
                            DataDone dataDone = new DataDone();
                            dataDone.execute("http://" + MainActivity.IP_ADDRESS + "/dataDone.php", item.getTaskId()
                                    , Boolean.toString(item.isDone()));
                        }
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (isDoneCkbox.isChecked()) {
                            Task item = MainActivity.taskManager.getItem(pos);
                            item.setIsDone(false);
                            checkListener.onCheckDone(item.isDone(), item.getTaskId());
                            DataDone dataDone = new DataDone();
                            dataDone.execute("http://" + MainActivity.IP_ADDRESS + "/dataDone.php", item.getTaskId()
                                    , Boolean.toString(item.isDone()));
                            isDoneCkbox.setChecked(false);
                        } else {
                            Task item = MainActivity.taskManager.getItem(pos);
                            item.setIsDone(true);
                            checkListener.onCheckDone(item.isDone(), item.getTaskId());
                            DataDone dataDone = new DataDone();
                            dataDone.execute("http://" + MainActivity.IP_ADDRESS + "/dataDone.php", item.getTaskId()
                                    , Boolean.toString(item.isDone()));
                            isDoneCkbox.setChecked(true);
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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Task item = MainActivity.taskManager.getItemList().get(position);
        long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
        int mYear = item.getDueDateYear();
        int mMonth = item.getDueDateMonth();
        int mDayOfMonth = item.getDueDateDayOfMonth();
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
        viewHolder.dueDateView.setText(strCount);
        String content = item.getContent();
        viewHolder.contentView.setText(content);

        if (item.isDone()) {
            viewHolder.isDoneCkbox.setChecked(true);
        } else {
            viewHolder.isDoneCkbox.setChecked(false);
        }


    }

    @Override
    public int getItemCount() {
        return MainActivity.taskManager.getItemList().size();

    }
}
