package com.example.movies_manager.model;

import java.util.Date;

import io.realm.RealmObject;

public class User extends RealmObject {

    private int id, session_id, guest_session_id;
    private String username;
    private Date expire_at;

    //********************
    //Constructor
    //********************

    public User(int id, int session_id, int guest_session_id, String username, Date expire_at) {
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

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGuest_session_id() {
        return guest_session_id;
    }

    public void setGuest_session_id(int guest_session_id) {
        this.guest_session_id = guest_session_id;
    }

    public Date getExpire_at() {
        return expire_at;
    }

    public void setExpire_at(Date expire_at) {
        this.expire_at = expire_at;
    }
}
