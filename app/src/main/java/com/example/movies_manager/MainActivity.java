package com.example.movies_manager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movies_manager.adapter.MoviesPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private MoviesPagerAdapter moviesPagerAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movies_list);

        // Initialisation de la RecyclerView et de son adapter
        viewPager = findViewById(R.id.viewPager); // Assurez-vous d'avoir cet ID dans votre layout
        tabLayout = findViewById(R.id.tabs); // Même remarque pour l'ID du TabLayout

        moviesPagerAdapter = new MoviesPagerAdapter(this);
        viewPager.setAdapter(moviesPagerAdapter);

        // Configuration des onglets avec le ViewPager
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