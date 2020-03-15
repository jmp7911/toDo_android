package com.jmp.todo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jmp.todo.R;
import com.jmp.todo.iface.APIService;
import com.jmp.todo.iface.OnCheckDoneListener;
import com.jmp.todo.iface.OnImagePostExecuteListener;
import com.jmp.todo.iface.OnItemClickListener;
import com.jmp.todo.model.ImageFileManager;
import com.jmp.todo.model.Task;
import com.jmp.todo.model.TaskManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.jmp.todo.model.TaskManager.BASE_URL;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Task> tasks;
    private OnItemClickListener itemClickListener;
    private OnCheckDoneListener checkDoneListener;
    private ImageFileManager fileManager;
    final long ONE_DAY = 1000 * 3600 * 24;

    void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    void setOnCheckListener(OnCheckDoneListener listener) {
        this.checkDoneListener = listener;
    }
    TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        itemClickListener = null;
        checkDoneListener = null;
        fileManager = new ImageFileManager(context);
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contentView;
        private TextView dueDateView;
        private CheckBox isDoneCkbox;
        private ImageView taskImage;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.item_content);
            dueDateView = itemView.findViewById(R.id.item_due_date);
            taskImage = itemView.findViewById(R.id.task_image);
            isDoneCkbox = itemView.findViewById(R.id.ckbox);

            isDoneCkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Task task = tasks.get(pos);
                        task.setIsDone(b);
                        checkDoneListener.onCheckDone(task);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Task task = tasks.get(pos);
                        task.setIsDone(isDoneCkbox.isChecked());
                        isDoneCkbox.setChecked(!isDoneCkbox.isChecked());
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
        Task task = tasks.get(position);
        viewHolder.dueDateView.setText(getDday(task));
        String content = task.getContent();
        viewHolder.contentView.setText(content);
        if (task.isDone()) {
            viewHolder.isDoneCkbox.setChecked(true);
        } else {
            viewHolder.isDoneCkbox.setChecked(false);
        }

        if (fileManager.isFileExist(task.getImageContent())) {
            String imageContent = fileManager.getPathFromInternalStorage(task.getImageContent());
            viewHolder.taskImage.setImageDrawable(Drawable.createFromPath(imageContent));
        } else {
            GETImageService(task, new OnImagePostExecuteListener() {
                @Override
                public void onPostExecute(String fileName) {
                    viewHolder.taskImage.setImageDrawable(Drawable.createFromPath(fileName));
                }
            });
        }

    }
    String getDday(Task task) {
        long today = System.currentTimeMillis();
        long dueDate = task.getDueDate();
        long result = dueDate - today;
        String strFormat;
        if (result >= 0) {
            strFormat = "D-%d";
            result = (dueDate - today) / ONE_DAY + 1;
        } else if (result > -ONE_DAY) {
            strFormat = "D-day";
        } else {
            result = (result * -1) / ONE_DAY;
            strFormat = "D+%d";
        }
        return String.format(strFormat, result );
    }
    @Override
    public int getItemCount() {
        return tasks.size();
    }
    private void GETImageService(final Task task, final OnImagePostExecuteListener onImagePostExecuteListener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);
        Call<ResponseBody> getImage = apiService.getImage(task.getImageContent());

        getImage.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                File image = new File(context.getFilesDir(), task.getImageContent());
                InputStream is = response.body().byteStream();
                try {
                    OutputStream outStream = new FileOutputStream(image);
                    byte[] buf = new byte[1024 * 1024 * 10];
                    int len = 0;

                    while ((len = is.read(buf)) > 0) {
                        outStream.write(buf, 0, len);
                    }
                    outStream.close();
                    is.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                onImagePostExecuteListener.onPostExecute(image.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}
