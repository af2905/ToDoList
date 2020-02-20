package ru.job4j.todolist.store;

import java.util.List;

import ru.job4j.todolist.model.Item;

public interface IStore {
    void addItem(Item item);

    Item getItem(int id);

    List<Item> getSelectedItems(String text);

    List<Item> getAllItems();

    int updateItem(Item item);

    void deleteItem(Item item);

    void deleteAll();
}
