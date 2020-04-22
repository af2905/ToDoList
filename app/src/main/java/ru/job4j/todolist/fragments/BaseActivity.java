package ru.job4j.todolist.fragments;

import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.job4j.todolist.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition transition = new Explode();
        transition.setDuration(1500);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
        setContentView(R.layout.host_frg);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.content) == null) {
            fm.beginTransaction().add(R.id.content, loadFrg())
                    .commit();
        }
    }

    public abstract Fragment loadFrg();
}
