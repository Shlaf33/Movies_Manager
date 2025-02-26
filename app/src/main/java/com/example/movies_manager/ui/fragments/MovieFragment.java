package com.example.movies_manager.ui.fragments;

import android.os.Bundle;
import android.util.Log;
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

    int accountId;
    String sessionId;
    String guestSessionId;
    int offset = 0;
    int limit = 10;


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

        //***************************************************
        //Retrieving user or guest credentials from activity
        //***************************************************
        movieViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user ->{
            if(user!=null){
                accountId = user.getId();
                if(user.getSessionId()!=null){
                    Log.d("UserData", user.getSessionId());
                    sessionId = user.getSessionId();
                    movieViewModel.getUserFavoriteMovie(accountId, "fr-FR", 1, sessionId).observe(getViewLifecycleOwner(), resultList -> {
                        if (resultList != null && !resultList.isEmpty()) {
                            Log.d("ResultList", "not null" + resultList);
                            movieViewModel.turnUserFavMovieInDatabase(resultList);
                        } else {
                            Log.d("ResultList", "null" + sessionId + accountId);
                        }
                    });

                }
                else if(user.getGuestSessionId()!=null){
                    guestSessionId = user.getGuestSessionId();

                }

            }
        });


        //**************************************************************************************************************
        //Load movies in adapter if there are any in database otherwise call web request to fetch database with movies
        //**************************************************************************************************************

        if(movieViewModel.isThereMoviesLeft()){
            observeMovieData(offset, limit);
        }
        else{
            loadMoviesFromWeb();
        }

        //*********************************************************************
        //Make sure the movies are saved in database before updating adapter
        //*********************************************************************

        movieViewModel.getDataLoaded().observe(getViewLifecycleOwner(), loaded -> {
            Log.v("Loaded data", String.valueOf(loaded));
            if (loaded) {
                observeMovieData(offset, limit);

            }
            else{
                loadMoviesFromWeb();
            }
        });



        //**************************************************
        //Load the next ten movies from database in adapter
        //**************************************************

        fabAddMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset += 10;
                limit = 10;
                observeMovieData(offset, limit);
            }
        });

    }



    //******************************************************************************************************************************************
    // Check either a movie is favorite or not, display a Toast and updating fav icon before changing its value in database and in user account
    //******************************************************************************************************************************************

    @Override
    public void onAddFavClick(Movie movie) {

        if (!movieViewModel.isMovieFavorite(movie)) {
            Toast.makeText(getContext(), movie.getTitle() + " ajouté au favoris", Toast.LENGTH_SHORT).show();
            adapter.changeFavoriteIcon(movie.getId_title(), true);
            movieViewModel.toggleFavorite(movie);
            movieViewModel.postFavoriteMovie(movie, accountId, sessionId, true);
        } else {
            Toast.makeText(getContext(), movie.getTitle() + " supprimé des favoris", Toast.LENGTH_SHORT).show();
            adapter.changeFavoriteIcon(movie.getId_title(), false);
            movieViewModel.toggleFavorite(movie);
            movieViewModel.postFavoriteMovie(movie, accountId, sessionId, false);
        }
    }


    //*****************************************************************************
    //Delete a movie from database and reload adapter when no more movies in it
    //*****************************************************************************

    @Override
    public void onDeleteClick(Movie movie) {
        movieViewModel.deleteMovieFromDatabase(movie);
        adapter.removeMovie(movie);
        if (adapter.getItemCount() == 0 ) {
            Toast.makeText(getContext(), "Loading ten more movies", Toast.LENGTH_SHORT).show();
            observeMovieData(0, 10);
        }

    }


    //**********************************************************************************************************
    //Observe movies in database and display them in adapter or display a Toast when no more movies available
    //**********************************************************************************************************

    public void observeMovieData(int offset, int limit) {
        movieViewModel.getTenMoviesFromDatabase(offset,limit).observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                adapter.updateMovies(movies);
            } else {
                Toast.makeText(getContext(), "No more movies to load from database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadMoviesFromWeb(){
        movieViewModel.getPopularMovies(3);
        Toast.makeText(getContext(), "Loading movies from themoviedb.org", Toast.LENGTH_SHORT).show();
    }

}
