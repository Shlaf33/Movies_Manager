package com.example.movies_manager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.movies_manager.R;
import com.example.movies_manager.databinding.ActivityLoginBinding;
import com.example.movies_manager.model.User;
import com.example.movies_manager.pojo.authenticate.AccountDetail;
import com.example.movies_manager.pojo.authenticate.SessionGuestUserResponse;
import com.example.movies_manager.service.TokenCallback;
import com.example.movies_manager.ui.fragments.AuthenticateFragment;
import com.example.movies_manager.viewModel.AuthUserViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {


    //***********
    //Variables
    //***********
    private AuthUserViewModel authUserViewModel;

    User user;


    //****************************
    //View Binding initialisation
    //****************************
    @Override
    ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        authUserViewModel = new ViewModelProvider(this).get(AuthUserViewModel.class);


        //***************************************************
        //change the ui if there is a user connected or not
        //***************************************************
        authUserViewModel.getUser().observe(this, user -> {
            if (user == null) {
                binding.tvUser.setText(R.string.userNeeded);
                binding.tvSelectUser.setVisibility(View.INVISIBLE);
                binding.btLogin.setVisibility(View.INVISIBLE);
                binding.btCreateUser.setVisibility(View.VISIBLE);
                binding.tvLoginGuest.setVisibility(View.VISIBLE);


            } else {
                binding.tvUser.setText(R.string.User);
                binding.tvSelectUser.setVisibility(View.VISIBLE);
                if (user.getUsername() != null) {
                    binding.tvSelectUser.setText(user.getUsername());
                } else {
                    binding.tvSelectUser.setText(R.string.guest);
                }

                binding.btLogin.setVisibility(View.VISIBLE);
                binding.btCreateUser.setVisibility(View.INVISIBLE);
                binding.tvLoginGuest.setVisibility(View.INVISIBLE);
                this.user = user;

            }
        });


        //************************************************************************************
        //Launch the webview fragment when clicked on "Connexion" button and get credentials
        //************************************************************************************
        binding.btCreateUser.setOnClickListener(v -> {

            authUserViewModel.fetchToken(new TokenCallback() {
                @Override
                public void onTokenReceived(String token) {
                    String authUrl = "https://www.themoviedb.org/authenticate/";

                    AuthenticateFragment dialogFragment = AuthenticateFragment.newInstance(authUrl, token);
                    dialogFragment.setAuthListener(new AuthenticateFragment.AuthListener() {
                        @Override
                        public void onAuthSuccess(String sessionId) {
                            User newUser = new User();
                            newUser.setSessionId(sessionId);
                            authUserViewModel.getAccountId(sessionId, new Callback<AccountDetail>() {
                                @Override
                                public void onResponse(Call<AccountDetail> call, Response<AccountDetail> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        int id = response.body().getId();
                                        String userName = response.body().getUsername();
                                        newUser.setId(id);
                                        newUser.setUsername(userName);
                                        authUserViewModel.createUser(newUser);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error while getting user credentials", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<AccountDetail> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    dialogFragment.show(getSupportFragmentManager(), "authDialog");
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(LoginActivity.this, "Getting token error", Toast.LENGTH_SHORT).show();
                }
            });

        });


        //*************************************************
        //Start the movie activity when user is registered
        //*************************************************
        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMovieActivity();
            }
        });


        //********************************************************
        //Create guest session and start activity when registered
        //********************************************************
        binding.tvLoginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authUserViewModel.createGuestSession(new Callback<SessionGuestUserResponse>() {
                    @Override
                    public void onResponse(Call<SessionGuestUserResponse> call, Response<SessionGuestUserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User guestUser = new User();
                            String guestSessionId = response.body().getGuest_session_id();
                            String expireAt = response.body().getExpires_at();
                            guestUser.setGuestSessionId(guestSessionId);
                            guestUser.setExpireAt(expireAt);
                            Log.d("GuestExpire", expireAt);
                            authUserViewModel.createUser(guestUser);
                            user = guestUser;
                            Toast.makeText(getApplicationContext(), "Logged as guest", Toast.LENGTH_SHORT).show();
                            startMovieActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error while getting Guestuser credentials", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SessionGuestUserResponse> call, Throwable t) {
                        Log.e("GuestUser", "Network error");
                    }
                });
            }
        });

    }

    public void startMovieActivity() {
        Intent intent = new Intent(this, MoviesActivity.class);
        intent.putExtra("userId", user.getId());
        if (user.getSessionId() != null) {
            intent.putExtra("userSessionId", user.getSessionId());
        } else {
            intent.putExtra("guestSessionId", user.getGuestSessionId());
            intent.putExtra("expiresAt", user.getExpireAt());
        }

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //*******************************
        //Check if the user still exists
        //*******************************
        authUserViewModel.refreshUser();
    }


}
