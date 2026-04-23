package com.example.moviehub;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PlayerController {

    @FXML private MediaView mediaView;
    @FXML private Label statusLabel;

    private MediaPlayer mediaPlayer;

    public void playMovie(Movie movie) {
        try {
            String path = movie.getVideoPath();
            String resourcePath = path.startsWith("/") ? path : "/" + path;

            System.out.println("Trying to load video: " + resourcePath);

            var url = getClass().getResource(resourcePath);
            if (url == null) {
                statusLabel.setText("Видео не найдено: " + path);
                System.out.println("URL is null for " + resourcePath);
                return;
            }

            System.out.println("Video URL: " + url);

            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.setOnReady(() -> {
                statusLabel.setText("Сейчас идет: " + movie.getTitle());
                System.out.println("Media ready");
            });

            mediaPlayer.setOnError(() -> {
                statusLabel.setText("Ошибка воспроизведения: " + mediaPlayer.getError());
                System.out.println("Media error: " + mediaPlayer.getError());
            });

            mediaPlayer.play();
        } catch (Exception e) {
            statusLabel.setText("Не удалось открыть видео");
            e.printStackTrace();
        }
    }

    @FXML
    private void onPlayClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }



    @FXML
    private void onWatchClicked() {
        statusLabel.setText("Кнопка Смотреть нажата");
    }

    @FXML
    private void onPauseClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    private void onStopClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}