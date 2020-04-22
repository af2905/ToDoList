package ru.job4j.todolist.fragments;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ru.job4j.todolist.R;

public class CurrentTasksActivity extends BaseActivity {
    @Override
    public Fragment loadFrg() {
        return new CurrentTasksFragment();
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.really_exit)
                .setMessage(R.string.exit_question)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    CurrentTasksActivity.super.onBackPressed();
                    quit();
                }).create().show();
    }

    public void quit() {
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
    }
}
