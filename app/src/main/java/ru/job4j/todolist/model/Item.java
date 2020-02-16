package ru.job4j.todolist.model;

import java.util.Objects;

public class Item {
    private int id;
    private String name;
    private String desc;
    private String created;
    private boolean done;

    public Item(String name, String desc, String created) {
        this.name = name;
        this.desc = desc;
        this.created = created;
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

    void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreated() {
        return created;
    }

    void setCreated(String created) {
        this.created = created;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
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
        return id == item.id
                && done == item.done
                && name.equals(item.name)
                && Objects.equals(desc, item.desc)
                && Objects.equals(created, item.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, desc, created, done);
    }
}
