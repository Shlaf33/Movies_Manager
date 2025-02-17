package com.example.movies_manager.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies_manager.R;
import com.example.movies_manager.adapter.MoviesAdapter;
import com.example.movies_manager.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Movie> mMovies;
    private MoviesAdapter adapter;

    public static MovieFragment newInstance(){
        MovieFragment movieFragment = new MovieFragment();
        return movieFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_list, container, false);
        Context context = view.getContext();

        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    private void initList(){
        mMovies = new ArrayList<>();
        adapter = new MoviesAdapter();
        mRecyclerView.setAdapter(adapter);
    }
}
