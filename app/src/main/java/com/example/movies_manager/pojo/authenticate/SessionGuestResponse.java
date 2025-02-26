package com.example.movies_manager.pojo.authenticate;

import com.google.gson.annotations.SerializedName;

public class SessionGuestResponse {

    //***************
    //Pojo variables
    //***************

    @SerializedName("success")
    private boolean success;
    @SerializedName("guest_session_id")
    private String guestSessionId;

    @SerializedName("expires_at")
    private String expiresAt;


    //**************
    // Pojo Getters
    //**************

    public boolean isSuccess() {
        return success;
    }
    public String getGuestSessionId() {
        return guestSessionId;
    }

    public String getExpiresAt() {
        return expiresAt;
    }
}
