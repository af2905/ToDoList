package ru.job4j.todolist.fragments;

import android.app.ActivityOptions;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import ru.job4j.todolist.DividerItemDecoration;
import ru.job4j.todolist.MyApplication;
import ru.job4j.todolist.R;
import ru.job4j.todolist.adapter.CurrentTaskAdapter;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class CurrentTasksFragment extends Fragment
        implements View.OnClickListener, SearchView.OnQueryTextListener {
    private CurrentTaskAdapter adapter;
    private RecyclerView recycler;
    private SqlStore sqlStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_tasks_row, container, false);
        AlarmHelper.getInstance().init(Objects.requireNonNull(getContext()).getApplicationContext());
        adapter = new CurrentTaskAdapter(getContext(), getActivity());
        sqlStore = SqlStore.getInstance(getContext());
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        BottomAppBar bottomAppBar = view.findViewById(R.id.bottom_app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(bottomAppBar);
        }
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        updateUI();
        return view;
    }

    private void updateUI() {
        List<Task> tasks = sqlStore.getCurrentItems();
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
        inflater.inflate(R.menu.current_bottomappbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        AlarmHelper alarmHelper = AlarmHelper.getInstance();
        switch (item.getItemId()) {
            case R.id.remove_all_current_tasks:
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()))
                        .setTitle(getResources().getString(R.string.removal_question_title))
                        .setMessage(getResources().getString(R.string.removal_question))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            List<Task> currentTasks = sqlStore.getCurrentItems();
                            for (Task task : currentTasks) {
                                sqlStore.deleteSubtasks(task.getId());
                                alarmHelper.removeAlarm(task.getId());
                            }
                            sqlStore.deleteAllCurrent();
                            updateUI();
                        })
                        .show();
                return true;
            case R.id.bottom_bar_done:
                intent = new Intent(getActivity(), DoneTasksActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                return true;
            case R.id.bottom_bar_sync:
                intent = new Intent(getActivity(), CurrentTasksActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AddActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Task> tasks = sqlStore.getSelectedItems(newText);
        adapter = new CurrentTaskAdapter(Objects.requireNonNull(getContext()), getActivity());
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
}
