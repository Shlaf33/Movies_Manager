package com.example.movies_manager.service;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module
@InstallIn(SingletonComponent.class)
public class RealmModule {

    @Provides
    @Singleton
    public RealmConfiguration provideRealmConfiguration(@ApplicationContext Context context) {
        Realm.init(context);
        return new RealmConfiguration.Builder()
                .name("movies.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    @Provides
    @Singleton
    public Realm provideRealm(RealmConfiguration configuration) {
        Realm.setDefaultConfiguration(configuration);
        return Realm.getDefaultInstance();
    }
}
