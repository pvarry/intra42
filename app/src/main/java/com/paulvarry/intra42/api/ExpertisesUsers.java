package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ExpertisesUsers {

    private static final String API_ID = "id";
    private static final String API_EXPERTISE_ID = "expertise_id";
    private static final String API_INTERESTED = "interested";
    private static final String API_VALUE = "value";
    private static final String API_CONTACT_ME = "contact_me";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_USER_ID = "user_id";
    private static final String API_EXPERTISE = "expertise";
    private static final String API_USER = "user";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_EXPERTISE_ID)
    public int expertiseId;
    @SerializedName(API_INTERESTED)
    public boolean interested;
    @SerializedName(API_VALUE)
    public int value;
    @SerializedName(API_CONTACT_ME)
    public boolean contactMe;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_USER_ID)
    public int userId;
    @SerializedName(API_EXPERTISE)
    public Expertises expertise;
    @SerializedName(API_USER)
    public UserLTE user;

}
