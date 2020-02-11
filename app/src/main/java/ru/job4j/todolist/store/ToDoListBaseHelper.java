package ru.job4j.todolist.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ToDoListBaseHelper extends SQLiteOpenHelper {
    public static final String DB = "toDoList.db";
    public static final int VERSION = 1;

    public ToDoListBaseHelper(@Nullable Context context) {
        super(context, DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ToDoListDbSchema.ToDoListTable.TABLE_NAME
                + " (" + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToDoListDbSchema.ToDoListTable.Cols.ITEM_NAME + " TEXT,"
                + ToDoListDbSchema.ToDoListTable.Cols.ITEM_DESC + " TEXT,"
                + ToDoListDbSchema.ToDoListTable.Cols.CREATED + " TEXT" + " )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
