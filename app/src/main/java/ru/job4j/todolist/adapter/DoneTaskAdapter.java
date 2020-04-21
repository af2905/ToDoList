package ru.job4j.todolist.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class DoneTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> items = new ArrayList<>();
    private Context context;
    private Activity activity;

    public DoneTaskAdapter(Context context, Activity activity) {
        this.context = context.getApplicationContext();
        this.activity = activity;
    }

    private Item getItem(int position) {
        return items.get(position);
    }

    private void addItem(Item item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    private void addItem(int location, Item item) {
        items.add(location, item);
        notifyItemInserted(location);
    }

    public void addSortedTask(Task newTask) {
        if (newTask.getDone() != 0) {
            int position = -1;
            for (int i = 0; i < getItemCount(); i++) {
                if (getItem(i).isTask()) {
                    Task task = (Task) getItem(i);
                    if (newTask.getDate() < task.getDate()) {
                        position = i;
                        break;
                    }
                }
            }
            if (position != -1) {
                addItem(position, newTask);
            } else {
                addItem(newTask);
            }
        }
    }

    private void removeItem(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            items.remove(location);
            notifyItemRemoved(location);
        }
    }

    public void removeAllItems() {
        if (getItemCount() != 0) {
            items = new ArrayList<>();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.done_task_design, parent, false);
        Chip name = view.findViewById(R.id.name);
        ImageView subTaskIcon = view.findViewById(R.id.sub_task_icon);
        ImageView alarmIcon = view.findViewById(R.id.alarm_icon);
        TextView date = view.findViewById(R.id.date_text);
        MaterialButton undo = view.findViewById(R.id.undo);
        ImageView delete = view.findViewById(R.id.delete_done_task);
        return new DoneTaskAdapter.TaskViewHolder(view, name, undo, subTaskIcon, alarmIcon, date, delete);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SqlStore sqlStore = SqlStore.getInstance(context);
        final Resources resources = holder.itemView.getResources();
        AlarmHelper alarmHelper = AlarmHelper.getInstance();
        Task task = (Task) items.get(position);
        final DoneTaskAdapter.TaskViewHolder taskViewHolder = (DoneTaskAdapter.TaskViewHolder) holder;

        taskViewHolder.name.setText(task.getName());
        taskViewHolder.name.setEnabled(false);
        taskViewHolder.name.setTextColor(
                resources.getColor(R.color.colorPrimaryLight, activity.getTheme()));
        if (task.getDate() != 0) {
            taskViewHolder.date.setText(String.valueOf(Utils.getDate(task.getDate())));
            taskViewHolder.date.setTextColor(
                    resources.getColor(R.color.colorSilver, activity.getTheme()));
        }
        if (task.getAlarm() != 0) {
            taskViewHolder.alarmIcon.setVisibility(View.VISIBLE);
            if (task.getAlarm() < Calendar.getInstance().getTimeInMillis()) {
                taskViewHolder.alarmIcon.setImageDrawable(
                        resources.getDrawable(
                                R.drawable.ic_notifications_active_silver_24dp, activity.getTheme()));
            }
        }
        if (task.getDone() == 1) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(task.getDate());
            long today = Calendar.getInstance(
                    TimeZone.getTimeZone("UTC")).get(Calendar.DAY_OF_YEAR);

            if (calendar.get(Calendar.DAY_OF_YEAR) < today) {
                taskViewHolder.undo.setEnabled(false);
                taskViewHolder.undo.setVisibility(View.INVISIBLE);
            }
            if (task.getAlarm() != 0) {
                if (task.getAlarm() < Calendar.getInstance().getTimeInMillis()) {
                    taskViewHolder.undo.setEnabled(false);
                    taskViewHolder.undo.setVisibility(View.INVISIBLE);
                }
            }
        }
        taskViewHolder.undo.setOnClickListener(v -> {
            task.setDone(0);
            sqlStore.updateItem(task);
            if (task.getAlarm() != 0) {
                alarmHelper.setExactAlarm(task);
            }
            removeItem(taskViewHolder.getLayoutPosition());
        });
        taskViewHolder.delete.setOnClickListener(v -> {
            removeItem(taskViewHolder.getLayoutPosition());
            sqlStore.deleteItem(task);
            sqlStore.deleteSubtasks(task.getId());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class TaskViewHolder extends RecyclerView.ViewHolder {
        Chip name;
        ImageView subTaskIcon, alarmIcon, delete;
        TextView date;
        MaterialButton undo;

        TaskViewHolder(@NonNull View itemView, Chip name, MaterialButton undo, ImageView subTaskIcon,
                       ImageView alarmIcon, TextView date, ImageView delete) {
            super(itemView);
            this.name = name;
            this.subTaskIcon = subTaskIcon;
            this.alarmIcon = alarmIcon;
            this.date = date;
            this.undo = undo;
            this.delete = delete;
        }
    }
}
