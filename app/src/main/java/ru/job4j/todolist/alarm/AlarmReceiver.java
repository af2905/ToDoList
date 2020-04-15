package ru.job4j.todolist.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import ru.job4j.todolist.MyApplication;
import ru.job4j.todolist.R;
import ru.job4j.todolist.fragments.CurrentTasksActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = String.valueOf(R.string.app_name);

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("_id", 0);
        String name = intent.getStringExtra("name");

        Intent resultIntent = new Intent(context, CurrentTasksActivity.class);

        if (MyApplication.isActivityVisible()) {
            resultIntent = intent;
        }

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_alarm_accent_24dp)
                .setDefaults(Notification.DEFAULT_ALL)
                .setCategory(Notification.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ToDoList",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        if (notificationManager != null) {
            notificationManager.notify(id, builder.build());
        }
    }
}