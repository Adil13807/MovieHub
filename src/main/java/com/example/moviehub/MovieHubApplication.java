package com.example.moviehub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MovieHubApplication extends Application {

    private final AuthService authService = new AuthService();
    private final UserDataService userDataService = new UserDataService();

    @Override
    public void start(Stage stage) throws IOException {
        showLoginWindow(stage);
    }

    private void showLoginWindow(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MovieHubApplication.class.getResource("LoginDialog.fxml"));
        Scene scene = new Scene(loader.load(), 360, 280);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        LoginController controller = loader.getController();
        controller.setAuthService(authService);
        controller.setOnLoginSuccess(() -> {
            try {
                if (authService.isAdmin()) {
                    showAdminWindow(stage);
                } else {
                    showUserWindow(stage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stage.setTitle("Login - MovieHub");
        stage.setScene(scene);
        stage.show();
    }

    private void showAdminWindow(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MovieHubApplication.class.getResource("admin-view.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        AdminController controller = loader.getController();
        controller.setAuthService(authService);

        stage.setTitle("MovieHub - Admin");
        stage.setScene(scene);
        stage.show();
    }

    private void showUserWindow(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MovieHubApplication.class.getResource("user-view.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        UserController controller = loader.getController();
        controller.setAuthService(authService);
        controller.setUserDataService(userDataService);

        stage.setTitle("MovieHub - User");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}