package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

public class LanguagesUsers {

    private static final String API_ID = "id";
    private static final String API_LANGUAGE_ID = "language_id";
    private static final String API_USER_ID = "user_id";
    private static final String API_POSITION = "position";
    private static final String API_CREATED_AT = "created_at";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_LANGUAGE_ID)
    public int languageId;
    @SerializedName(API_USER_ID)
    public int userId;
    @SerializedName(API_POSITION)
    public int position;
    @SerializedName(API_CREATED_AT)
    public int createdAt;
}
