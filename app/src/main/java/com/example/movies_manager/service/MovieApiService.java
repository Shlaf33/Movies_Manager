package com.example.movies_manager.service;

import com.example.movies_manager.pojo.moviesList.FavoriteRequest;
import com.example.movies_manager.pojo.moviesList.MoviesList;
import com.example.movies_manager.pojo.moviesList.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {

    @GET("3/movie/popular")
    Call<MoviesList> getPopularMovies(
            @Header("Authorization") String authToken,
            @Header("accept") String accept,
            @Query("language") String language,
            @Query("page") int page
    );

    @POST("3/account/{account_id}/favorite")
    Call<Result> addFavoriteMovie(
            @Header("Authorization") String authToken,
            @Header("Content-Type") String contentType,
            @Header("accept") String accept,
            @Path("account_id") int accountId,
            @Query("session_id") String sessionID,
            @Body FavoriteRequest favoriteRequest
    );

    @GET("3/account/{account_id}/favorite/movies")
    Call<MoviesList> getUserFavoriteMovies(
            @Header("Authorization") String authToken,
            @Header("accept") String accept,
            @Path("account_id") int accountId,
            @Query("language") String language,
            @Query("page") int page,
            @Query("session_id") String sessionId

    );


    @GET("3/guest_session/{guest_session_id}/rated/movies")
    Call<MoviesList> getGuestRatedMovies(
            @Path("guest_session_id") String guestSessionID,
            @Header("Authorization") String authToken,
            @Header("accept") String accept,
            @Query("language") String language,
            @Query("page") int page
    );
}
