package ru.job4j.todolist.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.todolist.model.Task;

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
    public void addItem(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = getContentValues(task);
        long id = db.insert(ToDoDbSchema.ToDoTable.TABLE_NAME, null, contentValues);
        task.setId((int) id);
        db.close();
    }

    @Override
    public Task getItem(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ToDoDbSchema.ToDoTable.TABLE_NAME,
                new String[]{
                        ToDoDbSchema.ToDoTable.Cols.ID,
                        ToDoDbSchema.ToDoTable.Cols.NAME,
                        ToDoDbSchema.ToDoTable.Cols.DESC,
                        ToDoDbSchema.ToDoTable.Cols.DATE,
                        ToDoDbSchema.ToDoTable.Cols.ALARM,
                        ToDoDbSchema.ToDoTable.Cols.DONE},
                ToDoDbSchema.ToDoTable.Cols.ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return new Task(
                cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.ID)),
                cursor.getString(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.NAME)),
                cursor.getString(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DESC)),
                cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DATE)),
                cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.ALARM)),
                cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DONE)));

    }

    @Override
    public List<Task> getSelectedItems(String text) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();
        String selectItems = "SELECT * FROM " + ToDoDbSchema.ToDoTable.TABLE_NAME
                + " WHERE " + ToDoDbSchema.ToDoTable.Cols.NAME
                + " LIKE '%" + text + "%'"
                + " OR " + ToDoDbSchema.ToDoTable.Cols.DATE
                + " LIKE '%" + text + "%'";
        Cursor cursor = db.rawQuery(selectItems, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.ID)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.NAME)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DESC)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DATE)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.ALARM)),
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DONE)));
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    @Override
    public List<Task> getAllItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();
        String selectAllItems = "SELECT * FROM " + ToDoDbSchema.ToDoTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAllItems, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.ID)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.NAME)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DESC)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DATE)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.ALARM)),
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.DONE)));
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    @Override
    public int updateItem(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = getContentValues(task);
        return db.update(ToDoDbSchema.ToDoTable.TABLE_NAME, contentValues,
                ToDoDbSchema.ToDoTable.Cols.ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    @Override
    public void deleteItem(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.ToDoTable.TABLE_NAME,
                ToDoDbSchema.ToDoTable.Cols.ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.ToDoTable.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    public int size() {
        return 0;
    }

    private static ContentValues getContentValues(Task task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDbSchema.ToDoTable.Cols.NAME, task.getName());
        contentValues.put(ToDoDbSchema.ToDoTable.Cols.DESC, task.getDesc());
        contentValues.put(ToDoDbSchema.ToDoTable.Cols.DATE, task.getDate());
        contentValues.put(ToDoDbSchema.ToDoTable.Cols.ALARM, task.getAlarm());
        contentValues.put(ToDoDbSchema.ToDoTable.Cols.DONE, task.getDone());
        return contentValues;
    }
}
