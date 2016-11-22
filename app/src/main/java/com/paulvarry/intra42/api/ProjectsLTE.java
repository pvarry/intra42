package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

public class ProjectsLTE {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_SLUG = "slug";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_SLUG)
    public String slug;

}
