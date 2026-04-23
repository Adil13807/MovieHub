package com.example.moviehub;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminController {

    @FXML private ListView<Movie> movieListView;
    @FXML private TextField titleField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField ratingField;
    @FXML private TextArea descriptionField;
    @FXML private TextField videoPathField;
    @FXML private Label statusLabel;

    private final ObservableList<Movie> movies = FXCollections.observableArrayList();
    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        updateStatus();
    }

    @FXML
    public void initialize() {
        movies.setAll(MovieRepository.getMovies());
        movieListView.setItems(movies);

        movieListView.getSelectionModel().selectedItemProperty().addListener((obs, oldMovie, newMovie) -> {
            if (newMovie != null) {
                fillFields(newMovie);
            }
        });
    }

    private void fillFields(Movie movie) {
        titleField.setText(movie.getTitle());
        genreField.setText(movie.getGenre());
        yearField.setText(String.valueOf(movie.getYear()));
        ratingField.setText(String.valueOf(movie.getRating()));
        descriptionField.setText(movie.getDescription());
        videoPathField.setText(movie.getVideoPath());
    }

    private void updateStatus() {
        if (authService != null && authService.getCurrentUser() != null) {
            User user = authService.getCurrentUser();
            statusLabel.setText("Вошёл: " + user.getUsername() + " [" + user.getRole() + "]");
        }
    }

    @FXML
    private void onAddMovieClicked() {
        try {
            Movie movie = readMovieFromFields();
            MovieRepository.addMovie(movie);
            movies.setAll(MovieRepository.getMovies());
            statusLabel.setText("Фильм добавлен: " + movie.getTitle());
        } catch (Exception e) {
            statusLabel.setText("Ошибка добавления фильма");
        }
    }

    @FXML
    private void onEditMovieClicked() {
        Movie selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Сначала выбери фильм");
            return;
        }

        try {
            Movie updated = readMovieFromFields();
            MovieRepository.updateMovie(selected, updated);
            movies.setAll(MovieRepository.getMovies());
            statusLabel.setText("Фильм обновлён: " + updated.getTitle());
        } catch (Exception e) {
            statusLabel.setText("Ошибка редактирования фильма");
        }
    }

    @FXML
    private void onDeleteMovieClicked() {
        Movie selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Сначала выбери фильм");
            return;
        }

        MovieRepository.removeMovie(selected);
        movies.setAll(MovieRepository.getMovies());
        clearFields();
        statusLabel.setText("Фильм удалён: " + selected.getTitle());
    }

    private Movie readMovieFromFields() {
        String title = titleField.getText();
        String genre = genreField.getText();
        int year = Integer.parseInt(yearField.getText());
        double rating = Double.parseDouble(ratingField.getText());
        String description = descriptionField.getText();
        String videoPath = videoPathField.getText();

        if (title.isBlank() || genre.isBlank() || description.isBlank() || videoPath.isBlank()) {
            throw new IllegalArgumentException("Пустые поля");
        }

        return new Movie(title, genre, year, rating, description, videoPath);
    }

    private void clearFields() {
        titleField.clear();
        genreField.clear();
        yearField.clear();
        ratingField.clear();
        descriptionField.clear();
        videoPathField.clear();
    }
}