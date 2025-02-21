package com.example.movies_manager.service;

import com.example.movies_manager.pojo.authenticate.SessionRequest;
import com.example.movies_manager.pojo.authenticate.SessionResponse;
import com.example.movies_manager.pojo.authenticate.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApiService {

    @GET("3/authentication/token/new")
    Call<Token> getNewToken(
            @Header("Authorization") String authToken,
            @Header("accept") String accept
    );

    @POST("3/authentication/session/new")
    Call<SessionResponse> createSession(
            @Header("Authorization") String authToken,
            @Header("content-type") String contentType,
            @Header("accept") String accept,
            @Body SessionRequest request
    );
}
