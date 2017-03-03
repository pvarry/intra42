package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

public class CampusUsers {

    private static final String API_ID = "id";
    private static final String API_USER_ID = "user_id";
    private static final String API_CAMPUS_ID = "campus_id";
    private static final String API_IS_PRIMARY = "is_primary";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_USER_ID)
    public int userId;
    @SerializedName(API_CAMPUS_ID)
    public int campusId;
    @SerializedName(API_IS_PRIMARY)
    public boolean isPrimary;
}
