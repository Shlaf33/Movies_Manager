package com.example.movies_manager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.movies_manager.R;
import com.example.movies_manager.databinding.ActivityLoginBinding;
import com.example.movies_manager.model.User;
import com.example.movies_manager.pojo.authenticate.AccountDetail;
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
                binding.tvSelectUser.setText(user.getUsername());
                binding.btLogin.setVisibility(View.VISIBLE);
                binding.btCreateUser.setVisibility(View.INVISIBLE);
                binding.tvLoginGuest.setVisibility(View.INVISIBLE);
                this.user = user;

            }
        });


        binding.btCreateUser.setOnClickListener(v -> {

            authUserViewModel.fetchToken(new TokenCallback() {
                @Override
                public void onTokenReceived(String token) {
                    String authUrl = "https://www.themoviedb.org/authenticate/";
                    // Affichez votre DialogFragment avec l'URL d'authentification
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
                                        Toast.makeText(getApplicationContext(), "Erreur lors de la récupération des details du compte utilisateur", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<AccountDetail> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    dialogFragment.show(getSupportFragmentManager(), "authDialog");
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(LoginActivity.this, "Erreur lors de la récupération du token", Toast.LENGTH_SHORT).show();
                }
            });

        });

        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMovieActivity();
            }
        });

    }

    public void startMovieActivity() {
        Intent intent = new Intent(this, MoviesActivity.class);
        intent.putExtra("userId", user.getId());
        intent.putExtra("userSessionId", user.getSessionId());
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
