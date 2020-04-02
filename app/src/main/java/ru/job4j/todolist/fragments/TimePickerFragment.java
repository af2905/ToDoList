package ru.job4j.todolist.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

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
        Calendar time = (Calendar) getArguments().getSerializable(ARG_TIME);
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setHour(hour);
        timePicker.setMinute(minute);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                        calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        sendResult(Activity.RESULT_OK, calendar);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
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

