package com.example.moviehub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private static final List<Movie> movies = new ArrayList<>();

    static {
        DatabaseManager.getInstance();
        loadMovies();
        if (movies.isEmpty()) {
            seedDefaultMovies();
            saveMovies();
        }
    }

    private static void seedDefaultMovies() {
        movies.add(new Movie("Inception", "Sci-Fi", 2010, 8.8,
                "История о воровстве идей во сне.", "videos/inception.mp4"));
        movies.add(new Movie("Interstellar", "Sci-Fi", 2014, 8.7,
                "Путешествие через космос ради спасения человечества.", "videos/interstellar.mp4"));
        movies.add(new Movie("The Dark Knight", "Action", 2008, 9.0,
                "Бэтмен против Джокера.", "videos/dark_knight.mp4"));
        movies.add(new Movie("Avatar", "Fantasy", 2009, 7.9,
                "Новый мир, новые правила, новая война.", "videos/avatar.mp4"));
        movies.add(new Movie("Titanic", "Drama", 1997, 7.9,
                "Любовь на фоне трагедии.", "videos/titanic.mp4"));
        movies.add(new Movie("The Matrix", "Sci-Fi", 1999, 8.7,
                "Реальность — не то, чем кажется.", "videos/matrix.mp4"));
        movies.add(new Movie("Joker", "Drama", 2019, 8.4,
                "Путь Артура Флека к безумию.", "videos/joker.mp4"));
        movies.add(new Movie("Avengers: Endgame", "Action", 2019, 8.4,
                "Финал эпической битвы.", "videos/endgame.mp4"));
        movies.add(new Movie("Spider-Man: No Way Home", "Action", 2021, 8.2,
                "Несколько вселенных сталкиваются.", "videos/spiderman_nwh.mp4"));
        movies.add(new Movie("Toy Story", "Animation", 1995, 8.3,
                "Игрушки оживают, когда никто не видит.", "videos/toy_story.mp4"));
        movies.add(new Movie("Coco", "Animation", 2017, 8.4,
                "Музыка, семья и память.", "videos/coco.mp4"));
        movies.add(new Movie("The Godfather", "Crime", 1972, 9.2,
                "История семьи Корлеоне.", "videos/godfather.mp4"));
        movies.add(new Movie("Fight Club", "Drama", 1999, 8.8,
                "Тайный клуб и разрушение личности.", "videos/fight_club.mp4"));
        movies.add(new Movie("Forrest Gump", "Drama", 1994, 8.8,
                "Жизнь простого человека в необычное время.", "videos/forrest_gump.mp4"));
        movies.add(new Movie("The Lion King", "Animation", 1994, 8.5,
                "Путь Симбы к своему месту в мире.", "videos/lion_king.mp4"));
        movies.add(new Movie("Deadpool", "Comedy", 2016, 8.0,
                "Супергерой с очень странным чувством юмора.", "videos/deadpool.mp4"));
        movies.add(new Movie("Guardians of the Galaxy", "Action", 2014, 8.0,
                "Команда очень разных героев.", "videos/guardians.mp4"));
        movies.add(new Movie("Parasite", "Thriller", 2019, 8.5,
                "История о социальном неравенстве и тайнах.", "videos/parasite.mp4"));
        movies.add(new Movie("Whiplash", "Drama", 2014, 8.5,
                "Музыка, давление и амбиции.", "videos/whiplash.mp4"));
        movies.add(new Movie("Shrek", "Comedy", 2001, 7.9,
                "Огр, принцесса и очень необычное приключение.", "videos/shrek.mp4"));
    }

    private static void loadMovies() {
        String sql = "SELECT title, genre, year, rating, description, video_path FROM movies";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                movies.add(new Movie(
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("year"),
                        rs.getDouble("rating"),
                        rs.getString("description"),
                        rs.getString("video_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Movie> getMovies() {
        return movies;
    }

    public static void addMovie(Movie movie) {
        movies.add(movie);
        saveMovies();
    }

    public static void removeMovie(Movie movie) {
        movies.remove(movie);
        saveMovies();
    }

    public static void updateMovie(Movie oldMovie, Movie updatedMovie) {
        int index = movies.indexOf(oldMovie);
        if (index >= 0) {
            movies.set(index, updatedMovie);
            saveMovies();
        }
    }

    public static void saveMovies() {
        String deleteSql = "DELETE FROM movies";
        String insertSql = """
            INSERT INTO movies(title, genre, year, rating, description, video_path)
            VALUES(?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Movie movie : movies) {
                    insertStmt.setString(1, movie.getTitle());
                    insertStmt.setString(2, movie.getGenre());
                    insertStmt.setInt(3, movie.getYear());
                    insertStmt.setDouble(4, movie.getRating());
                    insertStmt.setString(5, movie.getDescription());
                    insertStmt.setString(6, movie.getVideoPath());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}