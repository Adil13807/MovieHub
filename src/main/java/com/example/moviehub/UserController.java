package com.example.moviehub;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreBox;
    @FXML private ListView<Movie> movieListView;

    @FXML private Label titleLabel;
    @FXML private Label genreLabel;
    @FXML private Label yearLabel;
    @FXML private Label ratingLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Label statusLabel;

    private final ObservableList<Movie> allMovies = FXCollections.observableArrayList();
    private final ObservableList<Movie> filteredMovies = FXCollections.observableArrayList();

    private AuthService authService;
    private UserDataService userDataService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        updateStatus();
    }

    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @FXML
    private void onOpenLibraryClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("library-view.fxml"));
            Scene scene = new Scene(loader.load(), 700, 500);

            LibraryController controller = loader.getController();
            controller.setAuthService(authService);
            controller.setUserDataService(userDataService);

            Stage stage = new Stage();
            stage.setTitle("Мои данные");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Не удалось открыть окно");
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        allMovies.addAll(MovieRepository.getMovies());
        filteredMovies.addAll(allMovies);

        movieListView.setItems(filteredMovies);

        genreBox.getItems().addAll(
                "All",
                "Action",
                "Drama",
                "Sci-Fi",
                "Fantasy",
                "Animation",
                "Crime",
                "Comedy",
                "Thriller"
        );
        genreBox.getSelectionModel().selectFirst();

        movieListView.getSelectionModel().selectedItemProperty().addListener((obs, oldMovie, newMovie) -> {
            if (newMovie != null) {
                showMovieDetails(newMovie);
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> applyFilters());
        genreBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        if (!filteredMovies.isEmpty()) {
            movieListView.getSelectionModel().selectFirst();
        }
    }

    private void updateStatus() {
        if (authService != null && authService.getCurrentUser() != null) {
            User user = authService.getCurrentUser();
            statusLabel.setText("Вошёл: " + user.getUsername() + " [" + user.getRole() + "]");
        }
    }

    private void applyFilters() {
        String search = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String genre = genreBox.getValue();

        filteredMovies.setAll(allMovies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(search))
                .filter(movie -> "All".equals(genre) || movie.getGenre().equalsIgnoreCase(genre))
                .toList());

        if (filteredMovies.isEmpty()) {
            clearDetails();
            statusLabel.setText("Ничего не найдено");
        } else {
            movieListView.getSelectionModel().selectFirst();
        }
    }

    private void showMovieDetails(Movie movie) {
        titleLabel.setText(movie.getTitle());
        genreLabel.setText("Жанр: " + movie.getGenre());
        yearLabel.setText("Год: " + movie.getYear());
        ratingLabel.setText("Рейтинг: " + movie.getRating());
        descriptionArea.setText(movie.getDescription());
    }

    private void clearDetails() {
        titleLabel.setText("Выберите фильм");
        genreLabel.setText("Жанр:");
        yearLabel.setText("Год:");
        ratingLabel.setText("Рейтинг:");
        descriptionArea.clear();
    }

    @FXML
    private void onWatchClicked() {
        Movie selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Сначала выбери фильм");
            return;
        }

        try {
            if (authService != null && authService.getCurrentUser() != null && userDataService != null) {
                userDataService.addToHistory(authService.getCurrentUser().getUsername(), selected.getTitle());
            }

            var url = getClass().getResource("/" + selected.getVideoPath());
            if (url == null) {
                statusLabel.setText("Видео не найдено: " + selected.getVideoPath());
                return;
            }

            java.awt.Desktop.getDesktop().open(new java.io.File(url.toURI()));
            statusLabel.setText("Открывается: " + selected.getTitle());
        } catch (Exception e) {
            statusLabel.setText("Не удалось открыть видео");
            e.printStackTrace();
        }
    }

    @FXML
    private void onFavoriteClicked() {
        Movie selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Сначала выбери фильм");
            return;
        }

        if (authService != null && authService.getCurrentUser() != null && userDataService != null) {
            userDataService.addToFavorites(authService.getCurrentUser().getUsername(), selected.getTitle());
        }

        statusLabel.setText("Добавлено в избранное: " + selected.getTitle());
    }

    @FXML
    private void onWatchLaterClicked() {
        Movie selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Сначала выбери фильм");
            return;
        }

        if (authService != null && authService.getCurrentUser() != null && userDataService != null) {
            userDataService.addToWatchLater(authService.getCurrentUser().getUsername(), selected.getTitle());
        }

        statusLabel.setText("Добавлено в 'Смотреть позже': " + selected.getTitle());
    }

    @FXML
    private void onRateClicked() {
        Movie selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Сначала выбери фильм");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Оценка фильма");
        dialog.setHeaderText("Поставь оценку от 1 до 10");
        dialog.setContentText("Оценка:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                double rating = Double.parseDouble(value);
                if (rating < 1 || rating > 10) {
                    statusLabel.setText("Оценка должна быть от 1 до 10");
                    return;
                }

                if (authService != null && authService.getCurrentUser() != null && userDataService != null) {
                    userDataService.saveRating(authService.getCurrentUser().getUsername(), selected.getTitle(), rating);
                }

                statusLabel.setText("Оценка сохранена: " + selected.getTitle() + " = " + rating);
            } catch (NumberFormatException e) {
                statusLabel.setText("Введите число");
            }
        });
    }
}