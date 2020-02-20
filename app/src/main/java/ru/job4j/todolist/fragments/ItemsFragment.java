package ru.job4j.todolist.fragments;

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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.job4j.todolist.R;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.store.SqlStore;

public class ItemsFragment extends Fragment {
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.items, container, false);
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        //loadStore();
        return view;
    }

    private void updateUI() {
        List<Item> items = SqlStore.getInstance(getContext()).getAllItems();
        adapter = new ItemAdapter(items);
        recycler.setAdapter(adapter);
    }

  /*  private void loadStore() {
        String selection = "a";
        Cursor cursor = this.getActivity().getContentResolver()
                .query(StoreContentProvider.CONTENT_URI, null,
                        selection, null,
                        null, null);
        try {
            while (cursor.moveToNext()) {
                Log.d("ContentProvider", cursor.getString(1));
            }
        } finally {
            cursor.close();
        }
    }*/

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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_item:
                intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
                return true;
            case R.id.find_item:
                intent = new Intent(getActivity(), SearchActivity.class);
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
        private final List<Item> items;

        private ItemAdapter(List<Item> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            final Item item = items.get(position);
            final TextView name = holder.itemView.findViewById(R.id.name);
            name.setText(item.getName());
            final TextView desc = holder.itemView.findViewById(R.id.description);
            desc.setText(item.getDesc());
            final TextView created = holder.itemView.findViewById(R.id.created);
            created.setText(item.getCreated());
            final ImageButton editOneItem = holder.itemView.findViewById(R.id.editOneItemBtn);
            editOneItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!item.isDone()) {
                        Intent intent = new Intent(getActivity(), EditActivity.class);
                        intent.putExtra("id", item.getId());
                        startActivity(intent);
                    }
                }
            });

            final ImageButton deleteOneItem = holder.itemView.findViewById(R.id.deleteOneItemBtn);
            deleteOneItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SqlStore.getInstance(getContext()).deleteItem(item);
                    updateUI();
                }
            });

            final CheckBox done = holder.itemView.findViewById(R.id.done);
            done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        item.setDone(true);
                        setPaintFlagsAndColor(name);
                        setPaintFlagsAndColor(desc);
                        setPaintFlagsAndColor(created);

                    } else {
                        item.setDone(false);
                        removePaintFlagsAndColor(name);
                        removePaintFlagsAndColor(desc);
                        removePaintFlagsAndColor(created);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return SqlStore.getInstance(getContext()).getAllItems().size();
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

