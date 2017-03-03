package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Locations {

    private static final String API_ID = "id";
    private static final String API_BEGIN_AT = "begin_at";
    private static final String API_END_AT = "end_at";
    private static final String API_PRIMARY = "primary";
    private static final String API_HOST = "host";
    private static final String API_CAMPUS_ID = "campus_id";
    private static final String API_USER = "user";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_BEGIN_AT)
    public Date beginAt;
    @SerializedName(API_END_AT)
    public Date endAt;
    @SerializedName(API_PRIMARY)
    public boolean primary;
    @SerializedName(API_HOST)
    public String host;
    @SerializedName(API_CAMPUS_ID)
    public int campus;
    @SerializedName(API_USER)
    public UsersLTE user;

}
