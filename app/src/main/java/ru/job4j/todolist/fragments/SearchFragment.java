package ru.job4j.todolist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import ru.job4j.todolist.R;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.store.SqlStore;

public class SearchFragment extends Fragment implements View.OnClickListener {
    private EditText searchText;
    private Button search;
    private TextView matchesFound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find, container, false);
        searchText = view.findViewById(R.id.etSearchText);
        search = view.findViewById(R.id.btnSearch);
        search.setOnClickListener(this);
        matchesFound = view.findViewById(R.id.tvMatchesFound);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSearch) {
            String textMatchesFound = "";
            List<Item> items = SqlStore.getInstance(getContext())
                    .getSelectedItems(searchText.getText().toString());
            for (Item item : items) {
                String textFound = String.format(
                        "%s\n%s\n%s", item.getName(), item.getDesc(), item.getCreated());
                textMatchesFound += textFound + "\n\n";
            }
            matchesFound.setText(textMatchesFound);
            if (items.size() == 0) {
                matchesFound.setText("no matches found");
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}
