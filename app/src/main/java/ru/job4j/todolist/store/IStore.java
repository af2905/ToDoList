package ru.job4j.todolist.store;

import java.util.List;

import ru.job4j.todolist.model.Task;

public interface IStore {
    void addItem(Task task);

    Task getItem(int id);

    List<Task> getSelectedItems(String text);

    List<Task> getAllItems();

    int size();

    int updateItem(Task task);

    void deleteItem(Task task);

    void deleteAll();
}
