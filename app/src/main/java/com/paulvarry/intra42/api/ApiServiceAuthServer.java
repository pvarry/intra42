package com.paulvarry.intra42.api;

import com.paulvarry.intra42.api.model.AccessToken;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServiceAuthServer {

    //  @FormUrlEncoded
    @GET("https://intra42.paulvarry.com/auth")
    Call<AccessToken> getNewAccessToken(
            @Query("code") String code,
            @Query("redirect_uri") String redirectUri);


}
