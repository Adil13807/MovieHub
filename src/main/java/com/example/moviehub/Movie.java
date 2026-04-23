package com.example.moviehub;

public class Movie {
    private String title;
    private String genre;
    private int year;
    private double rating;
    private String description;
    private String videoPath;

    public Movie(String title, String genre, int year, double rating, String description, String videoPath) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
        this.description = description;
        this.videoPath = videoPath;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    @Override
    public String toString() {
        return title + " (" + year + ")";
    }
}