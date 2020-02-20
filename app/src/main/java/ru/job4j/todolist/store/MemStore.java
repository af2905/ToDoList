package ru.job4j.todolist.store;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.todolist.model.Item;

public class MemStore implements IStore {
    private static final MemStore INST = new MemStore();
    private final List<Item> items = new ArrayList<>();

    private MemStore() {
    }

    public static MemStore getStore() {
        return INST;
    }

    @Override
    public void addItem(Item item) {
        this.items.add(item);
    }

    @Override
    public Item getItem(int id) {
        return items.get(id);
    }

    @Override
    public List<Item> getSelectedItems(String text) {
        return null;
    }

    @Override
    public List<Item> getAllItems() {
        return this.items;
    }

    @Override
    public int updateItem(Item item) {
        return 0;
    }

    @Override
    public void deleteItem(Item item) {
        items.remove(item);
    }

    @Override
    public void deleteAll() {
        items.clear();
    }
}
