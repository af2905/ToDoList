package ru.job4j.todolist.store;

final class ToDoDbSchema {
    static final class ToDoTable {
        static final String TABLE_NAME = "items";

        static final class Cols {
            static final String ID = "_id";
            static final String NAME = "itemName";
            static final String DESC = "itemDesc";
            static final String CREATED = "itemCreated";
        }
    }
}