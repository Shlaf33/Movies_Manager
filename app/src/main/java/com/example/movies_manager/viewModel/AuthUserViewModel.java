package com.example.movies_manager.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movies_manager.model.User;
import com.example.movies_manager.pojo.authenticate.SessionResponse;
import com.example.movies_manager.repositories.AuthUserRepository;
import com.example.movies_manager.service.TokenCallback;

import retrofit2.Callback;

public class AuthUserViewModel extends ViewModel {

    //**********
    //Variables
    //**********

    AuthUserRepository authUserRepository;


    //************
    //Constructor
    //************

    public AuthUserViewModel(){
        authUserRepository = new AuthUserRepository();

    }

    //**************************
    //Create a user in database
    //**************************

    public void createUser(User user){
        authUserRepository.createUserInDatabase(user);
    }



    //****************************************
    //Get the user Token from themoviedb.org
    //****************************************

    public void fetchToken(TokenCallback callback) {
        authUserRepository.getTokenFromWeb(callback);
    }


    //************************************
    //Create user session after approval
    //************************************

    public void createSession(String requestToken, Callback<SessionResponse> callback){
        authUserRepository.createSession(requestToken,callback);
    }


    //*************************************
    //Check if there is a user in database
    //*************************************


    public LiveData<User> getUser() {
        return authUserRepository.getUser();
    }



}
