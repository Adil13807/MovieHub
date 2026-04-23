package com.example.moviehub;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (authService != null && authService.getCurrentUser() != null) {
            usernameField.setText(authService.getCurrentUser().getUsername());
        }
    }

    @FXML
    private void onSaveClicked() {
        statusLabel.setText("Профиль обновлён");
    }
}