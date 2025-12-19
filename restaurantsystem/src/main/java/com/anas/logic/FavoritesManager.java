package com.anas.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static final String FILE_NAME = "favorites.txt";

    // Load favorites from file
    public static ArrayList<String> loadFavorites() {
        ArrayList<String> favs = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(FILE_NAME))) {
                favs.addAll(Files.readAllLines(Paths.get(FILE_NAME)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return favs;
    }

    // Toggle: If exists -> Remove. If not -> Add.
    public static void toggleFavorite(String itemName) {
        ArrayList<String> favs = loadFavorites();
        
        if (favs.contains(itemName)) {
            favs.remove(itemName); // Remove if already there
        } else {
            favs.add(itemName);    // Add if not there
        }
        
        saveFavorites(favs);
    }

    // Check if an item is a favorite
    public static boolean isFavorite(String itemName) {
        return loadFavorites().contains(itemName);
    }

    private static void saveFavorites(List<String> favs) {
        try {
            Files.write(Paths.get(FILE_NAME), favs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}