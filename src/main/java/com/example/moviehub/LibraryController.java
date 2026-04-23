package com.example.moviehub;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class LibraryController {

    @FXML private ListView<String> favoritesList;
    @FXML private ListView<String> watchLaterList;
    @FXML private ListView<String> historyList;

    private AuthService authService;
    private UserDataService userDataService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        loadData();
    }

    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
        loadData();
    }

    private String username() {
        return authService != null && authService.getCurrentUser() != null
                ? authService.getCurrentUser().getUsername()
                : null;
    }

    private void loadData() {
        String user = username();
        if (user == null || userDataService == null) {
            return;
        }

        favoritesList.setItems(FXCollections.observableArrayList(userDataService.loadFavorites(user)));
        watchLaterList.setItems(FXCollections.observableArrayList(userDataService.loadWatchLater(user)));
        historyList.setItems(FXCollections.observableArrayList(userDataService.loadHistory(user)));
    }

    @FXML
    private void onRemoveFavoriteClicked() {
        String user = username();
        String selected = favoritesList.getSelectionModel().getSelectedItem();
        if (user != null && selected != null) {
            userDataService.removeFromFavorites(user, selected);
            loadData();
        }
    }

    @FXML
    private void onRemoveWatchLaterClicked() {
        String user = username();
        String selected = watchLaterList.getSelectionModel().getSelectedItem();
        if (user != null && selected != null) {
            userDataService.removeFromWatchLater(user, selected);
            loadData();
        }
    }

    @FXML
    private void onClearHistoryClicked() {
        String user = username();
        if (user != null) {
            userDataService.clearHistory(user);
            loadData();
        }
    }
}