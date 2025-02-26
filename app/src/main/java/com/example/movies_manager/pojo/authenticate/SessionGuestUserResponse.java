package com.example.movies_manager.pojo.authenticate;

import com.google.gson.annotations.SerializedName;

public class SessionGuestUserResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("guest_session_id")
    private String guest_session_id;

    @SerializedName("expires_at")
    private String expires_at;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getGuest_session_id() {
        return guest_session_id;
    }

    public void setGuest_session_id(String guest_session_id) {
        this.guest_session_id = guest_session_id;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }
}
