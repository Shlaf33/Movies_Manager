package com.example.movies_manager.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movies_manager.model.Movie;
import com.example.movies_manager.pojo.moviesList.MoviesList;
import com.example.movies_manager.pojo.moviesList.Result;
import com.example.movies_manager.service.MovieApiService;
import com.example.movies_manager.service.RetrofitService;

import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    private static final String API_TOKEN = "Bearer YOUR_API_TOKEN";
    private static final String ACCEPT_HEADER = "application/json";
    private MovieApiService movieApiService; // Votre service Retrofit pour l'API
    private Realm realm;

    public MovieRepository() {
        movieApiService = RetrofitService.getInstance();
        realm = Realm.getDefaultInstance();
    }

    // Méthode pour récupérer les films depuis l'API
    public LiveData<List<Result>> getNowPlayingMovies(String language, int page) {
        MutableLiveData<List<Result>> liveData = new MutableLiveData<>();

        movieApiService.getNowPlayingMovies(API_TOKEN, ACCEPT_HEADER, language, page)
                .enqueue(new Callback<MoviesList>() {
                    @Override
                    public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Result> movies = response.body().getResults();
                            liveData.postValue(movies);
                            // Mise en cache en base locale via Realm
                            saveMoviesInRealm(movies);
                        } else {
                            liveData.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviesList> call, Throwable t) {
                        liveData.postValue(null);
                    }
                });

        return liveData;
    }

    /**
     * Sauvegarde la liste des films dans Realm.
     *
     * @param movies La liste des films à sauvegarder.
     */
    private void saveMoviesInRealm(List<Result> movies) {
        realm.executeTransactionAsync(r -> r.insertOrUpdate((Collection<? extends RealmModel>) movies));
    }

    /**
     * Récupère la liste des films favoris depuis Realm.
     *
     * @return Un LiveData contenant la liste des films favoris.
     */
    public LiveData<List<Movie>> getFavoriteMovies() {
        MutableLiveData<List<Movie>> liveData = new MutableLiveData<>();
        RealmResults<Movie> favorites = realm.where(Result.class)
                .equalTo("isFavorite", true)
                .findAllAsync();
        favorites.addChangeListener(results ->
                liveData.postValue(realm.copyFromRealm(results))
        );
        return liveData;
    }

    /**
     * Bascule le statut favori d'un film.
     *
     * @param movie Le film dont le statut doit être modifié.
     */
    public void toggleFavorite(Movie movie) {
        realm.executeTransactionAsync(r -> {
            // Inverse l'état "isFavorite"
            movie.setFavorite(!movie.isFavorite());
            r.insertOrUpdate(movie);
        });
    }

    /**
     * Ferme l'instance Realm. À appeler lors du nettoyage (ex. onCleared() du ViewModel).
     */
    public void close() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    // Pensez à fermer votre instance Realm dans une méthode de nettoyage (e.g., onCleared dans le ViewModel)
}
