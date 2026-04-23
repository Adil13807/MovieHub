package com.example.moviehub;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private static final List<Movie> movies = new ArrayList<>();
    private static final Path moviesFile = Paths.get("data", "movies.txt");

    static {
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
        try {
            if (!Files.exists(moviesFile)) {
                Files.createDirectories(moviesFile.getParent());
                return;
            }

            List<String> lines = Files.readAllLines(moviesFile);
            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", 6);
                if (parts.length == 6) {
                    movies.add(new Movie(
                            parts[0],
                            parts[1],
                            intParse(parts[2], 2000),
                            doubleParse(parts[3], 0.0),
                            parts[4],
                            parts[5]
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int intParse(String value, int def) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return def;
        }
    }

    private static double doubleParse(String value, double def) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return def;
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
        try {
            Files.createDirectories(moviesFile.getParent());

            List<String> lines = new ArrayList<>();
            for (Movie movie : movies) {
                lines.add(
                        movie.getTitle() + ";" +
                                movie.getGenre() + ";" +
                                movie.getYear() + ";" +
                                movie.getRating() + ";" +
                                movie.getDescription() + ";" +
                                movie.getVideoPath()
                );
            }

            Files.write(moviesFile, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}