package ru.job4j.todolist.store;

public class ToDoListDbSchema {
    public static final class ToDoListTable {
        public static final String TABLE_NAME = "toDoList";

        public static final class Cols {
            public static final String ITEM_NAME = "itemName";
            public static final String ITEM_DESC = "itemDesc";
            public static final String CREATED = "itemCreated";
        }
    }
}
