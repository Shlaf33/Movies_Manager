package com.example.movies_manager.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movies_manager.model.Movie;
import com.example.movies_manager.repositories.MovieRepository;

import java.util.List;

public class MovieViewModel extends ViewModel {

    //*****************
    //Variables
    //*****************

    private final MovieRepository movieRepository;

    private LiveData<List<Movie>> moviesFromDatabase;
    private final LiveData<List<Movie>> favoriteMovies;

    private final MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> dataLoaded = new MutableLiveData<>();




    //*****************
    //Constructor
    //*****************

    public MovieViewModel() {
        movieRepository = new MovieRepository();
        favoriteMovies = movieRepository.getFavoriteMovies();
        dataLoaded.observeForever(loaded -> {
            if (loaded != null && loaded) {
                movieRepository.getTenMoviesFromDatabase().observeForever(movies -> {
                    moviesLiveData.postValue(movies);
                });
            }
        });
    }




    //*************************************************
    // Méthode pour récupérer les films depuis l'API
    //*************************************************

    public void getPopularMovies() {
        movieRepository.getPopularMovies("fr-FR",1, () -> {
            dataLoaded.postValue(true);
        });

    }

    public LiveData<Boolean> getDataLoaded(){
        return dataLoaded;
    }

    //********************************************
    //Return the first 10 movies from database
    //********************************************

    public LiveData<List<Movie>> getTenMoviesFromDatabase(){
        moviesFromDatabase = movieRepository.getTenMoviesFromDatabase();
        return moviesFromDatabase;
    }


    //******************************************************************************
    //Return either a movie is favorite or not for updating UI ans display a Toast
    //******************************************************************************

    public boolean isMovieFavorite(Movie movie){
        return movieRepository.isMovieFavorite(movie);
    }



    //********************************
    //Delete a movie from database
    //********************************

    public void deleteMovieFromDatabase(Movie movie){
        movieRepository.deleteMovie(movie);
    }


    //*******************************************
    //Return all favorite movies from database
    //*******************************************

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }



    //**********************************
    //Turn a movie into a favorite one
    //**********************************

    public void toggleFavorite(Movie movie) {
        movieRepository.toggleFavorite(movie);
    }


    //*********************************************
    //Check if there are movies left in database
    //*********************************************
    public LiveData<Boolean> hasMoreMovies(){
        return movieRepository.hasMoreMovies();
    }


    //**********************
    //Close realm instance
    //**********************

    @Override
    protected void onCleared() {
        super.onCleared();
        movieRepository.close();
    }
}
