package com.example.movies_manager.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.movies_manager.R;
import com.example.movies_manager.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    //Variables

    private final List<Movie> moviesList;

    //Constructor
    public MoviesAdapter() {
        this.moviesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movies_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.ViewHolder holder, int position) {

        Movie movie = moviesList.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return 10;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        //IHM elements
        private final ImageView iv_movie_image;
        private final TextView tv_movie_title, tv_release_date, tv_popularity, tv_overview_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_movie_image = itemView.findViewById(R.id.iv_movie_image);
            tv_movie_title = itemView.findViewById(R.id.tv_movie_title);
            tv_release_date = itemView.findViewById(R.id.tv_release_date);
            tv_popularity = itemView.findViewById(R.id.tv_popularity);
            tv_overview_text = itemView.findViewById(R.id.tv_overview_text);
        }

        public void bind(Movie movie){

            if(movie.getPosterPath() == null){
                iv_movie_image.setImageResource(R.mipmap.ic_movie_image_vaiana);
            }
            else{
                Glide.with(itemView.getContext())
                        .load(movie.getPosterPath())
                        .apply(RequestOptions.centerCropTransform())
                        .into(iv_movie_image);

            }

            if(movie.getTitle() == null || movie.getTitle()==""){
                tv_movie_title.setText("Movie title unknown");
            }
            else {
                tv_movie_title.setText(movie.getTitle());
            }
            if(movie.getReleaseDate() == null || movie.getReleaseDate() == ""){
                tv_release_date.setText("Release Date unknown");
            }
            else {
                tv_release_date.setText(movie.getReleaseDate());
            }
            if(movie.getOverview() == null || movie.getOverview()==""){
                tv_overview_text.setText("Overview unknown");
            }
            else {
                tv_overview_text.setText(movie.getOverview());
            }
            if(movie.getPopularity() == 0){
                tv_popularity.setText("Popularity unknown");
            }
            else {
                tv_popularity.setText("" + movie.getPopularity());
            }

        }
    }

    public void updateMovies(List<Movie> movies){
        this.moviesList.clear();
        this.moviesList.addAll(movies);
        notifyDataSetChanged();
    }


}
