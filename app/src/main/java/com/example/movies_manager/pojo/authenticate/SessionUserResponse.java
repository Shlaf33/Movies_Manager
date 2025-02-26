package com.example.movies_manager.pojo.authenticate;


import com.google.gson.annotations.SerializedName;

public class SessionUserResponse {

    //***************
    //Pojo variables
    //***************

    @SerializedName("success")
    private boolean success;
    @SerializedName("session_id")
    private String sessionId;


    //**************
    // Pojo Getters
    //**************

    public boolean isSuccess() {
        return success;
    }
    public String getSessionId() {
        return sessionId;
    }
}
