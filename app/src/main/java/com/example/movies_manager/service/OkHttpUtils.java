package com.example.movies_manager.service;

import android.util.Log;

import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {

    public static String sendGetOkHttpRequest(String url) throws Exception {
        Log.v("TAG", url);
        OkHttpClient client = new OkHttpClient();

        //Création de la requête
        Request request = new Request.Builder().url(url).build();

        //Execution de la requête
        Response response = client.newCall(request).execute();

        //Analyse du code retour
        if(response.code() != HttpURLConnection.HTTP_OK){
            throw new Exception("Réponse du serveur incorrect : " + response.body());
        }
        else if(response.body()== null) {
            throw  new Exception("Réponse du serveur vide");
        }
        else{
            //Resultat de la requête
            return response.body().string();
        }
    }
}
