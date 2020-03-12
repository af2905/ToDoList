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
        db.execSQL("CREATE TABLE " + ToDoDbSchema.ToDoTable.TABLE_NAME + " ("
                + ToDoDbSchema.ToDoTable.Cols.ID + " integer primary key autoincrement, "
                + ToDoDbSchema.ToDoTable.Cols.NAME + " TEXT, "
                + ToDoDbSchema.ToDoTable.Cols.DESC + " TEXT, "
                + ToDoDbSchema.ToDoTable.Cols.CREATED + " TEXT,"
                + ToDoDbSchema.ToDoTable.Cols.DONE + " INTEGER"
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
