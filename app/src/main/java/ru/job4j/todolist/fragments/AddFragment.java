package ru.job4j.todolist.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;

import ru.job4j.todolist.R;
import ru.job4j.todolist.Utils;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.store.SqlStore;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private EditText editName, editDesc;
    private Button save, editDate, editTime;
    private ImageView photo;
    private SqlStore store;
    private long selectedDate;
    private long selectedTime;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_TIME = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add, container, false);
        editName = view.findViewById(R.id.editName);
        editDesc = view.findViewById(R.id.editDesc);
        editDate = view.findViewById(R.id.editDate);
        editTime = view.findViewById(R.id.editTime);
        editDate.setOnClickListener(this);
        editTime.setOnClickListener(this);
        save = view.findViewById(R.id.save);
        save.setEnabled(false);
        save.setOnClickListener(this);
        photo = view.findViewById(R.id.photo);
        photo.setOnClickListener(this);
        editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
        editDesc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
        editName.addTextChangedListener(this);
        store = SqlStore.getInstance(getContext());
        return view;
    }

    @Override
    public void onClick(View v) {
        AppCompatActivity activity;
        Intent intent;
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
                            }
                        });
                break;
            case R.id.editTime:
                FragmentManager manager = getFragmentManager();
                Calendar calendar = Calendar.getInstance();
                TimePickerFragment dialog = TimePickerFragment.newInstance(calendar);
                dialog.setTargetFragment(AddFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
                break;
            case R.id.save:
                String descText = editDesc.getText().toString();
                if (descText.length() == 0) {
                    descText = "description not added";
                }
                store.addItem(new Item(editName.getText().toString(),
                        descText,
                        Utils.getDate(selectedDate) + " " + Utils.getTime(selectedTime),
                        0));
                intent = new Intent(getActivity().getApplicationContext(), ItemsActivity.class);
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
            Calendar calendar = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            selectedTime = calendar.getTimeInMillis();
            editTime.setText(Utils.getTime(calendar.getTimeInMillis()));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            save.setEnabled(false);
            editName.setError("The field must not be empty");
        } else {
            save.setEnabled(true);
            editName.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
