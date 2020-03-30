package ru.job4j.todolist.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ru.job4j.todolist.DividerItemDecoration;
import ru.job4j.todolist.R;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.store.SqlStore;

public class ItemsFragment extends Fragment implements View.OnClickListener {
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private RecyclerView recycler;
    private final static String TAG = "log";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.items, container, false);
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(6);
        recycler.addItemDecoration(decoration);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    fab.show();
                } else if (dy > 0) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        updateUI();
        return view;
    }

    private void updateUI() {
        int id = R.anim.layout_animation_slide_right;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), id);
        recycler.setLayoutAnimation(animation);
        List<Item> items = SqlStore.getInstance(getContext()).getAllItems();
        adapter = new ItemAdapter(items);
        recycler.setAdapter(adapter);
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AddActivity.class);
        startActivity(intent);
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
            final SqlStore store = SqlStore.getInstance(getContext());
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
                    if (item.getDone() == 0) {
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
                    store.deleteItem(item);
                    // updateUI();
                    runLayoutAnimation(recycler);
                }
            });

            final CheckBox done = holder.itemView.findViewById(R.id.done);
            if (item.getDone() == 1) {
                done.setChecked(true);
            }
            done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        item.setDone(1);
                        store.updateItem(item);
                    } else {
                        item.setDone(0);
                        store.updateItem(item);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        int id = R.anim.layout_animation_fall_down;
        final LayoutAnimationController animation =
                AnimationUtils.loadLayoutAnimation(getContext(), id);
        recyclerView.setLayoutAnimation(animation);
        List<Item> items = SqlStore.getInstance(getContext()).getAllItems();
        adapter = new ItemAdapter(items);
        recycler.setAdapter(adapter);
    }

       /* private void loadStore() {
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
}
