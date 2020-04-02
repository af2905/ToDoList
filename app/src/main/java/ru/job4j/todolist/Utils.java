package ru.job4j.todolist;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {
    public static String getDate(long date) {
        SimpleDateFormat dateFormat
                = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(time);
    }

    public static String getFullDate(long date) {
        SimpleDateFormat fullDateFormat
                = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        return fullDateFormat.format(date);
    }
}
