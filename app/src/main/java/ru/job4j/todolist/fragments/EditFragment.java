package ru.job4j.todolist.fragments;

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

import ru.job4j.todolist.R;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.store.MemStore;

public class EditFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private Button save;
    private EditText editName;
    private EditText editDesc;
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add, container, false);
        position = getArguments().getInt("position", 0);
        editName = view.findViewById(R.id.editName);
        editDesc = view.findViewById(R.id.editDesc);
        save = view.findViewById(R.id.save);
        save.setOnClickListener(this);
        editName.setText(MemStore.getStore().getItem(position).getName());
        if (MemStore.getStore().getItem(position).getDesc().equals("description not added")) {
            editDesc.setText("");
        } else {
            editDesc.setText(MemStore.getStore().getItem(position).getDesc());
        }
        editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        editDesc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        editName.addTextChangedListener(this);
        return view;
    }

    static EditFragment of(int position) {
        EditFragment fragment = new EditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        String descText = editDesc.getText().toString();
        if (descText.length() == 0) {
            descText = "description not added";
        }
        MemStore.getStore().set(position, new Item(
                editName.getText().toString(),
                descText,
                Calendar.getInstance()));
        Intent intent = new Intent(getActivity().getApplicationContext(), ItemsActivity.class);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (s.toString().equals("")) {
            save.setEnabled(false);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().length() == 0) {
            save.setEnabled(false);
            editName.setError("The field must not be empty");
        } else if (s.toString().length() != 0) {
            save.setEnabled(true);
            editName.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
