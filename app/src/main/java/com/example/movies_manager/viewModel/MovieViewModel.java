package com.example.movies_manager.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movies_manager.model.Movie;
import com.example.movies_manager.repositories.MovieRepository;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private final MovieRepository movieRepository;
    private LiveData<List<Movie>> nowPlayingMovies;
    private final LiveData<List<Movie>> favoriteMovies;

    public MovieViewModel() {
        movieRepository = new MovieRepository();
        // Récupération des films favoris dès l'initialisation du ViewModel
        favoriteMovies = movieRepository.getFavoriteMovies();
    }

    /**
     * Charge la liste des films en cours depuis l'API et la retourne sous forme de LiveData.
     *
     * @param language Langue souhaitée (ex. "fr-FR")
     * @param page     Numéro de page à récupérer
     * @return LiveData contenant la liste des films
     */
    public LiveData<List<Movie>> getNowPlayingMovies(String language, int page) {
        nowPlayingMovies = movieRepository.getNowPlayingMovies(language, page);
        return nowPlayingMovies;
    }

    /**
     * Retourne la liste des films favoris stockés en base locale.
     *
     * @return LiveData contenant la liste des films favoris
     */
    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    /**
     * Permet de basculer l'état favori d'un film.
     *
     * @param movie Le film dont l'état favori doit être inversé
     */
    public void toggleFavorite(Movie movie) {
        movieRepository.toggleFavorite(movie);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        movieRepository.close();
    }
}
