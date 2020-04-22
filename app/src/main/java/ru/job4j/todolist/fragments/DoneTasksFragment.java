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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import ru.job4j.todolist.DividerItemDecoration;
import ru.job4j.todolist.R;
import ru.job4j.todolist.adapter.DoneTaskAdapter;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class DoneTasksFragment extends Fragment implements View.OnClickListener {
    private DoneTaskAdapter adapter;
    private RecyclerView recycler;
    private SqlStore sqlStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.done_tasks_row, container, false);
        sqlStore = SqlStore.getInstance(getContext());
        adapter = new DoneTaskAdapter(Objects.requireNonNull(getContext()), getActivity());
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        FloatingActionButton fab = view.findViewById(R.id.fab_done);
        fab.setOnClickListener(this);
        addBottomAppBar(view);
        updateUI();
        return view;
    }

    private void addBottomAppBar(View view) {
        BottomAppBar bottomAppBar = view.findViewById(R.id.bottom_app_bar_done);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(bottomAppBar);
        }
        if (activity != null) {
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.done_bottomappbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlarmHelper alarmHelper = AlarmHelper.getInstance();
        if (item.getItemId() == R.id.remove_all_done_tasks) {
            List<Task> doneTasks = sqlStore.getDoneItems();
            for (Task task : doneTasks) {
                sqlStore.deleteSubtasks(task.getId());
                alarmHelper.removeAlarm(task.getId());
            }
            sqlStore.deleteAllDone();
            updateUI();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        List<Task> tasks = SqlStore.getInstance(getContext()).getDoneItems();
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
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), CurrentTasksActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
}
