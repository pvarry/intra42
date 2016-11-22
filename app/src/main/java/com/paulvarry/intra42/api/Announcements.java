package com.paulvarry.intra42.api;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Announcements {

    private final static String API_ID = "id";
    private final static String API_AUTHOR = "author";
    private final static String API_TITLE = "title";
    private final static String API_TEXT = "text";
    private final static String API_KIND = "kind";
    private final static String API_LINK = "link";
    private final static String API_CREATED_AT = "created_at";
    private final static String API_UPDATED_AT = "updated_at";
    private final static String API_EXPIRE_AT = "expire_at";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_AUTHOR)
    public String author;
    @SerializedName(API_TITLE)
    public String title;
    @SerializedName(API_TEXT)
    public String text;
    @SerializedName(API_KIND)
    public String king;
    @SerializedName(API_LINK)
    public String link;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_UPDATED_AT)
    public Date updatedAt;
    @SerializedName(API_EXPIRE_AT)
    public Date expireAt;

}
