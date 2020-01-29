package ru.job4j.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class AddFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private Button save;
    private EditText editName;
    private EditText editDesc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add, container, false);
        editName = view.findViewById(R.id.editName);
        editDesc = view.findViewById(R.id.editDesc);
        save = view.findViewById(R.id.save);
        save.setEnabled(false);
        save.setOnClickListener(this);
        editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        editDesc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        editName.addTextChangedListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        String descText = editDesc.getText().toString();
        if (descText.length() == 0) {
            descText = "description not added";
        }
        Store.getStore().add(new Item(
                editName.getText().toString(),
                descText,
                Calendar.getInstance()));
        Intent intent = new Intent(getActivity().getApplicationContext(), ItemsActivity.class);
        startActivity(intent);
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
