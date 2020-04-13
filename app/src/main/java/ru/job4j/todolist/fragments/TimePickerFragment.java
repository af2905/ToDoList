package ru.job4j.todolist.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;

import ru.job4j.todolist.R;

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_TIME = "time";
    static final String EXTRA_TIME = "todoList.time";
    private TimePicker timePicker;

    static TimePickerFragment newInstance(Calendar time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendarDate = (Calendar) getArguments().getSerializable(ARG_TIME);
        final int year = calendarDate.get(Calendar.YEAR);
        final int month = calendarDate.get(Calendar.MONTH);
        final int day = calendarDate.get(Calendar.DAY_OF_MONTH);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        timePicker = view.findViewById(R.id.timePicker);
        return new MaterialAlertDialogBuilder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                    sendResult(Activity.RESULT_OK, calendar);
                })
                .setNegativeButton(android.R.string.cancel,
                        (dialog, which) -> sendResult(Activity.RESULT_CANCELED, null))
                .create();
    }

    private void sendResult(int resultCode, Calendar calendar) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, calendar);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

