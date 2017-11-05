package com.paulvarry.intra42.api;

import com.paulvarry.intra42.api.tools42.AccessToken;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService42Tools {

    @POST("/auth")
    Call<AccessToken> getAccessToken(@Query("access_token") String access_token);
}
