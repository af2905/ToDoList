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
import androidx.fragment.app.Fragment;

import ru.job4j.todolist.CalendarFormat;
import ru.job4j.todolist.R;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.store.SqlStore;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private EditText editName;
    private EditText editDesc;
    private ImageView photo;
    private Button save;
    private SqlStore store;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add, container, false);
        editName = view.findViewById(R.id.editName);
        editDesc = view.findViewById(R.id.editDesc);
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
        Intent intent;
        switch (v.getId()) {
            case R.id.save:
                String descText = editDesc.getText().toString();
                if (descText.length() == 0) {
                    descText = "description not added";
                }
                store.addItem(new Item(editName.getText().toString(),
                        descText,
                        CalendarFormat.dateFormatMethod(), 0));
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().length() == 0) {
            editName.setError("The field must not be empty");
            save.setEnabled(false);
        } else if (s.toString().length() != 0) {
            editName.setError(null);
            save.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
