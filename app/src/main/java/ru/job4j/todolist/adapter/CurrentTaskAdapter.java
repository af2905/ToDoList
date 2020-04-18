package ru.job4j.todolist.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.fragments.EditActivity;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.model.Separator;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class CurrentTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private boolean isContainsSeparatorPast;
    private boolean containsSeparatorToday;
    private boolean containsSeparatorTomorrow;
    private boolean containsSeparatorLater;
    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;
    private List<Item> items = new ArrayList<>();
    private Context context;
    private final SqlStore sqlStore;
    private Activity activity;

    public CurrentTaskAdapter(Context context, Activity activity) {
        this.context = context.getApplicationContext();
        this.activity = activity;
        sqlStore = SqlStore.getInstance(context);
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
        int position = -1;
        Separator separator = null;
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).isTask()) {
                Task task = (Task) getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(newTask.getDate());
        long today = Calendar.getInstance(
                TimeZone.getTimeZone("UTC")).get(Calendar.DAY_OF_YEAR);
        long tomorrow = Calendar.getInstance(
                TimeZone.getTimeZone("UTC")).get(Calendar.DAY_OF_YEAR) + 1;
        if (calendar.get(Calendar.DAY_OF_YEAR) < today) {
            newTask.setDateStatus(Separator.TYPE_PAST);
            newTask.setDone(1);
            sqlStore.updateItem(newTask);
        } else if (calendar.get(Calendar.DAY_OF_YEAR) == today) {
            newTask.setDateStatus(Separator.TYPE_TODAY);
            if (!containsSeparatorToday) {
                containsSeparatorToday = true;
                separator = new Separator(Separator.TYPE_TODAY);
            }
        } else if (calendar.get(Calendar.DAY_OF_YEAR) == tomorrow) {
            newTask.setDateStatus(Separator.TYPE_TOMORROW);
            if (!containsSeparatorTomorrow) {
                containsSeparatorTomorrow = true;
                separator = new Separator(Separator.TYPE_TOMORROW);
            }
        } else if (calendar.get(Calendar.DAY_OF_YEAR) > tomorrow) {
            newTask.setDateStatus(Separator.TYPE_LATER);
            if (!containsSeparatorLater) {
                containsSeparatorLater = true;
                separator = new Separator(Separator.TYPE_LATER);
            }
        }
        if (newTask.getDone() != 1) {
            if (position != -1) {
                if (!getItem(position - 1).isTask()) {
                    if (position - 2 >= 0 && getItem((position - 2)).isTask()) {
                        Task task = (Task) getItem(position - 2);
                        if (task.getDateStatus() == newTask.getDateStatus()) {
                            position -= 1;
                        }
                        if (position - 2 < 0) {
                            position -= 1;
                        }
                    }
                    if (separator != null) {
                        addItem(position - 1, separator);
                    }
                }
                addItem(position, newTask);
            } else {
                if (separator != null) {
                    addItem(separator);
                }
                addItem(newTask);
            }
        }
    }

    public void removeItem(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            items.remove(location);
            notifyItemRemoved(location);
            if (location - 1 >= 0 && location <= getItemCount() - 1) {
                if (!getItem(location).isTask() && !getItem(location - 1).isTask()) {
                    Separator separator = (Separator) getItem(location - 1);
                    checkSeparators(separator.getType());
                    items.remove(location - 1);
                    notifyItemRemoved(location - 1);
                }
            } else if (getItemCount() - 1 >= 0 && !getItem(getItemCount() - 1).isTask()) {
                Separator separator = (Separator) getItem(getItemCount() - 1);
                checkSeparators(separator.getType());
                int locationTemp = getItemCount() - 1;
                items.remove(locationTemp);
                notifyItemRemoved(locationTemp);
            }
        }
    }

    private void checkSeparators(int type) {
        switch (type) {
            case Separator.TYPE_PAST:
                isContainsSeparatorPast = false;
                break;
            case Separator.TYPE_TODAY:
                containsSeparatorToday = false;
                break;
            case Separator.TYPE_TOMORROW:
                containsSeparatorTomorrow = false;
                break;
            case Separator.TYPE_LATER:
                containsSeparatorLater = false;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public void removeAllItems() {
        if (getItemCount() != 0) {
            items = new ArrayList<>();
            notifyDataSetChanged();
            isContainsSeparatorPast = false;
            containsSeparatorToday = false;
            containsSeparatorTomorrow = false;
            containsSeparatorLater = false;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        switch (viewType) {
            case TYPE_TASK:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.current_task_design, parent, false);
                Chip name = view.findViewById(R.id.name);
                ImageView subTaskIcon = view.findViewById(R.id.sub_task_icon);
                ImageView alarmIcon = view.findViewById(R.id.alarm_icon);
                TextView date = view.findViewById(R.id.date_text);
                MaterialCheckBox done = view.findViewById(R.id.done);
                return new TaskViewHolder(view, name, subTaskIcon, alarmIcon, date, done);
            case TYPE_SEPARATOR:
                View separator = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.separator, parent, false);
                TextView type = separator.findViewById(R.id.separatorName);
                return new SeparatorViewHolder(separator, type);
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                 final int position) {
        Item item = getItem(position);
        final Resources resources = holder.itemView.getResources();

        if (item.isTask()) {
            holder.itemView.setEnabled(true);
            final Task task = (Task) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
            taskViewHolder.name.setText(task.getName());
            taskViewHolder.name.setOnClickListener(v -> {
                Intent intent = new Intent(activity, EditActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("id", task.getId());
                intent.putExtra("date", task.getDate());
                intent.putExtra("alarm", task.getAlarm());
                activity.startActivity(intent);
            });
            if (task.getDate() != 0) {
                taskViewHolder.date.setText(String.valueOf(Utils.getDate(task.getDate())));
            }
            if (task.getAlarm() != 0) {
                taskViewHolder.alarmIcon.setVisibility(View.VISIBLE);
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                if (task.getAlarm() < calendar.getTimeInMillis()) {
                    taskViewHolder.alarmIcon.setImageResource(R.drawable.ic_notifications_active_red_24dp);
                }
            }
            taskViewHolder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {
                AlarmHelper alarmHelper = AlarmHelper.getInstance();
                if (isChecked) {
                    task.setDone(1);
                    sqlStore.updateItem(task);
                    alarmHelper.removeAlarm(task.getId());
                    removeItem(taskViewHolder.getLayoutPosition());
                }
            });
        } else {
            Separator separator = (Separator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) holder;
            separatorViewHolder.type.setText(resources.getString(separator.getType()));
            if (separatorViewHolder.type.getText()
                    .equals(resources.getString(R.string.separator_past))) {
                separatorViewHolder.type.setTextColor(
                        resources.getColor(android.R.color.darker_gray, activity.getTheme()));
            } else if (separatorViewHolder.type.getText()
                    .equals(resources.getString(R.string.separator_today))) {
                separatorViewHolder.type.setTextColor(
                        resources.getColor(R.color.colorPrimary, activity.getTheme()));
            } else if (separatorViewHolder.type.getText()
                    .equals(resources.getString(R.string.separator_tomorrow))) {
                separatorViewHolder.type.setTextColor(
                        resources.getColor(R.color.colorPrimaryLight, activity.getTheme()));
            } else if (separatorViewHolder.type.getText()
                    .equals(resources.getString(R.string.separator_later))) {
                separatorViewHolder.type.setTextColor(
                        resources.getColor(android.R.color.darker_gray, activity.getTheme()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) {
            return TYPE_TASK;
        } else {
            return TYPE_SEPARATOR;
        }
    }

    private class TaskViewHolder extends RecyclerView.ViewHolder {
        Chip name;
        ImageView subTaskIcon, alarmIcon;
        TextView date;
        MaterialCheckBox done;


        TaskViewHolder(@NonNull View itemView, Chip name, ImageView subTaskIcon,
                       ImageView alarmIcon, TextView date, MaterialCheckBox done) {
            super(itemView);
            this.name = name;
            this.subTaskIcon = subTaskIcon;
            this.alarmIcon = alarmIcon;
            this.date = date;
            this.done = done;
        }
    }

    private class SeparatorViewHolder extends RecyclerView.ViewHolder {
        TextView type;

        SeparatorViewHolder(@NonNull View itemView, TextView type) {
            super(itemView);
            this.type = type;
        }
    }
}

