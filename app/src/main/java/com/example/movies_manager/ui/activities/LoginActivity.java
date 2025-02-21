package com.example.movies_manager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.movies_manager.R;
import com.example.movies_manager.model.User;
import com.example.movies_manager.service.TokenCallback;
import com.example.movies_manager.ui.fragments.AuthenticateFragment;
import com.example.movies_manager.viewModel.AuthUserViewModel;

public class LoginActivity extends AppCompatActivity {

    private AuthUserViewModel authUserViewModel;
    Button bt_create_user, bt_login;
    TextView tv_user, tv_select_user, tv_create_user;
    EditText et_user_name;

    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        tv_user = findViewById(R.id.tv_user);
        tv_select_user = findViewById(R.id.tv_select_user);
        bt_login = findViewById(R.id.bt_login);
        tv_create_user = findViewById(R.id.tv_create_user);
        bt_create_user = findViewById(R.id.bt_create_user);
        et_user_name = findViewById(R.id.et_user_name);


        authUserViewModel = new ViewModelProvider(this).get(AuthUserViewModel.class);

        authUserViewModel.getUser().observe(this, user -> {
            if (user == null) {
                Toast.makeText(LoginActivity.this, "No user in database", Toast.LENGTH_SHORT).show();
                tv_user.setText(R.string.userNeeded);
                tv_select_user.setVisibility(View.INVISIBLE);
                bt_login.setVisibility(View.INVISIBLE);
                et_user_name.setVisibility(View.VISIBLE);
                bt_create_user.setVisibility(View.VISIBLE);


            } else {
                Toast.makeText(LoginActivity.this, "There is a user register in the database", Toast.LENGTH_SHORT).show();
                tv_user.setText(R.string.User);
                tv_create_user.setVisibility(View.INVISIBLE);
                tv_select_user.setVisibility(View.VISIBLE);
                tv_select_user.setText(user.getUsername());
                bt_login.setVisibility(View.VISIBLE);
                et_user_name.setVisibility(View.INVISIBLE);
                bt_create_user.setVisibility(View.INVISIBLE);
                this.user = user;

            }
        });


        bt_create_user.setOnClickListener(v -> {
            if (et_user_name.getText() == null || et_user_name.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter a User Name", Toast.LENGTH_SHORT).show();
            } else {

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
                                newUser.setSession_id(sessionId);
                                newUser.setUsername(et_user_name.getText().toString());
                                //Enregistre l'utilisateur dans Realm
                                authUserViewModel.createUser(newUser);
                            }
                        });
                        dialogFragment.show(getSupportFragmentManager(), "authDialog");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(LoginActivity.this, "Erreur lors de la récupération du token", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMovieActivity();
            }
        });

    }

    public void startMovieActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }
}
