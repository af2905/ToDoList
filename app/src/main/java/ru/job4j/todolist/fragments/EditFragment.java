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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.adapter.CurrentTaskAdapter;
import ru.job4j.todolist.adapter.SubtaskAdapter;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Subtask;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

import static android.app.Activity.RESULT_OK;

public class EditFragment extends Fragment implements View.OnClickListener {
    private TextInputEditText editName, addSubtaskTxt;
    private MaterialButton editDate;
    private MaterialButton editAlarm;
    private SqlStore sqlStore;
    private long selectedDate, selectedTime;
    private ImageView cancel;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_TIME = 1;
    private Calendar calendarDate;
    private Calendar calendarTime;
    private int position;
    private int id;
    private CurrentTaskAdapter currentTaskAdapter;
    private SubtaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_task, container, false);
        sqlStore = SqlStore.getInstance(getContext());
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            selectedDate = getArguments().getLong("date");
            selectedTime = getArguments().getLong("alarm");
            id = getArguments().getInt("id");
        }
        calendarDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendarTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        editName = view.findViewById(R.id.editName);
        editName.setText(sqlStore.getItem(id).getName());
        editDate = view.findViewById(R.id.editDate);
        editDate.setText(Utils.getDate(selectedDate));
        editDate.setOnClickListener(this);
        editAlarm = view.findViewById(R.id.editAlarm);
        cancel = view.findViewById(R.id.cancel);
        if (selectedTime == 0) {
            editAlarm.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
        } else {
            editAlarm.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            editAlarm.setText(Utils.getTime(selectedTime));
        }
        editAlarm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        currentTaskAdapter = new CurrentTaskAdapter(Objects.requireNonNull(getContext()), getActivity());
        addBottomAppBar(view);
        final FloatingActionButton fab = view.findViewById(R.id.fab_save);
        fab.setOnClickListener(this);
        addSubtaskTxt = view.findViewById(R.id.addSubtaskText);
        MaterialButton addSubtaskBtn = view.findViewById(R.id.addSubtaskButton);
        addSubtaskBtn.setOnClickListener(this);
        List<Subtask> subtasks = sqlStore.getSubtasks(id);
        adapter = new SubtaskAdapter(subtasks);
        RecyclerView recycler = view.findViewById(R.id.recycler_subtasks);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
        return view;
    }

    private void addBottomAppBar(View view) {
        BottomAppBar bottomAppBar = view.findViewById(R.id.bottom_app_bar_edit);
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
        inflater.inflate(R.menu.edit_bottomappbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_bar_date:
                showCalendarForDateSelection();
                return true;
            case R.id.bottom_bar_alarm:
                showCalendarForAlarmSelection();
                return true;
            case R.id.bottom_bar_delete:
                sqlStore.deleteItem(sqlStore.getItem(id));
                sqlStore.deleteSubtasks(id);
                currentTaskAdapter.removeItem(position);
                Intent intent = new Intent(Objects.requireNonNull(getActivity())
                        .getApplicationContext(), CurrentTasksActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    static EditFragment of(int position, int id, long date, long alarm) {
        EditFragment fragment = new EditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("id", id);
        bundle.putLong("date", date);
        bundle.putLong("alarm", alarm);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editDate:
                showCalendarForDateSelection();
                break;
            case R.id.editAlarm:
                showCalendarForAlarmSelection();
                break;
            case R.id.cancel:
                selectedTime = 0;
                cancel.setVisibility(View.INVISIBLE);
                editAlarm.setVisibility(View.INVISIBLE);
                break;
            case R.id.addSubtaskButton:
                if (Objects.requireNonNull(addSubtaskTxt.getText()).length() == 0) {
                    Toast.makeText(getContext(), R.string.enter_subtask_name, Toast.LENGTH_SHORT).show();
                    break;
                }
                adapter.addSubtask(new Subtask(addSubtaskTxt.getText().toString()));
                addSubtaskTxt.setText(null);
                break;
            case R.id.fab_save:
                if (editName.length() == 0) {
                    Toast.makeText(getContext(), R.string.enter_task_name, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (selectedDate == 0) {
                    selectedDate = calendarDate.getTimeInMillis();
                }
                if (selectedTime != 0) {
                    calendarDate.setTimeInMillis(selectedDate);
                    calendarTime.setTimeInMillis(selectedTime);
                    if (calendarDate.get(Calendar.DAY_OF_YEAR) != calendarTime.get(Calendar.DAY_OF_YEAR)) {
                        calendarTime.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH));
                        calendarTime.set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH));
                        selectedTime = calendarTime.getTimeInMillis();
                    }
                }
                String containsSubtasks = "0";
                if (adapter.getItemCount() != 0) {
                    containsSubtasks = "1";
                }
                Task task = sqlStore.getItem(id);
                task.setName(Objects.requireNonNull(editName.getText()).toString());
                task.setDesc(containsSubtasks);
                task.setDate(selectedDate);
                task.setAlarm(selectedTime);
                task.setDone(0);
                sqlStore.updateItem(task);
                sqlStore.deleteSubtasks(id);
                List<Subtask> subtasks = adapter.getSubtasks();
                for (Subtask subtask : subtasks) {
                    sqlStore.addSubtask(subtask, task.getId());
                }
                if (selectedTime != 0) {
                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setExactAlarm(task);
                }
                Intent intent = new Intent(Objects.requireNonNull(getActivity())
                        .getApplicationContext(), CurrentTasksActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_TIME) {
            if (data != null) {
                calendarTime = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            }
            if (calendarTime != null) {
                selectedTime = calendarTime.getTimeInMillis();
            }
            if (calendarTime != null) {
                editAlarm.setText(Utils.getTime(calendarTime.getTimeInMillis()));
            }
            editAlarm.setVisibility(View.VISIBLE);
            editAlarm.setText(Utils.getTime(selectedTime));
            cancel.setVisibility(View.VISIBLE);
        }
    }

    private void showCalendarForDateSelection() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker<Long> picker = builder.build();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            picker.show(activity.getSupportFragmentManager(), picker.toString());
        }
        picker.addOnPositiveButtonClickListener(
                selection -> {
                    editDate.setText(Utils.getDate(selection));
                    selectedDate = selection;
                    calendarDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendarDate.setTimeInMillis(selection);
                });
    }

    private void showCalendarForAlarmSelection() {
        FragmentManager manager = getFragmentManager();
        if (calendarDate == null) {
            Date date = new Date(selectedDate);
            calendarDate.setTime(date);
        }
        Calendar calendar = calendarDate;
        TimePickerFragment dialog = TimePickerFragment.newInstance(calendar);
        dialog.setTargetFragment(EditFragment.this, REQUEST_TIME);
        if (manager != null) {
            dialog.show(manager, DIALOG_TIME);
        }
    }
}
