package ru.job4j.todolist;

import androidx.fragment.app.Fragment;

public class EditActivity extends BaseActivity{
    @Override
    public Fragment loadFrg() {
        return new EditFragment();
    }
}
