package com.example.movies_manager.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies_manager.R;
import com.example.movies_manager.adapter.MoviesAdapter;
import com.example.movies_manager.model.Movie;
import com.example.movies_manager.viewModel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MovieViewModel movieViewModel;
    private MoviesAdapter adapter;

    public static MovieFragment newInstance(){
        MovieFragment movieFragment = new MovieFragment();
        return movieFragment;
    }

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
        adapter = new MoviesAdapter();
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Récupération du ViewModel partagé avec l'activité
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);

        // Observer les films en cours ("Now Playing")
        movieViewModel.getNowPlayingMovies("fr-FR", 1).observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                adapter.updateMovies(movies);
            }
        });
    }
}
