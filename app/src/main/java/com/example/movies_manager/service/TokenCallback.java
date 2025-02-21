package com.example.movies_manager.service;

public interface TokenCallback {
    void onTokenReceived(String token);
    void onError(Throwable t);
}
