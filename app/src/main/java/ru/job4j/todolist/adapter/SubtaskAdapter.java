package ru.job4j.todolist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.todolist.R;
import ru.job4j.todolist.model.Subtask;

public class SubtaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Subtask> subtasks = new ArrayList<>();

    public SubtaskAdapter() {
    }

    public SubtaskAdapter(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        notifyItemInserted(getItemCount() - 1);
    }

    private void removeSubtask(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            subtasks.remove(location);
            notifyItemRemoved(location);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subtask_design, parent, false);
        TextView text = view.findViewById(R.id.subtask_text);
        ImageView cancel = view.findViewById(R.id.subtask_cancel);
        return new SubtaskAdapter.SubtaskViewHolder(view, text, cancel);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Subtask subtask = subtasks.get(position);
        final SubtaskAdapter.SubtaskViewHolder subtaskViewHolder = (SubtaskViewHolder) holder;
        subtaskViewHolder.text.setText(subtask.getText());
        subtaskViewHolder.cancel.setOnClickListener(v -> removeSubtask(subtaskViewHolder.getLayoutPosition()));
    }

    @Override
    public int getItemCount() {
        return subtasks.size();
    }

    private static class SubtaskViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView cancel;

        SubtaskViewHolder(@NonNull View itemView, TextView text, ImageView cancel) {
            super(itemView);
            this.text = text;
            this.cancel = cancel;
        }
    }
}