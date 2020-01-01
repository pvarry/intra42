package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

public class Coalitions {

    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("slug")
    public String slug;
    @SerializedName("image_url")
    public String imageUrl;
    @SerializedName("cover_url")
    public String coverUrl;
    @SerializedName("color")
    public String color;
    @SerializedName("score")
    public int score;
    @SerializedName("user_id")
    public int userId;

}
