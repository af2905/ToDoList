package ru.job4j.todolist;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Locale;

public class ItemsFragment extends Fragment {
    private final RecyclerView.Adapter adapter = new ItemAdapter();
    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.items, container, false);
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.items, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete_item:
                DialogFragment dialog = new DeleteDialogFragment();
                dialog.show(getActivity().getSupportFragmentManager(), "deleteDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            final Item item = Store.getStore().get(position);
            final TextView name = holder.itemView.findViewById(R.id.name);
            name.setText(String.format("%s. %s", position + 1, item.getName()));
            final TextView desc = holder.itemView.findViewById(R.id.description);
            desc.setText(item.getDesc());
            final TextView created = holder.itemView.findViewById(R.id.created);
            created.setText(format(item.getCreated()));
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!item.isDone()) {
                        Intent intent = new Intent(getActivity(), EditActivity.class);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                }
            });

            final CheckBox done = holder.itemView.findViewById(R.id.done);
            done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TextView tvDone = getActivity().findViewById(R.id.tvDone);
                    if (isChecked) {
                        item.setDone(true);
                        tvDone.setText(R.string.done);
                        tvDone.setTextColor(getResources().getColor(android.R.color.background_dark));
                        setPaintFlagsAndColor(name);
                        setPaintFlagsAndColor(desc);
                        setPaintFlagsAndColor(created);

                    } else {
                        item.setDone(false);
                        tvDone.setText("");
                        removePaintFlagsAndColor(name);
                        removePaintFlagsAndColor(desc);
                        removePaintFlagsAndColor(created);
                    }
                }
            });
        }

        private String format(Calendar cal) {
            int month = cal.get(Calendar.MONTH);
            int correctMonth = month++;
            return String.format(Locale.getDefault(), "%02d.%02d.%d",
                    cal.get(Calendar.DAY_OF_MONTH), cal.get(correctMonth), cal.get(Calendar.YEAR));
        }

        @Override
        public int getItemCount() {
            return Store.getStore().size();
        }
    }

    private void setPaintFlagsAndColor(TextView text) {
        text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        text.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void removePaintFlagsAndColor(TextView text) {
        text.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        text.setTextColor(getResources().getColor(android.R.color.background_dark));
    }
}

