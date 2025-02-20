package com.example.movies_manager.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies_manager.R;
import com.example.movies_manager.adapter.MoviesAdapter;
import com.example.movies_manager.model.Movie;
import com.example.movies_manager.viewModel.MovieViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MovieFragment extends Fragment implements MoviesAdapter.OnMovieListener, MoviesAdapter.OnDeleteMovieListener {


    //*******************
    //Variables
    //*******************

    private RecyclerView mRecyclerView;
    private MovieViewModel movieViewModel;
    private MoviesAdapter adapter;
    private FloatingActionButton fabAddMovies;


    //************************
    //Constructor
    //************************

    public static MovieFragment newInstance() {
        MovieFragment movieFragment = new MovieFragment();
        return movieFragment;
    }

    //*************************
    //Fragment initialisation
    //*************************

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_list, container, false);
        mRecyclerView = view.findViewById(R.id.rv_movies);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //************************************
        //ViewModel and button initialisation
        //*************************************

        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        fabAddMovies = view.findViewById(R.id.fab_movies);

        //********************************************************
        //Load movies in adapter if there are any in database
        //********************************************************

        observeMovieData();


        //******************************************************************************
        //Making sure movies have been saved in database before loading them in adapter
        //******************************************************************************

        fabAddMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieViewModel.getNowPlayingMovies();
                Toast.makeText(getContext(), "Loading movies from themoviedb.org", Toast.LENGTH_SHORT).show();
                movieViewModel.getDataLoaded().observe(getViewLifecycleOwner(), loaded -> {
                    if (loaded != null && loaded) {
                        observeMovieData();
                    }
                });

            }
        });

    }


    //***********************************************************************************************************************
    // Check either a movie is favorite or not, display a Toast and updating fav icon before changing its value in database
    //***********************************************************************************************************************

    @Override
    public void onAddFavClick(Movie movie) {

        if (!movieViewModel.isMovieFavorite(movie)) {
            Toast.makeText(getContext(), movie.getTitle() + " ajouté au favoris", Toast.LENGTH_SHORT).show();
            adapter.changeFavoriteIcon(movie.getId_title(), true);
            movieViewModel.toggleFavorite(movie);
        } else {
            Toast.makeText(getContext(), movie.getTitle() + " supprimer des favoris", Toast.LENGTH_SHORT).show();
            adapter.changeFavoriteIcon(movie.getId_title(), false);
            movieViewModel.toggleFavorite(movie);
        }
    }


    //*****************************************************************************
    //Delete a movie from database and reload adapter when no more movies in it
    //*****************************************************************************

    @Override
    public void onDeleteClick(Movie movie) {
        movieViewModel.deleteMovieFromDatabase(movie);
        adapter.removeMovie(movie);
        if (adapter.getItemCount() == 0) {
            observeMovieData();
        }

    }


    //**********************************************************************************************************
    //Observe movies in database and display them in adapter or displaying a Toast when no more movies available
    //**********************************************************************************************************

    public void observeMovieData() {
        movieViewModel.getTenMoviesFromDatabase().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                adapter.updateMovies(movies);
            } else {
                Toast.makeText(getContext(), "No more movies in database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
