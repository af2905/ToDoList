package ru.job4j.todolist.fragments;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public class DoneTasksActivity extends BaseActivity {
    @Override
    public Fragment loadFrg() {
        return new DoneTasksFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CurrentTasksActivity.class);
        startActivity(intent);
    }
}
