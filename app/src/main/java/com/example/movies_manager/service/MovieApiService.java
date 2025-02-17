package com.example.movies_manager.service;

import com.example.movies_manager.pojo.moviesList.MoviesList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MovieApiService {

    @GET("3/movie/now_playing")
    Call<MoviesList> getNowPlayingMovies(
            @Header("Authorization") String authToken,
            @Header("accept") String accept,
            @Query("language") String language,
            @Query("page") int page
    );
}
