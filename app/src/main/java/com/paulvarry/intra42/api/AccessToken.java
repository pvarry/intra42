package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

public class AccessToken {

    private static final String API_ACCESS_TOKEN = "access_token";
    private static final String API_TOKEN_TYPE = "token_type";
    private static final String API_EXPIRES_IN = "expires_in";
    private static final String API_SCOPE = "scope";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_REFRESH_TOKEN = "refresh_token";

    @SerializedName(API_ACCESS_TOKEN)
    public String accessToken;

    @SerializedName(API_TOKEN_TYPE)
    public String tokenType;

    @SerializedName(API_EXPIRES_IN)
    public int expiresIn;

    @SerializedName(API_SCOPE)
    public String scope;

    @SerializedName(API_CREATED_AT)
    public int createdAt;

    @SerializedName(API_REFRESH_TOKEN)
    public String refreshToken;

}
