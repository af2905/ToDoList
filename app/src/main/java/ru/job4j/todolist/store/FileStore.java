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

import ru.job4j.todolist.model.Item;

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
    public void addItem(Item item) {
        File file = new File(context.getFilesDir(), (counter++) + ".txt");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            out.println(item.getName());
            out.println(item.getDesc());
            out.println(item.getCreated());
            out.println(item.getDone());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int size() {
        return Objects.requireNonNull(context.getFilesDir().listFiles()).length;
    }

    @Override
    public Item getItem(int id) {
        Item item = new Item(null, null, null, 0);
        File file = new File(context.getFilesDir(), id + ".txt");
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            item.setName(in.readLine());
            item.setDesc(in.readLine());
            item.setCreated(in.readLine());
            item.setDone(Integer.parseInt(in.readLine()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<Item> getSelectedItems(String text) {
        return null;
    }

    @Override
    public List<Item> getAllItems() {
        return null;
    }

    @Override
    public int updateItem(Item item) {
        return 0;
    }

    @Override
    public void deleteItem(Item item) {

    }

    @Override
    public void deleteAll() {

    }
}
