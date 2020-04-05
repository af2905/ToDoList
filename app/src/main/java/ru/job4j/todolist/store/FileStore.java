package ru.job4j.todolist.store;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import ru.job4j.todolist.model.Task;

public class FileStore implements IStore {
    private static IStore instance;
    private int counter = 0;
    private Context context;

    private FileStore(Context context) {
        this.context = context;
    }

    public static IStore getInstance(Context context) {
        if (instance == null) {
            instance = new FileStore(context);
        }
        return instance;
    }


    @Override
    public void addItem(Task task) {
        File file = new File(context.getFilesDir(), (counter++) + ".txt");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            out.println(task.getName());
            out.println(task.getDesc());
            out.println(task.getDate());
            out.println(task.getAlarm());
            out.println(task.getDone());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int size() {
        return Objects.requireNonNull(context.getFilesDir().listFiles()).length;
    }

    @Override
    public Task getItem(int id) {
        Task task = new Task(null, null, 0, 0, 0);
        File file = new File(context.getFilesDir(), id + ".txt");
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            task.setName(in.readLine());
            task.setDesc(in.readLine());
            task.setDate(Long.parseLong(in.readLine()));
            task.setDate(Long.parseLong(in.readLine()));
            task.setDone(Integer.parseInt(in.readLine()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }

    @Override
    public List<Task> getSelectedItems(String text) {
        return null;
    }

    @Override
    public List<Task> getAllItems() {
        return null;
    }

    @Override
    public int updateItem(Task task) {
        return 0;
    }

    @Override
    public void deleteItem(Task task) {

    }

    @Override
    public void deleteAll() {

    }
}
