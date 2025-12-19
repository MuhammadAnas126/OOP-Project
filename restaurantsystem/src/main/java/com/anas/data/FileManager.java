package com.anas.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.anas.models.MenuItem;

public class FileManager {
    private static final String FILE_NAME = "menu.dat";

    public static void saveMenu(ArrayList<MenuItem> menu) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(menu);
        } catch (IOException e) {
            System.err.println("Failed to save menu: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<MenuItem> loadMenu() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (ArrayList<MenuItem>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>(); // Return empty list if file doesn't exist
        }
    }
}