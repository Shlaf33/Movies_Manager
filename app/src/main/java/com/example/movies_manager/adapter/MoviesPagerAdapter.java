package com.example.movies_manager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.movies_manager.ui.fragments.FavorisFragment;
import com.example.movies_manager.ui.fragments.MovieFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class MoviesPagerAdapter extends FragmentStateAdapter {
    public MoviesPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return MovieFragment.newInstance();
            case 1:
                return FavorisFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }



}
