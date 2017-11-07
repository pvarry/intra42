package com.paulvarry.intra42.api;

import com.paulvarry.intra42.api.tools42.AccessToken;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.api.tools42.FriendsSmall;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService42Tools {

    @POST("/auth")
    Call<AccessToken> getAccessToken(@Query("access_token") String access_token);

    @GET("friends")
    Call<List<FriendsSmall>> getFriends();

    @GET("friends/{id}")
    Call<Friends> getFriends(@Path("id") int userId);

    @POST("friends")
    Call<Friends> addFriend(@Query("user_id") int userId);

    @DELETE("friends/{id}")
    Call<Void> deleteFriend(@Path("id") int friendId);
}
