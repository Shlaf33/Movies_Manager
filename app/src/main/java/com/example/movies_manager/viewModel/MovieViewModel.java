package com.example.movies_manager.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movies_manager.model.Movie;
import com.example.movies_manager.model.User;
import com.example.movies_manager.pojo.moviesList.FavoriteRequest;
import com.example.movies_manager.pojo.moviesList.Result;
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
    private final MutableLiveData<Boolean> userDeleted = new MutableLiveData<>();

    private LiveData<User>  userLiveData;




    //*****************
    //Constructor
    //*****************

    public MovieViewModel() {
        movieRepository = new MovieRepository();
        favoriteMovies = movieRepository.getFavoriteMovies();
    }




    //*************************************************
    // Méthode pour récupérer les films depuis l'API
    //*************************************************

    public void getPopularMovies(int totalPages) {

        for(int page = 1; page<=totalPages; page++){
            int finalPage = page;
            movieRepository.getPopularMovies("fr-FR",finalPage, () -> {
                Log.v("Loaded Data", "true");
                if(finalPage ==totalPages){
                    dataLoaded.postValue(true);
                }
            });
        }


    }

    public LiveData<Boolean> getDataLoaded(){
        return dataLoaded;
    }

    //*********************************************
    //Check if there are movies left in database
    //*********************************************

    public Boolean isThereMoviesLeft(){
        return movieRepository.isThereMoviesLeft();
    }


    //********************************************
    //Return the first 10 movies from database
    //********************************************

    public LiveData<List<Movie>> getTenMoviesFromDatabase(final int offset, final int limit){
        moviesFromDatabase = movieRepository.getTenMoviesFromDatabase(offset, limit);
        return moviesFromDatabase;
    }

    public MutableLiveData<List<Movie>> getLiveData(){
        return moviesLiveData;
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


    //**************************************
    //Return the user that has the same ID
    //**************************************

    public LiveData<User> getUserById(int userId){
        userLiveData = movieRepository.getUserById(userId);
        return movieRepository.getUserById(userId);
    }

    public LiveData<User> getUserLiveData(){
        return userLiveData;
    }

    //********************************************
    //Post a favorite movie onto the user account
    //********************************************

    public void postFavoriteMovie(Movie movie, int accountId, String sessionId, boolean isFav){
        movieRepository.postFavoriteMovie(movie, accountId,sessionId, isFav);
    }

    //***********************************************************
    //Get the user favorite movies list session from themoviedb
    //***********************************************************

    public LiveData<List<Result>> getUserFavoriteMovie(int accountId, String language, int page, String sessionId) {
        return movieRepository.getUserFavoriteMovie(accountId, language, page,sessionId);
    }


    //***************************************
    //Turn user favorite movie into database
    //***************************************

    public void turnUserFavMovieInDatabase(List<Result> resultList){
        movieRepository.turnUserFavMovieInDatabase(resultList);
    }

    //***************************
    //Delete user from database
    //***************************
    public LiveData<Boolean> deleteUserFromDatabase(int userId){
        movieRepository.deleteUserFromDatabase(userId, () ->
               userDeleted.postValue(true) );
        return userDeleted;
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
