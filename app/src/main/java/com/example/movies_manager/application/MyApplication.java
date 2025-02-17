package com.example.movies_manager.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("movies.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded() // Pour le développement, à adapter pour la prod
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
