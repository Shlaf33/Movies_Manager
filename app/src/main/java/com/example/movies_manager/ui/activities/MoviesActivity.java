package com.example.movies_manager.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movies_manager.R;
import com.example.movies_manager.adapter.MoviesPagerAdapter;
import com.example.movies_manager.databinding.ActivityMoviesListBinding;
import com.example.movies_manager.model.User;
import com.example.movies_manager.viewModel.MovieViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MoviesActivity extends BaseActivity<ActivityMoviesListBinding> {

    //***********
    //Variables
    //***********
    private MoviesPagerAdapter moviesPagerAdapter;
    int userId;
    String userSessionId;
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
        userId = getIntent().getIntExtra("userId",-1);;
        userSessionId = getIntent().getStringExtra("userSessionId");

        binding.viewPager.setAdapter(moviesPagerAdapter);
        getPageTitle();

        //*************************************************************
        //Get the user from database by its id to be used in fragments
        //*************************************************************

        movieViewModel.getUserById(userId).observe(this, user -> {
            if(user != null){
                this.user = user;
            }
        });

        //******************************************************************************
        //End the current activity and delete user from database so it is disconnected
        //******************************************************************************

        binding.bottomNavigationView.findViewById(R.id.profil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieViewModel.deleteUserFromDatabase(userId).observe(MoviesActivity.this, value ->{
                    if(value){
                        finish();
                    }
                });

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

}