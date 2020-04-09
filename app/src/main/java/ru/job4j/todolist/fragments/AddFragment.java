package ru.job4j.todolist.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.LayoutInflater;
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
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.alarm.AlarmHelper;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment implements View.OnClickListener {
    private TextInputEditText addName, addNotes;
    private MaterialButton save, addDate, addAlarm;
    private ImageView photo, cancelDate;
    private SqlStore sqlStore;
    private long selectedDate = 0, selectedTime = 0;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_TIME = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Calendar calendarDate;
    private Calendar calendarTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add, container, false);
        sqlStore = SqlStore.getInstance(getContext());
        addName = view.findViewById(R.id.addName);
        addNotes = view.findViewById(R.id.addNotes);
        addName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        addDate = view.findViewById(R.id.addDate);
        addAlarm = view.findViewById(R.id.addAlarm);
        addDate.setOnClickListener(this);
        addAlarm.setOnClickListener(this);
        cancelDate = view.findViewById(R.id.cancelDate);
        cancelDate.setOnClickListener(this);
        cancelDate.setVisibility(View.INVISIBLE);
        save = view.findViewById(R.id.saveAdd);
        save.setOnClickListener(this);
        photo = view.findViewById(R.id.photo);
        photo.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        AppCompatActivity activity;
        Intent intent;
        Calendar calendar;
        switch (v.getId()) {
            case R.id.addDate:
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                MaterialDatePicker<Long> picker = builder.build();
                activity = (AppCompatActivity) getActivity();
                picker.show(activity.getSupportFragmentManager(), picker.toString());
                picker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                addDate.setText(Utils.getDate(selection));
                                selectedDate = selection;
                                calendarDate = Calendar.getInstance();
                                calendarDate.setTimeInMillis(selection);
                                cancelDate.setVisibility(View.VISIBLE);
                            }
                        });
                break;
            case R.id.addAlarm:
                FragmentManager manager = getFragmentManager();
                if (selectedDate == 0) {
                    Toast.makeText(getContext(), R.string.choose_date, Toast.LENGTH_SHORT).show();
                    break;
                }
                calendar = calendarDate;
                TimePickerFragment dialog = TimePickerFragment.newInstance(calendar);
                dialog.setTargetFragment(AddFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
                addDate.setEnabled(false);
                break;
            case R.id.cancelDate:
                addDate.setText(R.string.date);
                addDate.setEnabled(true);
                selectedDate = 0;
                selectedTime = 0;
                addAlarm.setText(R.string.time);
                break;
            case R.id.saveAdd:
                if (addName.length() == 0) {
                    Toast.makeText(getContext(), R.string.enter_task_name, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (selectedDate == 0) {
                    selectedDate = Calendar.getInstance().getTimeInMillis();
                }
                Task task = new Task(addName.getText().toString(), addNotes.getText().toString(),
                        selectedDate, selectedTime, 0);
                sqlStore.addItem(task);
                if (selectedTime != 0) {
                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setExactAlarm(task);
                }
                intent = new Intent(getActivity().getApplicationContext(), TasksActivity.class);
                startActivity(intent);
                break;
            case R.id.photo:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
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
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(imageBitmap);
        }
        if (requestCode == REQUEST_TIME) {
            calendarTime = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            selectedTime = calendarTime.getTimeInMillis();
            addAlarm.setText(Utils.getTime(calendarTime.getTimeInMillis()));
        }
    }
}
