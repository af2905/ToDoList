package ru.job4j.todolist.model;

import java.util.Objects;

public class Subtask {
    private int id;
    private int taskId;
    private String text;

    public Subtask(int id, int taskId, String text) {
        this.id = id;
        this.taskId = taskId;
        this.text = text;
    }

    public Subtask(int taskId, String text) {
        this.taskId = taskId;
        this.text = text;
    }

    public Subtask(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subtask subtask = (Subtask) o;
        return id == subtask.id
                && taskId == subtask.taskId
                && Objects.equals(text, subtask.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskId, text);
    }
}
