package com.example.movies_manager.repositories;

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
    private MovieApiService movieApiService; // Votre service Retrofit pour l'API
    private Realm realm;

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
                                        false  // Par défaut, non favori
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
                // Utilise copyToRealmOrUpdate pour mettre à jour si l'objet existe déjà
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
        RealmResults<Movie> favorites = realm.where(Movie.class)
                .equalTo("isFavorite", true)
                .findAllAsync();
        favorites.addChangeListener(new RealmChangeListener<RealmResults<Movie>>() {
            @Override
            public void onChange(RealmResults<Movie> movies) {
                liveData.postValue(realm.copyFromRealm(movies));
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
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Récupérer l'instance gérée par Realm
                Movie managedMovie = realm.where(Movie.class)
                        .equalTo("id_title", movie.getId_title())
                        .findFirst();
                if (managedMovie != null) {
                    managedMovie.setFavorite(!managedMovie.isFavorite());
                }
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
