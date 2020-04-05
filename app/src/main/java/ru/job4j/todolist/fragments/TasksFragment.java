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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ru.job4j.todolist.DividerItemDecoration;
import ru.job4j.todolist.MyApplication;
import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class TasksFragment extends Fragment
        implements View.OnClickListener, SearchView.OnQueryTextListener {
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks, container, false);
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources()
                .getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        updateUI();
        return view;
    }

    private void updateUI() {
        RecyclerView.ItemDecoration decoration
                = new DividerItemDecoration(8, 16);
        recycler.addItemDecoration(decoration);
        int id = R.anim.layout_animation_slide_right;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), id);
        recycler.setLayoutAnimation(animation);
        List<Task> tasks = SqlStore.getInstance(getContext()).getAllItems();
        adapter = new TaskAdapter(tasks);
        recycler.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_item) {
            DialogFragment dialog = new DeleteDialogFragment();
            dialog.show(getActivity().getSupportFragmentManager(), "deleteDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        adapter = new TaskAdapter(tasks);
        recycler.setAdapter(adapter);
        return false;
    }

    private final class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<Task> tasks;

        private TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.new_task, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            final SqlStore store = SqlStore.getInstance(getContext());
            final Task task = tasks.get(position);
            final TextView name = holder.itemView.findViewById(R.id.name);
            name.setText(task.getName());

            final TextView date = holder.itemView.findViewById(R.id.date);
            if (task.getDate() != 0) {
                date.setText(String.valueOf(Utils.getDate(task.getDate())));
            }

            final ImageView imgAlarm = holder.itemView.findViewById(R.id.imgAlarm);
            final TextView alarm = holder.itemView.findViewById(R.id.alarmOnOff);
            if (task.getAlarm() != 0) {
                alarm.setText(String.valueOf(Utils.getTime(task.getAlarm())));
                imgAlarm.setImageResource(R.drawable.ic_alarm_dark_gray_24dp);
                imgAlarm.setColorFilter(getResources()
                        .getColor(R.color.colorAccent, getActivity().getTheme()));
            }

            final ImageView imgNotes = holder.itemView.findViewById(R.id.imgNotes);
            if (task.getDesc().length() != 0) {
                imgNotes.setImageResource(R.drawable.ic_short_text_dark_gray_24dp);
                imgNotes.setColorFilter(getResources()
                        .getColor(R.color.colorAccent, getActivity().getTheme()));
            }

            final CheckBox done = holder.itemView.findViewById(R.id.done);
            if (task.getDone() == 1) {
                done.setChecked(true);
            }
            done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        task.setDone(1);
                        store.updateItem(task);
                    } else {
                        task.setDone(0);
                        store.updateItem(task);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }
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
