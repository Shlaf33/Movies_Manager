package com.example.movies_manager.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.lifecycle.ViewModelProvider;

import com.example.movies_manager.R;
import com.example.movies_manager.adapter.MoviesPagerAdapter;
import com.example.movies_manager.databinding.ActivityMoviesListBinding;
import com.example.movies_manager.model.User;
import com.example.movies_manager.viewModel.MovieViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoviesActivity extends BaseActivity<ActivityMoviesListBinding> {

    //***********
    //Variables
    //***********
    private MoviesPagerAdapter moviesPagerAdapter;
    int userId;
    String userSessionId;
    String guestSessionId;
    String expireAt;
    private MovieViewModel movieViewModel;
    User user;


    //***************************
    //ViewBinding initialisation
    //***************************
    @Override
    ActivityMoviesListBinding getViewBinding() {
        return ActivityMoviesListBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        moviesPagerAdapter = new MoviesPagerAdapter(this);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        //*********************
        //Get the intent data
        //*********************
        if (getIntent().getStringExtra("userSessionId") != null) {
            userSessionId = getIntent().getStringExtra("userSessionId");
        } else if (getIntent().getStringExtra("guestSessionId") != null) {
            guestSessionId = getIntent().getStringExtra("guestSessionId");
            expireAt = getIntent().getStringExtra("expiresAt");
        }
        userId = getIntent().getIntExtra("userId", -1);
        ;


        binding.viewPager.setAdapter(moviesPagerAdapter);
        getPageTitle();

        //*************************************************************
        //Get the user from database by its id to be used in fragments
        //*************************************************************

        movieViewModel.getUserById(userId).observe(this, user -> {
            if (user != null) {
                this.user = user;
            }
        });

        //******************************************************************************
        //End the current activity and delete user from database so it is disconnected
        //******************************************************************************

        binding.bottomNavigationView.findViewById(R.id.profil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectUser();

            }
        });

        //*************************************************************
        //Observe current date to disconnect guest if session expire
        //*************************************************************
        movieViewModel.getCurrentDate().observe(this, date -> {
            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH);
                try {
                    Date parsedDate = sdf.parse(expireAt);
                    if (date.after(parsedDate)) {
                        Toast.makeText(getApplicationContext(), "Guest session expired", Toast.LENGTH_SHORT).show();
                        disconnectUser();
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    //************************************
    // ViewPager configuration with tabs
    //************************************
    public void getPageTitle() {
        new TabLayoutMediator(binding.tabs, binding.viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Films");
                    } else if (position == 1) {
                        tab.setText("Favoris");
                    }
                }).attach();
    }

    public void disconnectUser() {
        movieViewModel.deleteUserFromDatabase(userId).observe(MoviesActivity.this, value -> {
            if (value) {
                movieViewModel.allMovieNotFav();
                finish();

            }
        });
    }

}