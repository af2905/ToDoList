package ru.job4j.todolist.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;

import java.util.List;

import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Task> tasks;
    private Context context;
    private Activity activity;

    TaskAdapter(Context context, Activity activity, List<Task> tasks) {
        this.context = context.getApplicationContext();
        this.activity = activity;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_task, parent, false)) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final SqlStore store = SqlStore.getInstance(context);
        final Task task = tasks.get(position);
        final Chip name = holder.itemView.findViewById(R.id.name);
        name.setText(task.getName());
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditActivity.class);
                intent.putExtra("id", task.getId());
                intent.putExtra("date", task.getDate());
                intent.putExtra("alarm", task.getAlarm());
                activity.startActivity(intent);
            }
        });

        final TextView date = holder.itemView.findViewById(R.id.date);
        if (task.getDate() != 0) {
            date.setText(String.valueOf(Utils.getDate(task.getDate())));
        }

        final ImageView imgAlarm = holder.itemView.findViewById(R.id.imgAlarm);
        final TextView alarm = holder.itemView.findViewById(R.id.alarmOnOff);
        if (task.getAlarm() != 0) {
            alarm.setText(String.valueOf(Utils.getTime(task.getAlarm())));
            imgAlarm.setImageResource(R.drawable.ic_alarm_dark_gray_24dp);
            imgAlarm.setColorFilter(activity.getResources()
                    .getColor(R.color.colorAccent, activity.getTheme()));
        }

        final ImageView imgNotes = holder.itemView.findViewById(R.id.imgNotes);
        if (task.getDesc().length() != 0) {
            imgNotes.setImageResource(R.drawable.ic_short_text_dark_gray_24dp);
            imgNotes.setColorFilter(activity.getResources()
                    .getColor(R.color.colorAccent, activity.getTheme()));
        }

        final MaterialCheckBox done = holder.itemView.findViewById(R.id.done);
        if (task.getDone() == 1) {
            done.setChecked(true);
            name.setEnabled(false);
        }
        done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AlarmHelper alarmHelper = AlarmHelper.getInstance();
                if (isChecked) {
                    task.setDone(1);
                    store.updateItem(task);
                    alarmHelper.removeAlarm(task.getId());
                    name.setEnabled(false);
                } else {
                    task.setDone(0);
                    store.updateItem(task);
                    alarmHelper.setExactAlarm(task);
                    name.setEnabled(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}