package ru.job4j.todolist.store;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.todolist.model.Task;

public class MemStore implements IStore {
    private static final MemStore INST = new MemStore();
    private final List<Task> tasks = new ArrayList<>();

    private MemStore() {
    }

    public static MemStore getStore() {
        return INST;
    }

    @Override
    public void addItem(Task task) {
        this.tasks.add(task);
    }

    @Override
    public Task getItem(int id) {
        return tasks.get(id);
    }

    @Override
    public List<Task> getSelectedItems(String text) {
        return null;
    }

    @Override
    public List<Task> getAllItems() {
        return this.tasks;
    }

    @Override
    public int size() {
        return tasks.size();
    }

    @Override
    public int updateItem(Task task) {
        return 0;
    }

    @Override
    public void deleteItem(Task task) {
        tasks.remove(task);
    }

    @Override
    public void deleteAll() {
        tasks.clear();
    }
}
