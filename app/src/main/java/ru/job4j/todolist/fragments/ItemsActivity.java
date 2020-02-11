package ru.job4j.todolist.fragments;

import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import ru.job4j.todolist.R;
import ru.job4j.todolist.store.MemStore;

public class ItemsActivity extends BaseActivity
        implements DeleteDialogFragment.DeleteDialogListener {
    @Override
    public Fragment loadFrg() {
        return new ItemsFragment();
    }

    @Override
    public void onPositiveDialogClick(DialogFragment fragment) {
        MemStore.getStore().getAllItems().clear();
        TextView name = findViewById(R.id.name);
        name.setText("");
        TextView desc = findViewById(R.id.description);
        desc.setText("");
        TextView created = findViewById(R.id.created);
        created.setText("");
    }

    @Override
    public void onNegativeDialogClick(DialogFragment fragment) {

    }
}
