package ru.job4j.todolist.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ru.job4j.todolist.DividerItemDecoration;
import ru.job4j.todolist.MyApplication;
import ru.job4j.todolist.R;
import ru.job4j.todolist.adapter.TaskAdapter;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class TasksFragment extends Fragment
        implements View.OnClickListener, SearchView.OnQueryTextListener {
    private TaskAdapter adapter;
    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_row, container, false);
        AlarmHelper.getInstance().init(getContext().getApplicationContext());
        adapter = new TaskAdapter(getContext(), getActivity());
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        Toolbar toolbar = view.findViewById(R.id.bottom_app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        updateUI();
        return view;
    }

    private void updateUI() {
        List<Task> tasks = SqlStore.getInstance(getContext()).getAllItems();
        addTasksFromDB(tasks);
        RecyclerView.ItemDecoration decoration
                = new DividerItemDecoration(8, 16);
        recycler.addItemDecoration(decoration);
        int id = R.anim.layout_animation_slide_right;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), id);
        recycler.setLayoutAnimation(animation);
        recycler.setAdapter(adapter);
    }

    private void addTasksFromDB(List<Task> tasks) {
        adapter.removeAllItems();
        for (int i = 0; i < tasks.size(); i++) {
            adapter.addSortedTask(tasks.get(i));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_bottomappbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_all_tasks:
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getResources().getString(R.string.removal_question_title))
                        .setMessage(getResources().getString(R.string.removal_question))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            SqlStore.getInstance(getContext()).deleteAll();
                            updateUI();
                        })
                        .show();
                return true;
            case R.id.bottom_bar_done:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AddActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Task> tasks = SqlStore.getInstance(getContext())
                .getSelectedItems(newText);
        adapter = new TaskAdapter(getContext(), getActivity());
        addTasksFromDB(tasks);
        recycler.setAdapter(adapter);
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    /* private void loadStore() {
        String selection = "a";
        Cursor cursor = this.getActivity().getContentResolver()
                .query(StoreContentProvider.CONTENT_URI, null,
                        selection, null,
                        null, null);
        try {
            while (cursor.moveToNext()) {
                Log.d("ContentProvider", cursor.getString(1));
            }
        } finally {
            cursor.close();
        }
    }*/
}
