package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Expertises {
    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_SLUG = "slug";
    private static final String API_URL = "url";
    private static final String API_KIND = "kind";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_EXPERTISES_USERS_URL = "expertises_users_url";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_SLUG)
    public String slug;
    @SerializedName(API_URL)
    public String url;
    @SerializedName(API_KIND)
    public String kind;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_EXPERTISES_USERS_URL)
    public String expertises_users_url;

}
