package com.example.movies_manager.pojo.moviesList;

import com.google.gson.annotations.SerializedName;

public class FavoriteRequest {

    @SerializedName("media_type")
    private String mediaType;
    @SerializedName("media_id")
    private int mediaId;
    @SerializedName("favorite")
    private boolean favorite;

    public FavoriteRequest(String mediaType, int mediaId, boolean favorite) {
        this.mediaType = mediaType;
        this.mediaId = mediaId;
        this.favorite = favorite;
    }

}
