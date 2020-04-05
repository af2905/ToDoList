package ru.job4j.todolist.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ru.job4j.todolist.model.Task;

public class AlarmHelper {
    private static AlarmHelper instance;
    private Context context;
    private AlarmManager alarmManager;

    public static AlarmHelper getInstance() {
        if (instance == null) {
            instance = new AlarmHelper();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context
                .getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(Task task) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("_id", task.getId());
        intent.putExtra("name", task.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getAlarm(), pendingIntent);
    }

    public void removeAlarm(int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
}
