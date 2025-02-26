package com.example.movies_manager.repositories;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movies_manager.model.Movie;
import com.example.movies_manager.model.User;
import com.example.movies_manager.pojo.moviesList.FavoriteRequest;
import com.example.movies_manager.pojo.moviesList.MoviesList;
import com.example.movies_manager.pojo.moviesList.Result;
import com.example.movies_manager.service.MovieApiService;
import com.example.movies_manager.service.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    //*****************
    //Variables
    //*****************
    private static final String API_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2M2Q0ZTY2MjMzNzU4MzZjNjhkZmIxMmRmODNkNTg3ZSIsIm5iZiI6MTczOTUyMzY4Ni43NDg5OTk4LCJzdWIiOiI2N2FmMDY2NmExOGViZjJhYzU4ZTVmNzIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.mdHxMj7gwa3s3z-tAKyFNFIhEGC6Isob1Zyrg3Z7DX8";
    private static final String ACCEPT_HEADER = "application/json";

    private MovieApiService movieApiService;
    private Realm realm;
    private RealmResults<Movie> favorites;
    private RealmResults<Movie> movies;


    //*****************
    //Constructor
    //*****************
    public MovieRepository() {
        movieApiService = RetrofitService.getMovieApiInstance();
        realm = Realm.getDefaultInstance();
    }



    //******************************************
    // Get popular movies from themoviedb.org
    //******************************************
    public void getPopularMovies(String language, int page, Runnable onSuccess) {

        movieApiService.getPopularMovies(API_TOKEN, ACCEPT_HEADER, language, page)
                .enqueue(new Callback<MoviesList>() {
                    @Override
                    public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Result> results = response.body().getResults();
                            List<Movie> movies = new ArrayList<>();
                            for (Result r : results) {
                                // Conversion de Result en Movie
                                Movie movie = new Movie(
                                        r.getId(),
                                        r.getTitle(),
                                        r.getOverview(),
                                        r.getPopularity() != null ? r.getPopularity().intValue() : 0,
                                        "https://image.tmdb.org/t/p/w500" + r.getPoster_path(),
                                        r.getRelease_date(),
                                        false
                                );
                                movies.add(movie);
                            }
                            saveMoviesInRealm(movies, onSuccess);
                        }
                        Log.d("Appel API", "ok");
                    }

                    @Override
                    public void onFailure(Call<MoviesList> call, Throwable t) {
                        Log.e("Appel API", "echec");
                    }
                });

    }


    //********************************************
    //Saving movies from web request in database
    //********************************************
    private void saveMoviesInRealm(List<Movie> movies, Runnable onSuccess) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Movie m : movies) {
                    Movie existing = realm.where(Movie.class)
                            .equalTo("id_title", m.getId_title())
                            .findFirst();
                    if (existing != null && existing.isFavorite()) {
                        m.setFavorite(true);
                    }
                }
                realm.insertOrUpdate(movies);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (onSuccess != null) {
                    onSuccess.run();
                    Log.d("GetPopularCall", "ok base");
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("GetPopularCall", "Erreur lors de l'insertion", error);
            }
        });
    }


    //*********************************
    //Return all movies from database
    //*********************************
    public LiveData<List<Movie>> getMoviesFromDatabase() {
        MutableLiveData<List<Movie>> moviesList = new MutableLiveData<>();
        movies = realm.where(Movie.class)
                .findAllAsync();
        movies.addChangeListener(new RealmChangeListener<RealmResults<Movie>>() {
            @Override
            public void onChange(RealmResults<Movie> movies) {
                moviesList.postValue(realm.copyFromRealm(movies));
            }
        });
        return moviesList;
    }


    //************************************************************************************
    //Return either a movie is favorite or not for updating the UI and displaying a Toast
    //************************************************************************************
    public Boolean isMovieFavorite(Movie movie){
        Movie manageMovie = realm.where(Movie.class)
                .equalTo("id_title", movie.getId_title())
                .findFirst();
        return manageMovie != null && manageMovie.isFavorite();
    }




    //*******************************************
    //Return the first 10 movies from database
    //*******************************************
    public LiveData<List<Movie>> getTenMoviesFromDatabase(final int offset, final int limit) {
        MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Movie> results = realm.where(Movie.class)
                        .findAll(); // Requête synchrone dans la transaction

                List<Movie> movies = new ArrayList<>();
                if(results.size() > offset){
                    int end = Math.min(results.size() , offset+limit);
                    movies = realm.copyFromRealm(results.subList(offset, end));
                }
                moviesLiveData.postValue(movies);
            }
        });
        return moviesLiveData;
    }


    //******************************
    //Delete a movie from database
    //******************************
    public void deleteMovie(Movie movie){

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Movie managedMovie = realm.where(Movie.class)
                        .equalTo("id_title", movie.getId_title())
                        .findFirst();
                if(managedMovie!=null){
                    managedMovie.deleteFromRealm();
                }
            }
        });
    }


    //********************************************
    //Return all favorite movies from database
    //********************************************
    public LiveData<List<Movie>> getFavoriteMovies() {
        MutableLiveData<List<Movie>> favList = new MutableLiveData<>();
        favorites = realm.where(Movie.class)
                .equalTo("isFavorite", true)
                .findAllAsync();
        favorites.addChangeListener(new RealmChangeListener<RealmResults<Movie>>() {
            @Override
            public void onChange(RealmResults<Movie> movies) {
                Log.d("MovieRepository", "Favorites changed, count: " + movies.size());
                Log.d("RealmCheck", "Is realm closed? " + realm.isClosed());
                favList.postValue(realm.copyFromRealm(movies));

            }
        });
        return favList;
    }


    //************************************
    //Turn a movie into a favorite one
    //************************************
    public void toggleFavorite(final Movie movie) {
        Log.d("RealmCheck", "Is realm closed while addFav? " + realm.isClosed());
        Log.d("MovieRepository", "Movie is favorite : " + movie.isFavorite());
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Movie managedMovie = realm.where(Movie.class)
                        .equalTo("id_title", movie.getId_title())
                        .findFirst();
                if (managedMovie != null) {
                    boolean newValue = !managedMovie.isFavorite();
                    managedMovie.setFavorite(newValue);
                    Log.d("MovieRepository", "Inside transaction: Managed movie favorite set to " + newValue);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("MovieRepository", "Toggle transaction ok");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("MovieRepository", "Toggle transaction error", error);
            }
        });
    }


    //********************************************
    //Check if there are movies left in database
    //********************************************
    public Boolean isThereMoviesLeft(){
        long count = realm.where(Movie.class)
                .count();
        return count>0;
    }


    //**************************************
    //Return the user that has the same ID
    //**************************************
    public LiveData<User> getUserById(int userId){
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        realm.executeTransactionAsync(realm -> {
            User user = realm.where(User.class)
                    .equalTo("id", userId)
                    .findFirst();
            User detachedUser = realm.copyFromRealm(user);
            userLiveData.postValue(detachedUser);

        });
        return userLiveData;
    }


    //***********************************************************
    //Get the user favorite movies list session from themoviedb
    //***********************************************************

    public LiveData<List<Result>> getUserFavoriteMovie(int accountId, String language, int page, String sessionId) {
        MutableLiveData<List<Result>> resultList = new MutableLiveData<>();
        movieApiService.getUserFavoriteMovies(API_TOKEN, ACCEPT_HEADER, accountId, language, page, sessionId).enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if(response.isSuccessful() && response.body()!=null){
                    Log.d("GetUserFav", "Transaction ok" + response.body());
                    Log.d("GetUserFav", "Transaction ok" + response.body().getTotal_results());
                    resultList.postValue(response.body().getResults());
                    Log.v("GetUserFav", "empty response : " + response.code() + " / " + response.message());
                }
                else{
                    Log.v("GetUserFav", "empty response");
                }
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.e("GetUserFav", "Transaction error");
            }
        });
        return resultList;
    }


    //****************************************************************************************************
    //Turn all movies in database to not favorite and get user favorite movie and change them in database
    //****************************************************************************************************
    public void turnUserFavMovieInDatabase(List<Result> resultList){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<Movie> moviesList = realm.where(Movie.class).findAll();
                for(Movie movie : moviesList){
                    movie.setFavorite(false);
                }
                for(Result result : resultList){
                    Log.d("TurnUserFav", "Recherche du film avec ID: " + result.getId());
                    Movie managedFavMovie = realm.where(Movie.class)
                            .equalTo("id_title", result.getId())
                            .findFirst();
                    if (managedFavMovie != null) {
                        managedFavMovie.setFavorite(true);
                        Log.d("TurnUserFav", "Inside database: Managed movie favorite set to true");
                    }
                    else {
                        Log.d("TurnUserFav", "ManageMovie null");
                    }
                }
                realm.insertOrUpdate(moviesList);
            }

        });
    }

    //***********************************************************
    //Turn all movies into no favorite for guest user connexion
    //***********************************************************
    public void allMovieNotFav(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<Movie> moviesList = realm.where(Movie.class)
                        .findAll();
                for(Movie movie: moviesList){
                    movie.setFavorite(false);
                }
                realm.insertOrUpdate(moviesList);
            }
        });
    }


    //********************************************
    //Post a favorite movie onto the user account
    //********************************************

    public void postFavoriteMovie(Movie movie, int accountId, String sessionId, boolean isFav){
        FavoriteRequest favRequest = new FavoriteRequest("movie", movie.getId_title(), isFav);
        movieApiService.addFavoriteMovie(API_TOKEN, ACCEPT_HEADER, ACCEPT_HEADER, accountId, sessionId, favRequest).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(response.isSuccessful()){
                    Log.d("FavoriteResponse", "Films ajouté "+response.body());
                }
                else{
                    Log.d("FavoriteResponse", "Films pas ajouté "+response.code()+ " - " + response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                Log.d("FavoriteResponse", "Echec de l'appel");
            }
        });
    }

    //***************************
    //Delete user from database
    //***************************

    public void deleteUserFromDatabase(int userId, Runnable onSuccess){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User managedUser = realm.where(User.class)
                        .equalTo("id", userId)
                        .findFirst();
                if(managedUser!=null){
                    managedUser.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (onSuccess != null) {
                    onSuccess.run();
                    Log.d("UserDeleted", "ok");
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("UserDeleted", "delete error", error);
            }
        });
    }




    //**********************
    //Close realm instance
    //**********************
    public void close() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

}
