package ru.job4j.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.job4j.todolist.fragments.ItemsActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("name");
        long timeStamp = intent.getLongExtra("time_stamp", 0);

        Intent resultIntent = new Intent(context, ItemsActivity.class);
    }
}
