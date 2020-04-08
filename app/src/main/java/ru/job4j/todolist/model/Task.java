package ru.job4j.todolist.model;

import java.util.Objects;

public class Task implements Item {
    private int id;
    private String name;
    private String desc;
    private long date;
    private long alarm;
    private int done;

    @Override
    public boolean isTask() {
        return true;
    }

    public Task(int id, String name, String desc, long date, long alarm, int done) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.alarm = alarm;
        this.done = done;
    }

    public Task(String name, String desc, long date, long alarm, int done) {
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.alarm = alarm;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getAlarm() {
        return alarm;
    }

    public void setAlarm(long alarm) {
        this.alarm = alarm;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id
                && date == task.date
                && alarm == task.alarm
                && done == task.done
                && Objects.equals(name, task.name)
                && Objects.equals(desc, task.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, desc, date, alarm, done);
    }
}
