package ru.job4j.todolist.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SqlStore sqlStore = SqlStore.getInstance(context);
        AlarmHelper.getInstance().init(context);
        AlarmHelper alarmHelper = AlarmHelper.getInstance();
        List<Task> tasks = sqlStore.getAllItems();
        for (Task task : tasks) {
            if (task.getAlarm() != 0) {
                alarmHelper.setExactAlarm(task);
            }
        }
    }
}
