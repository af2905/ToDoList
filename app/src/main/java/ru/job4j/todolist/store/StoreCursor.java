package ru.job4j.todolist.store;

import android.database.AbstractCursor;

import ru.job4j.todolist.model.Item;

public class StoreCursor extends AbstractCursor {
    private final MemStore memStore;
    private String selection;

    StoreCursor(MemStore memStore, String selection) {
        this.memStore = memStore;
        this.selection = selection;
    }

    @Override
    public int getCount() {
        return memStore.getAllItems().size();
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"_ID", "NAME"};
    }

    @Override
    public String getString(int column) {
        Item item = memStore.getItem(getPosition());
        String value = "";
        if (column == 1) {
            if (item.getName() != null && item.getName().contains(selection)) {
                value = item.getName();
            }
        }
        return value;
    }

    @Override
    public short getShort(int column) {
        return 0;
    }

    @Override
    public int getInt(int column) {
        return 0;
    }

    @Override
    public long getLong(int column) {
        return 0;
    }

    @Override
    public float getFloat(int column) {
        return 0;
    }

    @Override
    public double getDouble(int column) {
        return 0;
    }

    @Override
    public boolean isNull(int column) {
        return false;
    }
}

