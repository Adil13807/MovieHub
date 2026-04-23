package com.example.moviehub;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private AuthService authService;
    private Runnable onLoginSuccess;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    private void onRegisterClicked() {
        if (authService == null) {
            messageLabel.setText("Ошибка авторизации");
            return;
        }

        boolean ok = authService.register(usernameField.getText(), passwordField.getText());
        if (ok) {
            messageLabel.setText("Регистрация успешна");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            messageLabel.setText("Не удалось зарегистрироваться");
        }
    }

    @FXML
    private void onLoginClicked() {
        if (authService == null) {
            messageLabel.setText("Ошибка авторизации");
            return;
        }

        boolean ok = authService.login(usernameField.getText(), passwordField.getText());
        if (ok) {
            messageLabel.setText("Вход выполнен");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            messageLabel.setText("Неверный логин или пароль");
        }
    }
}