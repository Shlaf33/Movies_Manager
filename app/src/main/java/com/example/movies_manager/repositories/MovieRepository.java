package com.example.movies_manager.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movies_manager.model.Movie;
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

    private static final String API_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2M2Q0ZTY2MjMzNzU4MzZjNjhkZmIxMmRmODNkNTg3ZSIsIm5iZiI6MTczOTUyMzY4Ni43NDg5OTk4LCJzdWIiOiI2N2FmMDY2NmExOGViZjJhYzU4ZTVmNzIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.mdHxMj7gwa3s3z-tAKyFNFIhEGC6Isob1Zyrg3Z7DX8";
    private static final String ACCEPT_HEADER = "application/json";
    private MovieApiService movieApiService;
    private Realm realm;

    private RealmResults<Movie> favorites;

    public MovieRepository() {
        movieApiService = RetrofitService.getInstance();
        realm = Realm.getDefaultInstance();
    }

    // Méthode pour récupérer les films depuis l'API
    public LiveData<List<Movie>> getNowPlayingMovies(String language, int page) {
        MutableLiveData<List<Movie>> liveData = new MutableLiveData<>();

        movieApiService.getNowPlayingMovies(API_TOKEN, ACCEPT_HEADER, language, page)
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
                                        "https://image.tmdb.org/t/p/w500"+r.getPoster_path(),
                                        r.getRelease_date(),
                                        false
                                );
                                movies.add(movie);
                            }
                            liveData.postValue(movies);
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
    private void saveMoviesInRealm(List<Movie> movies) {
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
        });
    }

    /**
     * Récupère la liste des films favoris depuis Realm.
     *
     * @return Un LiveData contenant la liste des films favoris.
     */
    public LiveData<List<Movie>> getFavoriteMovies() {
        MutableLiveData<List<Movie>> liveData = new MutableLiveData<>();
        Realm realmLocal = Realm.getDefaultInstance();
        favorites = realmLocal.where(Movie.class)
                .equalTo("isFavorite", true)
                .findAllAsync();
        favorites.addChangeListener(new RealmChangeListener<RealmResults<Movie>>() {
            @Override
            public void onChange(RealmResults<Movie> movies) {
                Log.d("MovieRepository", "Favorites changed, count: " + movies.size());
                Log.d("RealmCheck", "Is realm closed? " + realmLocal.isClosed());
                liveData.postValue(realmLocal.copyFromRealm(movies));

            }
        });
        return liveData;
    }

    /**
     * Bascule le statut favori d'un film.
     *
     * @param movie Le film dont le statut doit être modifié.
     */
    public void toggleFavorite(final Movie movie) {
        Log.d("RealmCheck", "Is realm closed while addFav? " + realm.isClosed());
        Log.d("MovieRepository", "Movie is favorite : " + movie.isFavorite());
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Récupérer l'instance gérée par Realm

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

    /**
     * Ferme l'instance Realm. À appeler lors du nettoyage (ex. onCleared() du ViewModel).
     */
    public void close() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

}
