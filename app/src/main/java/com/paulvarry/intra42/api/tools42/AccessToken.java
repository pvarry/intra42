package com.paulvarry.intra42.api.tools42;

import com.google.gson.annotations.SerializedName;

public class AccessToken {
    private static final String API_ACCESS_TOKEN = "access_token";

    @SerializedName(API_ACCESS_TOKEN)
    public String accessToken;
}
