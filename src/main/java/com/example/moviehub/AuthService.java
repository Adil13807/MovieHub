package com.example.moviehub;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;

    private final Path usersFile = Paths.get("data", "users.txt");

    public AuthService() {
        loadUsers();
        seedAdmins();
        saveUsers();
    }

    public boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        if (users.containsKey(username)) {
            return false;
        }

        User user = new User(username.trim(), password.trim(), User.Role.USER);
        users.put(user.getUsername(), user);
        currentUser = user;
        saveUsers();
        return true;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }
        if (!user.getPassword().equals(password)) {
            return false;
        }
        currentUser = user;
        return true;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }

    private void seedAdmins() {
        addAdminIfMissing("admin1", "admin123");
        addAdminIfMissing("admin2", "admin123");
        addAdminIfMissing("admin3", "admin123");
    }

    private void addAdminIfMissing(String username, String password) {
        users.putIfAbsent(username, new User(username, password, User.Role.ADMIN));
    }

    private void loadUsers() {
        try {
            if (!Files.exists(usersFile)) {
                Files.createDirectories(usersFile.getParent());
                return;
            }

            List<String> lines = Files.readAllLines(usersFile);
            for (String line : lines) {
                if (line.isBlank()) continue;

                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    User.Role role = User.Role.valueOf(parts[2]);
                    users.put(username, new User(username, password, role));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try {
            Files.createDirectories(usersFile.getParent());

            StringBuilder sb = new StringBuilder();
            for (User user : users.values()) {
                sb.append(user.getUsername())
                        .append(";")
                        .append(user.getPassword())
                        .append(";")
                        .append(user.getRole())
                        .append(System.lineSeparator());
            }

            Files.writeString(usersFile, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}