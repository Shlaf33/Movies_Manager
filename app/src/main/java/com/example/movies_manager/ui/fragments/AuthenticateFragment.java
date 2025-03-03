package com.example.movies_manager.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.movies_manager.databinding.WebAuthenticateFragmentBinding;
import com.example.movies_manager.pojo.authenticate.SessionUserResponse;
import com.example.movies_manager.viewModel.AuthUserViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticateFragment extends DialogFragment {

    //***********
    //Variables
    //***********

    private WebAuthenticateFragmentBinding binding;
    private AuthListener authListener;
    private String authUrl;

    private String approvedToken;

    private AuthUserViewModel authUserViewModel;


    //***********************************************************
    // Constructor of the fragment instance with the URL to load
    //***********************************************************
    public static AuthenticateFragment newInstance(String url, String token) {
        AuthenticateFragment fragment = new AuthenticateFragment();
        Bundle args = new Bundle();
        args.putString("AUTH_URL", url);
        args.putString("Token", token);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAuthListener(AuthListener listener) {
        this.authListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = WebAuthenticateFragmentBinding.inflate(inflater, container, false);
        authUserViewModel = new ViewModelProvider(requireActivity()).get(AuthUserViewModel.class);

        if (getArguments() != null) {
            authUrl = getArguments().getString("AUTH_URL");
            approvedToken = getArguments().getString("Token");

        }

        //*************************
        // WebView Configuration
        //*************************
        binding.webviewAuth.getSettings().setJavaScriptEnabled(true);


        //********************************************
        // Load the right URL with the token provided
        //********************************************
        binding.webviewAuth.loadUrl(authUrl + approvedToken);


        //*********************************************************************
        // Check when the URL change to /allow and display the continue button
        //*********************************************************************
        binding.webviewAuth.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("/allow")) {
                    binding.btContinue.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });


        //*****************************************************
        //Create a session when the continue button is clicked
        //*****************************************************
        binding.btContinue.setOnClickListener(v -> {
            createSession();
        });

        return binding.getRoot();
    }


    //********************************************************
    //Method that throw the session_id get from web request
    //********************************************************
    private void createSession() {

        authUserViewModel.createSession(approvedToken, new Callback<SessionUserResponse>() {
            @Override
            public void onResponse(Call<SessionUserResponse> call, Response<SessionUserResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String sessionId = response.body().getSessionId();
                    if (authListener != null) {
                        authListener.onAuthSuccess(sessionId);
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Erreur lors de la création de la session", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SessionUserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //*************************************************************************
    //Define the size of the dialog fragment so that it covers all the screen
    //*************************************************************************
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    //*******************************************************
    //The listener use to throw session_id to the activity
    //*******************************************************

    public interface AuthListener {
        void onAuthSuccess(String sessionId);
    }
}
