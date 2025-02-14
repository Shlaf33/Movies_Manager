package com.example.movies_manager.model;

public class Movie {

    private int id_title;
    private String overview, popularity, posterPath;
    private boolean isFavorite;


    //***********************
    //Constructor
    //***********************

    public Movie(int id_title, String overview, String popularity, String posterPath, boolean isFavorite) {
        this.id_title = id_title;
        this.overview = overview;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.isFavorite = isFavorite;
    }



    //***********************
    //Getter and Setter
    //***********************

    public int getId_title() {
        return id_title;
    }

    public void setId_title(int id_title) {
        this.id_title = id_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
