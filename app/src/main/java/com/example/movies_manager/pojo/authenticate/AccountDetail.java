package com.example.movies_manager.pojo.authenticate;

import com.google.gson.annotations.SerializedName;

public class AccountDetail {

    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String username;

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
}
