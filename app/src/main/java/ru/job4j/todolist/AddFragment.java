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

public class AddFragment extends Fragment {
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
        editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        editDesc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    save.setEnabled(false);
                } else if (!editName.getText().toString().equals("")) {
                    save.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName = getActivity().findViewById(R.id.editName);
                editDesc = getActivity().findViewById(R.id.editDesc);
                String descText = editDesc.getText().toString();

                if (descText.equals("")) {
                    descText = "description not added.";
                }
                Store.getStore().add(new Item(
                        editName.getText().toString(),
                        descText,
                        Calendar.getInstance()));
                Intent intent = new Intent(getActivity().getApplicationContext(), ItemsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
