package ru.job4j.todolist.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ToDoBaseHelper extends SQLiteOpenHelper {
    private static final String DB = "toDoList.db";
    private static final int VERSION = 1;

    ToDoBaseHelper(@Nullable Context context) {
        super(context, DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ToDoDbSchema.TasksTable.NAME + " ("
                + ToDoDbSchema.TasksTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToDoDbSchema.TasksTable.Cols.NAME + " TEXT, "
                + ToDoDbSchema.TasksTable.Cols.DESC + " TEXT, "
                + ToDoDbSchema.TasksTable.Cols.DATE + " LONG, "
                + ToDoDbSchema.TasksTable.Cols.ALARM + " LONG, "
                + ToDoDbSchema.TasksTable.Cols.DONE + " INTEGER"
                + ")"
        );

        db.execSQL("CREATE TABLE " + ToDoDbSchema.SubtasksTable.NAME + " ("
                + ToDoDbSchema.SubtasksTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToDoDbSchema.SubtasksTable.Cols.TASK_ID + " INTEGER, "
                + ToDoDbSchema.SubtasksTable.Cols.TEXT + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + ToDoDbSchema.TasksTable.NAME);
        onCreate(db);

        db.execSQL("DROP TABLE " + ToDoDbSchema.SubtasksTable.NAME);
        onCreate(db);
    }
}
