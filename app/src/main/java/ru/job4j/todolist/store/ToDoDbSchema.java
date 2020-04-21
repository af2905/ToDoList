package ru.job4j.todolist.store;

import android.provider.BaseColumns;

final class ToDoDbSchema implements BaseColumns {
    static final class TasksTable {
        static final String NAME = "tasks";

        static final class Cols {
            static final String ID = BaseColumns._ID;
            static final String NAME = "taskName";
            static final String DESC = "taskDesc";
            static final String DATE = "taskDate";
            static final String ALARM = "taskAlarm";
            static final String DONE = "taskDone";
        }
    }

    static final class SubtasksTable {
        static final String NAME = "subtasks";

        static final class Cols {
            static final String ID = BaseColumns._ID;
            static final String TASK_ID = "taskId";
            static final String TEXT = "subtaskText";
        }
    }
}