package ru.job4j.todolist;

import java.util.ArrayList;
import java.util.List;

class Store {
    private static final Store INST = new Store();
    private final List<Item> items = new ArrayList<>();

    private Store() {
    }

    static Store getStore() {
        return INST;
    }

    void add(Item item) {
        this.items.add(item);
    }

    void set(int index, Item item) {
        this.items.set(index, item);
    }

    List<Item> getAll() {
        return this.items;
    }

    int size() {
        return items.size();
    }

    Item get(int index) {
        return items.get(index);
    }

}
