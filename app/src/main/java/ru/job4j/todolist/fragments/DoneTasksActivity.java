package ru.job4j.todolist.fragments;

import androidx.fragment.app.Fragment;

public class DoneTasksActivity extends BaseActivity {
    @Override
    public Fragment loadFrg() {
        return new DoneTasksFragment();
    }
}
