package com.example.moviehub;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public final class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/moviehub.db";
    private static final Path DB_DIR = Paths.get("data");

    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws java.sql.SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initDatabase() {
        try {
            Files.createDirectories(DB_DIR);

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.execute("PRAGMA foreign_keys = ON");

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        username TEXT PRIMARY KEY,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL
                    )
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS movies (
                        title TEXT PRIMARY KEY,
                        genre TEXT NOT NULL,
                        year INTEGER NOT NULL,
                        rating REAL NOT NULL,
                        description TEXT NOT NULL,
                        video_path TEXT NOT NULL
                    )
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS favorites (
                        username TEXT NOT NULL,
                        movie_title TEXT NOT NULL,
                        PRIMARY KEY (username, movie_title),
                        FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
                        FOREIGN KEY (movie_title) REFERENCES movies(title) ON DELETE CASCADE
                    )
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS watch_later (
                        username TEXT NOT NULL,
                        movie_title TEXT NOT NULL,
                        PRIMARY KEY (username, movie_title),
                        FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
                        FOREIGN KEY (movie_title) REFERENCES movies(title) ON DELETE CASCADE
                    )
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL,
                        movie_title TEXT NOT NULL,
                        watched_at TEXT DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
                        FOREIGN KEY (movie_title) REFERENCES movies(title) ON DELETE CASCADE
                    )
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS ratings (
                        username TEXT NOT NULL,
                        movie_title TEXT NOT NULL,
                        rating REAL NOT NULL,
                        PRIMARY KEY (username, movie_title),
                        FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
                        FOREIGN KEY (movie_title) REFERENCES movies(title) ON DELETE CASCADE
                    )
                """);
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось инициализировать SQLite", e);
        }
    }
}