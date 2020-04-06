package ru.job4j.todolist.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

import static android.app.Activity.RESULT_OK;

public class EditFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private TextInputEditText editName, editNotes;
    private MaterialButton save, delete, editDate, editAlarm;
    private SqlStore sqlStore;
    private long selectedDate, selectedTime;
    private ImageView cancelDate, cancelAlarm;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_TIME = 2;
    private Calendar calendarDate;
    private Calendar calendarTime;
    private int id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit, container, false);
        sqlStore = SqlStore.getInstance(getContext());
        id = getArguments().getInt("id");
        selectedDate = getArguments().getLong("date");
        selectedTime = getArguments().getLong("alarm");
        editName = view.findViewById(R.id.editName);
        editNotes = view.findViewById(R.id.editNotes);
        save = view.findViewById(R.id.saveEdit);
        save.setOnClickListener(this);
        delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(this);
        editName.setText(sqlStore.getItem(id).getName());
        editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        editName.addTextChangedListener(this);
        editNotes.setText(sqlStore.getItem(id).getDesc());
        editDate = view.findViewById(R.id.editDate);
        if (selectedDate == 0) {
            editDate.setText(R.string.date);
        } else {
            editDate.setText(Utils.getDate(selectedDate));
        }
        editDate.setOnClickListener(this);
        editAlarm = view.findViewById(R.id.editAlarm);
        if (selectedTime == 0) {
            editAlarm.setText(R.string.time);
        } else {
            editAlarm.setText(Utils.getTime(selectedTime));
        }
        editAlarm.setOnClickListener(this);
        cancelDate = view.findViewById(R.id.cancelDate);
        cancelDate.setOnClickListener(this);
        cancelAlarm = view.findViewById(R.id.cancelAlarm);
        cancelAlarm.setOnClickListener(this);
        addToolbar(view);
        return view;
    }

    private void addToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarBack);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    static EditFragment of(int id, long date, long alarm) {
        EditFragment fragment = new EditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putLong("date", date);
        bundle.putLong("alarm", alarm);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        AppCompatActivity activity;
        Intent intent;
        Task task;
        AlarmHelper alarmHelper;
        switch (v.getId()) {
            case R.id.editDate:
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                MaterialDatePicker<Long> picker = builder.build();
                activity = (AppCompatActivity) getActivity();
                picker.show(activity.getSupportFragmentManager(), picker.toString());
                picker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                editDate.setText(Utils.getDate(selection));
                                selectedDate = selection;
                                calendarDate = Calendar.getInstance();
                                calendarDate.setTimeInMillis(selection);
                                editAlarm.setEnabled(true);
                            }
                        });
                break;
            case R.id.editAlarm:
                FragmentManager manager = getFragmentManager();
                Calendar calendar;
                if (calendarDate == null) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(selectedDate);
                } else {
                    calendar = calendarDate;
                }
                TimePickerFragment dialog = TimePickerFragment.newInstance(calendar);
                dialog.setTargetFragment(EditFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
                editDate.setEnabled(false);
                break;
            case R.id.saveEdit:
                task = sqlStore.getItem(id);
                task.setName(editName.getText().toString());
                task.setDesc(editNotes.getText().toString());
                task.setDate(selectedDate);
                task.setAlarm(selectedTime);
                task.setDone(0);
                sqlStore.updateItem(task);
                if (selectedTime != 0) {
                    alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                }
                intent = new Intent(getActivity().getApplicationContext(), TasksActivity.class);
                startActivity(intent);
                break;
            case R.id.cancelDate:
                editDate.setText(R.string.date);
                selectedDate = 0;
                alarmHelper = AlarmHelper.getInstance();
                alarmHelper.removeAlarm(id);
                editAlarm.setText(R.string.time);
                selectedTime = 0;
                editAlarm.setEnabled(false);
                break;
            case R.id.cancelAlarm:
                alarmHelper = AlarmHelper.getInstance();
                alarmHelper.removeAlarm(id);
                editAlarm.setText(R.string.time);
                selectedTime = 0;
                break;
            case R.id.delete:
                intent = new Intent(getActivity().getApplicationContext(), TasksActivity.class);
                startActivity(intent);
                sqlStore.deleteItem(sqlStore.getItem(id));
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
            calendarTime = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            selectedTime = calendarTime.getTimeInMillis();
            editAlarm.setText(Utils.getTime(calendarTime.getTimeInMillis()));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            save.setEnabled(false);
            editName.setError(getResources().getString(R.string.helper_text));
        } else {
            save.setEnabled(true);
            editName.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
