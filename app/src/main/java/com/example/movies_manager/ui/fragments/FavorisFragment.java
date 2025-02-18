package com.example.movies_manager.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies_manager.R;
import com.example.movies_manager.adapter.FavoriteAdapter;
import com.example.movies_manager.adapter.MoviesAdapter;
import com.example.movies_manager.model.Movie;
import com.example.movies_manager.viewModel.MovieViewModel;

public class FavorisFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MovieViewModel movieViewModel;
    private FavoriteAdapter adapter;

    public static FavorisFragment newInstance(){
        FavorisFragment favorisFragment = new FavorisFragment();
        return favorisFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favoris_list, container, false);
        mRecyclerView = view.findViewById(R.id.rv_movies_fav);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoriteAdapter();
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        movieViewModel.getFavoriteMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                adapter.updateFavMovies(movies);
            }
        });
    }


}
