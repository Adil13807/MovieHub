package com.example.moviehub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private User currentUser;

    public AuthService() {
        DatabaseManager.getInstance();
        seedAdmins();
    }

    public boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        username = username.trim();
        password = password.trim();

        if (userExists(username)) {
            return false;
        }

        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, User.Role.USER.name());
            ps.executeUpdate();

            currentUser = new User(username, password, User.Role.USER);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        String sql = "SELECT username, password, role FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                if (!rs.getString("password").equals(password)) {
                    return false;
                }

                currentUser = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        User.Role.valueOf(rs.getString("role"))
                );
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }

    private boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void seedAdmins() {
        addAdminIfMissing("admin1", "admin123");
        addAdminIfMissing("admin2", "admin123");
        addAdminIfMissing("admin3", "admin123");
    }

    private void addAdminIfMissing(String username, String password) {
        if (userExists(username)) {
            return;
        }

        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, User.Role.ADMIN.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}