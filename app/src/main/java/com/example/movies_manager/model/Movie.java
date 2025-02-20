package com.example.movies_manager.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Movie extends RealmObject {

    //***********
    //Variables
    //***********

    @PrimaryKey
    private int id_title;
    private int popularity;
    private String title, overview, posterPath, releaseDate;
    private boolean isFavorite;


    //***********************
    //Constructors
    //***********************

    public Movie() {
    }

    public Movie(int id_title, String title, String overview, int popularity, String posterPath, String releaseDate, boolean isFavorite) {
        this.id_title = id_title;
        this.title = title;
        this.overview = overview;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
