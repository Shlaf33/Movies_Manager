package com.example.movies_manager;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movies_manager.model.Movie;
import com.example.movies_manager.pojo.moviesList.MoviesList;
import com.example.movies_manager.pojo.moviesList.Result;
import com.example.movies_manager.service.MovieApiService;
import com.example.movies_manager.service.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2M2Q0ZTY2MjMzNzU4MzZjNjhkZmIxMmRmODNkNTg3ZSIsIm5iZiI6MTczOTUyMzY4Ni43NDg5OTk4LCJzdWIiOiI2N2FmMDY2NmExOGViZjJhYzU4ZTVmNzIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.mdHxMj7gwa3s3z-tAKyFNFIhEGC6Isob1Zyrg3Z7DX8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movies_list);

        MovieApiService apiService = RetrofitService.getInstance();

        Call<MoviesList> call = apiService.getNowPlayingMovies(AUTH_TOKEN, "application/json", "fr-FR", 1);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Result movie : response.body().getResults()) {
                        Log.d("Movie", "Title: " + movie.getTitle() + ", Release Date: " + movie.getRelease_date());
                    }
                } else {
                    Log.e("API_ERROR", "Erreur : " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.e("API_ERROR", "Erreur réseau : " + t.getMessage());
            }
        });
    }
}