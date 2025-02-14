package com.example.movies_manager.service;


import android.util.Log;

import com.example.movies_manager.pojo.moviesList.MoviesList;

import java.util.ArrayList;

public class MoviesDBWebService {

    private static final String WS_URL = "https://api.themoviedb.org/3/movie/now_playing";

    public static ArrayList<MoviesList> getMoviesFromServer() throws Exception {
        //Effectuer la requête

        String responseJson = OkHttpUtils.sendGetOkHttpRequest(WS_URL);

        Log.v("TAG", responseJson);

        return new ArrayList<>();
    }
}
