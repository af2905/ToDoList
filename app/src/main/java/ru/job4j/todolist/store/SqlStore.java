package ru.job4j.todolist.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.todolist.model.Item;

public class SqlStore implements IStore {
    private static SqlStore sqlStore;
    private ToDoBaseHelper dbHelper;
    private Context context;

    private SqlStore(Context context) {
        this.context = context.getApplicationContext();
        dbHelper = new ToDoBaseHelper(this.context);
    }

    public static SqlStore getInstance(Context context) {
        if (sqlStore == null) {
            sqlStore = new SqlStore(context);
        }
        return sqlStore;
    }

    @Override
    public void addItem(Item item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = getContentValues(item);
        long id = db.insert(ToDoDbSchema.ToDoTable.TABLE_NAME, null, contentValues);
        item.setId((int) id);
        db.close();
    }

    @Override
    public Item getItem(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ToDoDbSchema.ToDoTable.TABLE_NAME,
                new String[]{
                        ToDoDbSchema.ToDoTable.Cols.ID,
                        ToDoDbSchema.ToDoTable.Cols.NAME,
                        ToDoDbSchema.ToDoTable.Cols.DESC,
                        ToDoDbSchema.ToDoTable.Cols.CREATED},
                ToDoDbSchema.ToDoTable.Cols.ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return new Item(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));
    }

    @Override
    public List<Item> getAllItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Item> items = new ArrayList<>();
        String selectAllItems = "SELECT * FROM " + ToDoDbSchema.ToDoTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAllItems, null);
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3));
                items.add(item);
            } while (cursor.moveToNext());
        }
        return items;
    }

    @Override
    public int updateItem(Item item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = getContentValues(item);
        return db.update(ToDoDbSchema.ToDoTable.TABLE_NAME, contentValues,
                ToDoDbSchema.ToDoTable.Cols.ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    @Override
    public void deleteItem(Item item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.ToDoTable.TABLE_NAME,
                ToDoDbSchema.ToDoTable.Cols.ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.ToDoTable.TABLE_NAME, null, null);
        db.close();
    }

    private static ContentValues getContentValues(Item item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDbSchema.ToDoTable.Cols.NAME, item.getName());
        contentValues.put(ToDoDbSchema.ToDoTable.Cols.DESC, item.getDesc());
        contentValues.put(ToDoDbSchema.ToDoTable.Cols.CREATED, item.getCreated());
        return contentValues;
    }
}
