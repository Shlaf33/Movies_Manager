package com.example.movies_manager;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movies_manager.adapter.MoviesAdapter;
import com.example.movies_manager.adapter.MoviesPagerAdapter;
import com.example.movies_manager.viewModel.MovieViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private MoviesPagerAdapter moviesPagerAdapter;

    private MovieViewModel movieViewModel;
    private FloatingActionButton fab_movies;
    private MoviesAdapter adapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movies_list);

        // Initialisation de la RecyclerView et de son adapter
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);
        fab_movies = findViewById(R.id.fab_movies);

        moviesPagerAdapter = new MoviesPagerAdapter(this);
        viewPager.setAdapter(moviesPagerAdapter);
        getPageTitle();


        fab_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Observer les films en cours ("Now Playing")
            }
        });
    }

    // Configuration des onglets avec le ViewPager
    public void getPageTitle(){
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Films");
                    } else if (position == 1) {
                        tab.setText("Favoris");
                    }
                }).attach();
    }


}