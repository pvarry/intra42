package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Skills {

    static final String API_ID = "id";
    static final String API_NAME = "name";
    static final String API_CREATED_AT = "created_at";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;

}
