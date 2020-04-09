package ru.job4j.todolist.fragments;

import androidx.fragment.app.Fragment;

public class EditActivity extends BaseActivity {
    @Override
    public Fragment loadFrg() {
        return EditFragment.of(
                getIntent().getIntExtra("position", 0),
                getIntent().getIntExtra("id", 0),
                getIntent().getLongExtra("date", 0),
                getIntent().getLongExtra("alarm", 0)
        );
    }
}
