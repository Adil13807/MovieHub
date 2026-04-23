package com.example.moviehub;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class UserDataService {

    private final Path baseDir = Paths.get("data", "users");

    public UserDataService() {
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String safeName(String username) {
        return username.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private Path favoritesFile(String username) {
        return baseDir.resolve(safeName(username) + "_favorites.txt");
    }

    private Path watchLaterFile(String username) {
        return baseDir.resolve(safeName(username) + "_watch_later.txt");
    }

    private Path ratingsFile(String username) {
        return baseDir.resolve(safeName(username) + "_ratings.txt");
    }

    private Path historyFile(String username) {
        return baseDir.resolve(safeName(username) + "_history.txt");
    }

    public void addToFavorites(String username, String movieTitle) {
        appendUnique(favoritesFile(username), movieTitle);
    }

    public void addToWatchLater(String username, String movieTitle) {
        appendUnique(watchLaterFile(username), movieTitle);
    }

    public void addToHistory(String username, String movieTitle) {
        appendLine(historyFile(username), movieTitle);
    }

    public void saveRating(String username, String movieTitle, double rating) {
        Map<String, String> ratings = loadKeyValueFile(ratingsFile(username));
        ratings.put(movieTitle, String.valueOf(rating));
        saveKeyValueFile(ratingsFile(username), ratings);
    }

    public void removeFromFavorites(String username, String movieTitle) {
        removeLine(favoritesFile(username), movieTitle);
    }

    public void removeFromWatchLater(String username, String movieTitle) {
        removeLine(watchLaterFile(username), movieTitle);
    }

    public void clearHistory(String username) {
        try {
            Files.deleteIfExists(historyFile(username));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> loadFavorites(String username) {
        return readLines(favoritesFile(username));
    }

    public List<String> loadWatchLater(String username) {
        return readLines(watchLaterFile(username));
    }

    public List<String> loadHistory(String username) {
        return readLines(historyFile(username));
    }

    public Map<String, String> loadRatings(String username) {
        return loadKeyValueFile(ratingsFile(username));
    }

    private void removeLine(Path file, String value) {
        try {
            List<String> lines = readLines(file);
            lines.removeIf(line -> line.equals(value));
            Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendUnique(Path file, String value) {
        try {
            List<String> lines = readLines(file);
            if (!lines.contains(value)) {
                lines.add(value);
                Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendLine(Path file, String value) {
        try {
            Files.write(file,
                    List.of(value),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> readLines(Path file) {
        try {
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Files.readAllLines(file));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private Map<String, String> loadKeyValueFile(Path file) {
        Map<String, String> map = new HashMap<>();
        try {
            if (!Files.exists(file)) {
                return map;
            }
            for (String line : Files.readAllLines(file)) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void saveKeyValueFile(Path file, Map<String, String> map) {
        try {
            List<String> lines = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                lines.add(entry.getKey() + ";" + entry.getValue());
            }
            Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}