package ru.job4j.todolist.fragments;

import androidx.fragment.app.Fragment;

public class CurrentTasksActivity extends BaseActivity {
    @Override
    public Fragment loadFrg() {
        return new CurrentTasksFragment();
    }
}
