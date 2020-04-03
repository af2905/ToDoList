package ru.job4j.todolist.model;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String desc;
    private String created;
    private int done;

    public Task(int id, String name, String desc, String created, int done) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.done = done;
    }

    public Task(String name, String desc, String created, int done) {
        this.name = name;
        this.desc = desc;
        this.created = created;
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

    public String getCreated() {
        return created;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCreated(String created) {
        this.created = created;
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
                && done == task.done
                && name.equals(task.name)
                && Objects.equals(desc, task.desc)
                && Objects.equals(created, task.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, desc, created, done);
    }
}
