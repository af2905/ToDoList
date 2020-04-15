package ru.job4j.todolist.fragments;

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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment implements View.OnClickListener {
    private TextInputEditText addName;
    private MaterialButton addDate, addAlarm;
    private ImageView cancel;
    private SqlStore sqlStore;
    private long selectedDate = 0, selectedTime = 0;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_TIME = 0;
    private Calendar calendarDate;
    private Calendar calendarTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_new_task, container, false);
        sqlStore = SqlStore.getInstance(getContext());
        addName = view.findViewById(R.id.addName);
        calendarDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendarTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        addDate = view.findViewById(R.id.addDate);
        addDate.setText(Utils.getDate(
                calendarDate.getTimeInMillis()));
        addDate.setOnClickListener(this);
        addAlarm = view.findViewById(R.id.addAlarm);
        addAlarm.setVisibility(View.INVISIBLE);
        addAlarm.setOnClickListener(this);
        cancel = view.findViewById(R.id.cancel);
        cancel.setVisibility(View.INVISIBLE);
        cancel.setOnClickListener(this);
        addToolbar(view);
        final FloatingActionButton fab = view.findViewById(R.id.fab_save);
        fab.setOnClickListener(this);
        return view;
    }

    private void addToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.bottom_app_bar_add);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
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
        inflater.inflate(R.menu.add_bottomappbar_menu, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDate:
                showCalendarForDateSelection();
                break;
            case R.id.addAlarm:
                showCalendarForAlarmSelection();
                break;
            case R.id.cancel:
                selectedTime = 0;
                cancel.setVisibility(View.INVISIBLE);
                addAlarm.setVisibility(View.INVISIBLE);
                break;
            case R.id.fab_save:
                if (addName.length() == 0) {
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
                Task task = new Task(Objects.requireNonNull(addName.getText()).toString(), "",
                        selectedDate, selectedTime, 0);
                sqlStore.addItem(task);
                if (selectedTime != 0) {
                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setExactAlarm(task);
                }
                Intent intent = new Intent(
                        Objects.requireNonNull(getActivity())
                                .getApplicationContext(), CurrentTasksActivity.class);
                startActivity(intent);
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
                addAlarm.setText(Utils.getTime(calendarTime.getTimeInMillis()));
            }
            addAlarm.setVisibility(View.VISIBLE);
            addAlarm.setText(Utils.getTime(selectedTime));
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
                    addDate.setText(Utils.getDate(selection));
                    selectedDate = selection;
                    calendarDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendarDate.setTimeInMillis(selection);
                });
    }

    private void showCalendarForAlarmSelection() {
        FragmentManager manager = getFragmentManager();
        Calendar calendar = calendarDate;
        TimePickerFragment dialog = TimePickerFragment.newInstance(calendar);
        dialog.setTargetFragment(AddFragment.this, REQUEST_TIME);
        if (manager != null) {
            dialog.show(manager, DIALOG_TIME);
        }
    }
}
