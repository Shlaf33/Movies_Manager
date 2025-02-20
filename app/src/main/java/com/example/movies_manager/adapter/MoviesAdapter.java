package com.example.movies_manager.adapter;



import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.movies_manager.R;
import com.example.movies_manager.model.Movie;
import com.example.movies_manager.ui.fragments.MovieFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    //**********
    //Variables
    //**********

    private final List<Movie> moviesList;
    private final OnMovieListener onMovieListener;
    private final OnDeleteMovieListener onDeleteMovieListener;

    View.OnClickListener onDeleteClickMovieListener;


    //*************
    //Constructor
    //*************

    public MoviesAdapter(OnMovieListener onMovieListener, OnDeleteMovieListener onDeleteMovieListener) {
        this.onMovieListener = onMovieListener;
        this.onDeleteMovieListener = onDeleteMovieListener;
        this.moviesList = new ArrayList<>();
    }


    //**************************
    //Adapter initialisation
    //**************************

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movies, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.ViewHolder holder, int position) {

        Movie movie = moviesList.get(position);
        holder.bind(movie);

        onDeleteClickMovieListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDeleteMovieListener!=null){
                    onDeleteMovieListener.onDeleteClick(movie);
                }
            }
        };

        holder.ib_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMovieListener!=null){
                    onMovieListener.onAddFavClick(movie);
                }

            }
        });
        holder.ib_share.setOnClickListener(onDeleteClickMovieListener);
        holder.ib_comment.setOnClickListener(onDeleteClickMovieListener);
        holder.ib_options.setOnClickListener(onDeleteClickMovieListener);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    //***************************
    //ViewHolder declaration
    //***************************

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //***************
        //IHM elements
        //***************
        private final ImageView iv_movie_image;
        private final ImageButton ib_favorite, ib_share, ib_comment, ib_options;
        private final TextView tv_movie_title, tv_release_date, tv_popularity, tv_overview_text;

        //***************************
        //ViewHolder initialisation
        //***************************
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_movie_image = itemView.findViewById(R.id.iv_movie_image);
            tv_movie_title = itemView.findViewById(R.id.tv_movie_title);
            tv_release_date = itemView.findViewById(R.id.tv_release_date);
            tv_popularity = itemView.findViewById(R.id.tv_popularity);
            tv_overview_text = itemView.findViewById(R.id.tv_overview_text);
            ib_favorite = itemView.findViewById(R.id.ib_favorite);
            ib_comment = itemView.findViewById(R.id.ib_comment);
            ib_share = itemView.findViewById(R.id.ib_share);
            ib_options = itemView.findViewById(R.id.ib_options);
        }

        public void bind(Movie movie) {

            if (movie.getPosterPath() == null || movie.getPosterPath().isEmpty()) {
                iv_movie_image.setImageResource(R.mipmap.ic_movie_image_vaiana);
            } else {
                Glide.with(itemView.getContext())
                        .load(movie.getPosterPath())
                        .override(Target.SIZE_ORIGINAL)
                        .fitCenter()
                        .into(iv_movie_image);

            }

            if(movie.isFavorite()){
                ib_favorite.setImageResource(R.drawable.baseline_favorite_24);
            }
            if(!movie.isFavorite()){
                ib_favorite.setImageResource(R.drawable.baseline_favorite_border_24);
            }
            if (movie.getTitle() == null || Objects.equals(movie.getTitle(), "")) {
                tv_movie_title.setText("Movie title unknown");
            } else {
                tv_movie_title.setText(movie.getTitle());
            }
            if (movie.getReleaseDate() == null || Objects.equals(movie.getReleaseDate(), "")) {
                tv_release_date.setText("Release Date unknown");
            } else {
                tv_release_date.setText(movie.getReleaseDate());
            }
            if (movie.getOverview() == null || Objects.equals(movie.getOverview(), "")) {
                tv_overview_text.setText("Overview unknown");
                tv_overview_text.setTypeface(null, Typeface.BOLD_ITALIC);
            } else {
                tv_overview_text.setText(movie.getOverview());
            }
            if (movie.getPopularity() == 0) {
                tv_popularity.setText("Popularity unknown");
            } else {
                tv_popularity.setText("Popularity : " + movie.getPopularity());
            }

        }
    }


    //***************************************************
    // Update adapter to display movies send by fragment
    //***************************************************

    public void updateMovies(List<Movie> movies) {
        this.moviesList.clear();
        this.moviesList.addAll(movies);
        notifyDataSetChanged();
    }


    //******************************************************************
    //Callbacks for adding favorite movie and deleting it from database
    //******************************************************************

    public interface OnMovieListener{
        void onAddFavClick(Movie movie);
    }

    public interface OnDeleteMovieListener{

        void onDeleteClick(Movie movie);
    }


    //******************************************************************
    //Update adapter when an item is removed by deleting it in database
    //******************************************************************
    public void removeMovie(Movie movie) {
        int position = moviesList.indexOf(movie);
        if(position != -1){
            moviesList.remove(position);
            notifyItemRemoved(position);
        }
    }

    //*********************************************************************************************
    //Get movie ID from fragment and modify its favorite status in adapter to change favorite icon
    //*********************************************************************************************
    public void changeFavoriteIcon(int movieId, boolean isFavorite){
        for(int i = 0; i < moviesList.size(); i++){
            Movie movie = moviesList.get(i);
            if(movie.getId_title() == movieId){
                movie.setFavorite(isFavorite);
                notifyItemChanged(i);
            }
        }
    }

}
