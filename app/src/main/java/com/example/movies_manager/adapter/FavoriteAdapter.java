package com.example.movies_manager.adapter;

import android.graphics.Typeface;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.movies_manager.R;
import com.example.movies_manager.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    //Variable
    private final List<Movie> favMoviesList;

    public FavoriteAdapter() {
        this.favMoviesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_favoris, parent, false);
        return new FavoriteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Movie movie = favMoviesList.get(position);
        holder.bind(movie);

    }

    @Override
    public int getItemCount() {
        return favMoviesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //IHM elements
        private final ImageView iv_movie_image_fav;
        private final TextView tv_movie_title_fav, tv_release_date_fav, tv_popularity_fav, tv_overview_text_fav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_movie_image_fav = itemView.findViewById(R.id.iv_movie_image_fav);
            tv_movie_title_fav = itemView.findViewById(R.id.tv_movie_title_fav);
            tv_release_date_fav = itemView.findViewById(R.id.tv_release_date_fav);
            tv_popularity_fav = itemView.findViewById(R.id.tv_popularity_fav);
            tv_overview_text_fav = itemView.findViewById(R.id.tv_overview_text_fav);
        }

        public void bind(Movie movie) {

            if (movie.getPosterPath() == null || movie.getPosterPath().isEmpty()) {
                iv_movie_image_fav.setImageResource(R.mipmap.ic_movie_image_vaiana);
            } else {
                Glide.with(itemView.getContext())
                        .load(movie.getPosterPath())
                        .override(Target.SIZE_ORIGINAL)
                        .fitCenter()
                        .into(iv_movie_image_fav);

            }

            if (movie.getTitle() == null || Objects.equals(movie.getTitle(), "")) {
                tv_movie_title_fav.setText("Movie title unknown");
            } else {
                tv_movie_title_fav.setText(movie.getTitle());
            }
            if (movie.getReleaseDate() == null || Objects.equals(movie.getReleaseDate(), "")) {
                tv_release_date_fav.setText("Release Date unknown");
            } else {
                tv_release_date_fav.setText(movie.getReleaseDate());
            }
            if (movie.getOverview() == null || Objects.equals(movie.getOverview(), "")) {
                tv_overview_text_fav.setText("Overview unknown");
                tv_overview_text_fav.setTypeface(null, Typeface.BOLD_ITALIC);
            } else {
                tv_overview_text_fav.setText(movie.getOverview());
                tv_overview_text_fav.setMovementMethod(new ScrollingMovementMethod());
            }
            if (movie.getPopularity() == 0) {
                tv_popularity_fav.setText("Popularity unknown");
            } else {
                tv_popularity_fav.setText("Popularity : " + movie.getPopularity());
            }

        }
    }

    public void updateFavMovies(List<Movie> movies) {
        this.favMoviesList.clear();
        this.favMoviesList.addAll(movies);
        notifyDataSetChanged();
    }
}
