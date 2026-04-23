package com.example.moviehub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDataService {

    public void addToFavorites(String username, String movieTitle) {
        executeInsertIgnore("INSERT OR IGNORE INTO favorites(username, movie_title) VALUES(?, ?)", username, movieTitle);
    }

    public void addToWatchLater(String username, String movieTitle) {
        executeInsertIgnore("INSERT OR IGNORE INTO watch_later(username, movie_title) VALUES(?, ?)", username, movieTitle);
    }

    public void addToHistory(String username, String movieTitle) {
        String sql = "INSERT INTO history(username, movie_title) VALUES(?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, movieTitle);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveRating(String username, String movieTitle, double rating) {
        String sql = """
            INSERT INTO ratings(username, movie_title, rating)
            VALUES(?, ?, ?)
            ON CONFLICT(username, movie_title) DO UPDATE SET rating = excluded.rating
        """;
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, movieTitle);
            ps.setDouble(3, rating);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromFavorites(String username, String movieTitle) {
        deletePair("DELETE FROM favorites WHERE username = ? AND movie_title = ?", username, movieTitle);
    }

    public void removeFromWatchLater(String username, String movieTitle) {
        deletePair("DELETE FROM watch_later WHERE username = ? AND movie_title = ?", username, movieTitle);
    }

    public void clearHistory(String username) {
        String sql = "DELETE FROM history WHERE username = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> loadFavorites(String username) {
        return loadMovieTitles("SELECT movie_title FROM favorites WHERE username = ? ORDER BY movie_title", username);
    }

    public List<String> loadWatchLater(String username) {
        return loadMovieTitles("SELECT movie_title FROM watch_later WHERE username = ? ORDER BY movie_title", username);
    }

    public List<String> loadHistory(String username) {
        return loadMovieTitles("SELECT movie_title FROM history WHERE username = ? ORDER BY id DESC", username);
    }

    public Map<String, String> loadRatings(String username) {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT movie_title, rating FROM ratings WHERE username = ? ORDER BY movie_title";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("movie_title"), String.valueOf(rs.getDouble("rating")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    private void executeInsertIgnore(String sql, String username, String movieTitle) {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, movieTitle);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deletePair(String sql, String username, String movieTitle) {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, movieTitle);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadMovieTitles(String sql, String username) {
        List<String> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}