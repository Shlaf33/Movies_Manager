package com.example.movies_manager.pojo.authenticate;


import com.google.gson.annotations.SerializedName;

public class SessionRequest {

    //***************
    //Pojo variable
    //***************
    @SerializedName("request_token")
    private String requestToken;

    public SessionRequest(String requestToken) {
        this.requestToken = requestToken;
    }

    //*************************
    //Pojo Getters and Setters
    //*************************

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}
