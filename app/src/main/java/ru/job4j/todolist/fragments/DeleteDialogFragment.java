package ru.job4j.todolist.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ru.job4j.todolist.R;

public class DeleteDialogFragment extends DialogFragment {
    private DeleteDialogListener callback;

    public interface DeleteDialogListener {
        void onPositiveDialogClick(DialogFragment fragment);

        void onNegativeDialogClick(DialogFragment fragment);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(getActivity())
                .setMessage(R.string.removal_warning)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositiveDialogClick(DeleteDialogFragment.this);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onNegativeDialogClick(DeleteDialogFragment.this);
                    }
                }).create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format(
                    "%s must implement DeleteDialogListener", context.toString()));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
