package ru.job4j.todolist;

import android.database.AbstractCursor;

public class StoreCursor extends AbstractCursor {
    private final Store store;
    private String selection;

    StoreCursor(Store store, String selection) {
        this.store = store;
        this.selection = selection;
    }

    @Override
    public int getCount() {
        return store.getAll().size();
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"_ID", "NAME"};
    }

    @Override
    public String getString(int column) {
        Item item = store.get(getPosition());
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

