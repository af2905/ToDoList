package ru.job4j.todolist.fragments;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public class AddActivity extends BaseActivity {
    @Override
    public Fragment loadFrg() {
        return new AddFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CurrentTasksActivity.class);
        startActivity(intent);
    }
}
