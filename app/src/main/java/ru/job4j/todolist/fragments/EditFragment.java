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

import ru.job4j.todolist.R;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.store.SqlStore;

public class EditFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private Button save;
    private EditText editName;
    private EditText editDesc;
    private int id;
    private SqlStore sqlStore = SqlStore.getInstance(getContext());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_add, container, false);
        id = getArguments().getInt("id");
        editName = view.findViewById(R.id.editName);
        editDesc = view.findViewById(R.id.editDesc);
        save = view.findViewById(R.id.save);
        save.setOnClickListener(this);
        editName.setText(sqlStore.getItem(id).getName());
        if (sqlStore.getItem(id).getDesc().equals("description not added")) {
            editDesc.setText("");
        } else {
            editDesc.setText(SqlStore.getInstance(getContext()).getItem(id).getDesc());
        }
        editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
        editDesc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
        editName.addTextChangedListener(this);
        return view;
    }

    static EditFragment of(int id) {
        EditFragment fragment = new EditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        Task task = sqlStore.getItem(id);
        String descText = editDesc.getText().toString();
        if (descText.length() == 0) {
            descText = "description not added";
        }
        task.setDesc(descText);
        task.setName(editName.getText().toString());
        sqlStore.updateItem(task);
        Intent intent = new Intent(getActivity().getApplicationContext(), TasksActivity.class);
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
