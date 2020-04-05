package ru.job4j.todolist.store;

final class ToDoDbSchema {
    static final class ToDoTable {
        static final String TABLE_NAME = "items";

        static final class Cols {
            static final String ID = "_id";
            static final String NAME = "taskName";
            static final String DESC = "taskDesc";
            static final String DATE = "taskDate";
            static final String ALARM = "taskAlarm";
            static final String DONE = "taskDone";
        }
    }
}