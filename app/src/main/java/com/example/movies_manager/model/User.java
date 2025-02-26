package com.example.movies_manager.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private int id;
    private String session_id, guest_session_id, username, expire_at;

    //********************
    //Constructor
    //********************

    public User(){

    }

    public User(int id, String session_id, String guest_session_id, String username, String expire_at) {
        this.id = id;
        this.session_id = session_id;
        this.guest_session_id = guest_session_id;
        this.username = username;
        this.expire_at = expire_at;
    }

    //********************
    //Getter and Setter
    //********************


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionId() {
        return session_id;
    }

    public void setSessionId(String session_id) {
        this.session_id = session_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGuestSessionId() {
        return guest_session_id;
    }

    public void setGuestSessionId(String guest_session_id) {
        this.guest_session_id = guest_session_id;
    }

    public String getExpireAt() {
        return expire_at;
    }

    public void setExpireAt(String expire_at) {
        this.expire_at = expire_at;
    }

}
