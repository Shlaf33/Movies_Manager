package com.example.movies_manager;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.example.movies_manager.model.Movie;
import com.example.movies_manager.model.User;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class DatabaseOperationTests {
    private Realm realm;
    private RealmConfiguration config;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        Realm.init(context);
        config = new RealmConfiguration.Builder()
                .inMemory()
                .name("test-realm")
                .build();
        realm = Realm.getInstance(config);
    }

    @After
    public void tearDown() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    @Test
    public void testAddMovie() {
        final int movieId = 1001;
        realm.executeTransaction(r -> {
            Movie movie = r.createObject(Movie.class, movieId);
            movie.setTitle("Test Movie");
            movie.setFavorite(false);
        });
        Movie movie = realm.where(Movie.class).equalTo("id_title", movieId).findFirst();
        Assert.assertNotNull("Movie should be added", movie);
        Assert.assertEquals("Test Movie", movie.getTitle());
        Assert.assertFalse(movie.isFavorite());
    }

    @Test
    public void testDeleteMovie() {
        final int movieId = 1002;
        realm.executeTransaction(r -> {
            Movie movie = r.createObject(Movie.class, movieId);
            movie.setTitle("Movie To Delete");
        });
        realm.executeTransaction(r -> {
            Movie movie = r.where(Movie.class).equalTo("id_title", movieId).findFirst();
            if (movie != null) {
                movie.deleteFromRealm();
            }
        });
        Movie movieAfterDeletion = realm.where(Movie.class).equalTo("id_title", movieId).findFirst();
        Assert.assertNull("Movie should be deleted", movieAfterDeletion);
    }

    @Test
    public void testSetMovieFavorite() {
        final int movieId = 1003;
        realm.executeTransaction(r -> {
            Movie movie = r.createObject(Movie.class, movieId);
            movie.setTitle("Favorite Movie");
            movie.setFavorite(false);
        });
        realm.executeTransaction(r -> {
            Movie movie = r.where(Movie.class).equalTo("id_title", movieId).findFirst();
            if (movie != null) {
                movie.setFavorite(true);
            }
        });
        Movie movie = realm.where(Movie.class).equalTo("id_title", movieId).findFirst();
        Assert.assertNotNull(movie);
        Assert.assertTrue("Movie should be favorite", movie.isFavorite());
    }

    @Test
    public void testUnsetMovieFavorite() {
        final int movieId = 1004;
        realm.executeTransaction(r -> {
            Movie movie = r.createObject(Movie.class, movieId);
            movie.setTitle("Not Favorite Movie");
            movie.setFavorite(true);
        });
        realm.executeTransaction(r -> {
            Movie movie = r.where(Movie.class).equalTo("id_title", movieId).findFirst();
            if (movie != null) {
                movie.setFavorite(false);
            }
        });
        Movie movie = realm.where(Movie.class).equalTo("id_title", movieId).findFirst();
        Assert.assertNotNull(movie);
        Assert.assertFalse("Movie should not be favorite", movie.isFavorite());
    }

    @Test
    public void testAddUser() {
        int userId = 21845634;
        realm.executeTransaction(r -> {
            User user = r.createObject(User.class, userId);
            user.setUsername("TestUser");
            user.setSessionId("session123");
        });
        User user = realm.where(User.class).equalTo("id", userId).findFirst();
        Assert.assertNotNull("User should be added", user);
        Assert.assertEquals("TestUser", user.getUsername());
        Assert.assertEquals("session123", user.getSessionId());
    }

    @Test
    public void testDeleteUser() {
        int userId = 21845634;
        realm.executeTransaction(r -> {
            User user = r.createObject(User.class, userId);
            user.setUsername("UserToDelete");
        });
        realm.executeTransaction(r -> {
            User user = r.where(User.class).equalTo("id", userId).findFirst();
            if (user != null) {
                user.deleteFromRealm();
            }
        });
        User userAfterDeletion = realm.where(User.class).equalTo("id", userId).findFirst();
        Assert.assertNull("User should be deleted", userAfterDeletion);
    }
}
