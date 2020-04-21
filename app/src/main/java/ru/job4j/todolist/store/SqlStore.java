package ru.job4j.todolist.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.todolist.model.Subtask;
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
        long id = db.insert(ToDoDbSchema.TasksTable.NAME, null, contentValues);
        task.setId((int) id);
        db.close();
    }

    @Override
    public Task getItem(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ToDoDbSchema.TasksTable.NAME,
                new String[]{
                        ToDoDbSchema.TasksTable.Cols.ID,
                        ToDoDbSchema.TasksTable.Cols.NAME,
                        ToDoDbSchema.TasksTable.Cols.DESC,
                        ToDoDbSchema.TasksTable.Cols.DATE,
                        ToDoDbSchema.TasksTable.Cols.ALARM,
                        ToDoDbSchema.TasksTable.Cols.DONE},
                ToDoDbSchema.TasksTable.Cols.ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return new Task(
                cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.ID)),
                cursor.getString(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.NAME)),
                cursor.getString(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DESC)),
                cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DATE)),
                cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.ALARM)),
                cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DONE)));
    }

    @Override
    public List<Task> getSelectedItems(String text) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();
        String selectItems = "SELECT * FROM " + ToDoDbSchema.TasksTable.NAME
                + " WHERE " + ToDoDbSchema.TasksTable.Cols.NAME
                + " LIKE '%" + text + "%'";
        Cursor cursor = db.rawQuery(selectItems, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.ID)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.NAME)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DESC)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DATE)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.ALARM)),
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DONE)));
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
        String selectAllItems = "SELECT * FROM " + ToDoDbSchema.TasksTable.NAME;
        Cursor cursor = db.rawQuery(selectAllItems, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.ID)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.NAME)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DESC)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DATE)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.ALARM)),
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DONE)));
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
        return db.update(ToDoDbSchema.TasksTable.NAME, contentValues,
                ToDoDbSchema.TasksTable.Cols.ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    @Override
    public void deleteItem(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.TasksTable.NAME,
                ToDoDbSchema.TasksTable.Cols.ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.TasksTable.NAME, null, null);
        db.close();
    }

    public void deleteAllCurrent() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.TasksTable.NAME,
                ToDoDbSchema.TasksTable.Cols.DONE + " =? ", new String[]{Integer.toString(0)});
        db.close();
    }

    public void deleteAllDone() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.TasksTable.NAME,
                ToDoDbSchema.TasksTable.Cols.DONE + " =? ", new String[]{Integer.toString(1)});
        db.close();
    }

    @Override
    public int size() {
        return 0;
    }


    public List<Task> getCurrentItems() {
        String s = " 0";
        return getCurrentOrDoneItemsMethod(s);
    }

    public List<Task> getDoneItems() {
        String s = " 1";
        return getCurrentOrDoneItemsMethod(s);
    }

    private List<Task> getCurrentOrDoneItemsMethod(String number) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();
        String select = "SELECT * FROM " + ToDoDbSchema.TasksTable.NAME
                + " WHERE " + ToDoDbSchema.TasksTable.Cols.DONE
                + " = " + number;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.ID)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.NAME)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DESC)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DATE)),
                        cursor.getLong(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.ALARM)),
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.TasksTable.Cols.DONE)));
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    private static ContentValues getContentValues(Task task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDbSchema.TasksTable.Cols.NAME, task.getName());
        contentValues.put(ToDoDbSchema.TasksTable.Cols.DESC, task.getDesc());
        contentValues.put(ToDoDbSchema.TasksTable.Cols.DATE, task.getDate());
        contentValues.put(ToDoDbSchema.TasksTable.Cols.ALARM, task.getAlarm());
        contentValues.put(ToDoDbSchema.TasksTable.Cols.DONE, task.getDone());
        return contentValues;
    }


    public void addSubtask(Subtask subtask, int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDbSchema.SubtasksTable.Cols.TASK_ID, taskId);
        contentValues.put(ToDoDbSchema.SubtasksTable.Cols.TEXT, subtask.getText());
        long id = db.insert(ToDoDbSchema.SubtasksTable.NAME, null, contentValues);
        subtask.setId((int) id);
        db.close();
    }

    public List<Subtask> getSubtasks(int taskId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Subtask> subtasks = new ArrayList<>();
        String select = "SELECT * FROM " + ToDoDbSchema.SubtasksTable.NAME
                + " WHERE " + ToDoDbSchema.SubtasksTable.Cols.TASK_ID
                + " = " + taskId;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                Subtask subtask = new Subtask(
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.SubtasksTable.Cols.ID)),
                        cursor.getInt(cursor.getColumnIndex(ToDoDbSchema.SubtasksTable.Cols.TASK_ID)),
                        cursor.getString(cursor.getColumnIndex(ToDoDbSchema.SubtasksTable.Cols.TEXT)));
                subtasks.add(subtask);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subtasks;
    }

    public void deleteSubtasks(int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoDbSchema.SubtasksTable.NAME,
                ToDoDbSchema.SubtasksTable.Cols.TASK_ID + " =? ",
                new String[]{Integer.toString(taskId)});
        db.close();
    }
}
