package com.example.movies_manager.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movies_manager.model.User;
import com.example.movies_manager.pojo.authenticate.SessionRequest;
import com.example.movies_manager.pojo.authenticate.SessionResponse;
import com.example.movies_manager.pojo.authenticate.Token;
import com.example.movies_manager.service.RetrofitService;
import com.example.movies_manager.service.TokenCallback;
import com.example.movies_manager.service.UserApiService;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthUserRepository {

    //***********
    //Variables
    //***********

    private static final String API_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2M2Q0ZTY2MjMzNzU4MzZjNjhkZmIxMmRmODNkNTg3ZSIsIm5iZiI6MTczOTUyMzY4Ni43NDg5OTk4LCJzdWIiOiI2N2FmMDY2NmExOGViZjJhYzU4ZTVmNzIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.mdHxMj7gwa3s3z-tAKyFNFIhEGC6Isob1Zyrg3Z7DX8";
    private static final String ACCEPT_HEADER = "application/json";

    private UserApiService userApiService;
    private Realm realm;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();


    //***************
    //Constructor
    //***************

    public AuthUserRepository(){
        userApiService = RetrofitService.getUserApiInstance();
        realm = Realm.getDefaultInstance();
        loadUserFromDatabase();
    }


    //**************************
    //Get TOKEN from themoviedb
    //**************************

    public void getTokenFromWeb(TokenCallback callback) {
        userApiService.getNewToken(API_TOKEN, ACCEPT_HEADER)
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getRequest_token();
                            callback.onTokenReceived(token);
                        } else {
                            callback.onError(new Exception("Response not successful or empty"));
                        }
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        callback.onError(t);
                    }
                });
    }


    //************************************
    //Create user session after approval
    //************************************

    public void createSession(String requestToken, Callback<SessionResponse> callback) {
        SessionRequest request = new SessionRequest(requestToken);
        userApiService.createSession(API_TOKEN, ACCEPT_HEADER, ACCEPT_HEADER,request).enqueue(callback);
    }

    //*************************************
    //Check if there is a user in database
    //*************************************

    public boolean isUserInDatabase(User user){
        User userLogged = realm.where(User.class)
                .equalTo("id", user.getId())
                .findFirst();
        return userLogged != null;
    }


    //***************************************
    //Return the existing user from database
    //***************************************

    private void loadUserFromDatabase() {
        realm.where(User.class).findAllAsync().addChangeListener(users -> {
            if (!users.isEmpty()) {
                userLiveData.postValue(users.first()); // Met à jour en arrière-plan
            } else {
                userLiveData.postValue(null);
            }
        });
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }


    //****************************************
    //Create user in database with session_id
    //****************************************

    public void createUserInDatabase(User user) {
        realm.executeTransactionAsync(r -> r.insertOrUpdate(user), () -> {
            loadUserFromDatabase(); // Met à jour LiveData après insertion
        });
    }



    //**********************
    //Close Realm instance
    //**********************

    public void closeRealm(){
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
