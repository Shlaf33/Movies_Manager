package com.example.movies_manager.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.movies_manager.adapter.FavoriteAdapter;
import com.example.movies_manager.databinding.FragmentFavorisListBinding;
import com.example.movies_manager.viewModel.MovieViewModel;

public class FavorisFragment extends Fragment {

    private MovieViewModel movieViewModel;
    private FavoriteAdapter adapter;
    private FragmentFavorisListBinding binding;

    int accountId;
    String sessionId;

    String guestSessionId;

    public static FavorisFragment newInstance() {
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
        binding = FragmentFavorisListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //****************************************************
        //Adapter, viewmodel and recyclerview initialisation
        //****************************************************
        adapter = new FavoriteAdapter();
        binding.rvMoviesFav.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMoviesFav.setAdapter(adapter);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);

        //**************************************************************************
        //Get the account details before saving user favorite movies into database
        //**************************************************************************

        movieViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
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

        //******************************************************************
        //Get the favorite movies from database and display them in adapter
        //******************************************************************

        movieViewModel.getFavoriteMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                adapter.updateFavMovies(movies);
            }
        });
    }


}
