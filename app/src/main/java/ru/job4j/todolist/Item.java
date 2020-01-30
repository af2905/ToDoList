package ru.job4j.todolist;

import java.util.Calendar;
import java.util.Objects;

public class Item {
    private String name;
    private String desc;
    private Calendar created;
    private boolean done;

    Item(String name, String desc, Calendar created) {
        this.name = name;
        this.desc = desc;
        this.created = created;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getDesc() {
        return desc;
    }

    void setDesc(String desc) {
        this.desc = desc;
    }

    Calendar getCreated() {
        return created;
    }

    void setCreated(Calendar created) {
        this.created = created;
    }

    boolean isDone() {
        return done;
    }

    void setDone(boolean done) {
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
        Item item = (Item) o;
        return Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
