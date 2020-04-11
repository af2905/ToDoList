package ru.job4j.todolist.model;

import ru.job4j.todolist.R;

public class Separator implements Item {
    public static final int TYPE_PAST = R.string.separator_past;
    public static final int TYPE_TODAY = R.string.separator_today;
    public static final int TYPE_TOMORROW = R.string.separator_tomorrow;
    public static final int TYPE_LATER = R.string.separator_later;

    private int type;

    public Separator(int type) {
        this.type = type;
    }

    @Override
    public boolean isTask() {
        return false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
