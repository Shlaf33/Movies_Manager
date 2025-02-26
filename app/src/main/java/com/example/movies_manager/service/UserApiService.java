package com.example.movies_manager.service;

import com.example.movies_manager.pojo.authenticate.AccountDetail;
import com.example.movies_manager.pojo.authenticate.SessionGuestUserResponse;
import com.example.movies_manager.pojo.authenticate.SessionRequest;
import com.example.movies_manager.pojo.authenticate.SessionUserResponse;
import com.example.movies_manager.pojo.authenticate.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApiService {

    @GET("3/authentication/token/new")
    Call<Token> getNewToken(
            @Header("Authorization") String authToken,
            @Header("accept") String accept
    );

    @POST("3/authentication/session/new")
    Call<SessionUserResponse> createSession(
            @Header("Authorization") String authToken,
            @Header("content-type") String contentType,
            @Header("accept") String accept,
            @Body SessionRequest request
    );

    @GET("3/account")
    Call<AccountDetail> getAccountId(
            @Header("Authorization") String authToken,
            @Header("accept") String accept,
            @Query("session_id") String sessionId
    );

    @GET("3/authentication/guest_session/new")
    Call<SessionGuestUserResponse> createGuestSession(
            @Header("Authorization") String authToken,
            @Header("accept") String accept
    );


}
