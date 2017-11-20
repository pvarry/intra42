package com.paulvarry.intra42.api;

import com.paulvarry.intra42.api.tools42.AccessToken;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.api.tools42.Group;
import com.paulvarry.intra42.api.tools42.GroupLarge;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService42Tools {

    /* Auth */
    @POST("auth")
    Call<AccessToken> getAccessToken(@Query("access_token") String access_token);

    /* Friends */
    @GET("friends")
    Call<List<FriendsSmall>> getFriends();

    @GET("friends/{id}")
    Call<Friends> getFriends(@Path("id") int userId);

    @POST("friends")
    Call<Friends> addFriend(@Query("user_id") int userId);

    @DELETE("friends/{id}")
    Call<Void> deleteFriend(@Path("id") int friendId);

    /* Groups */
    @GET("friends_groups")
    Call<List<Group>> getFriendsGroups();

    @GET("friends_groups/{id}")
    Call<GroupLarge> getFriendsGroups(@Path("id") int groupId);

    @PUT("friends_groups/{id}")
    Call<Group> updateFriendsGroup(@Path("id") int groupId, @Query("name") String name);

    @POST("friends_groups")
    Call<Group> createFriendsGroup(@Query("name") String name);

    @POST("friends_groups/{id}/friends")
    Call<Friends> addFriendToGroup(@Path("id") int groupId, @Query("user_id") int userId);

    @DELETE("friends_groups/{id}")
    Call<Void> deleteFriendsGroup(@Path("id") int groupId);

    @DELETE("friends_groups/{group_id}/friends/{user_id}")
    Call<Void> deleteFriendFromGroup(@Path("group_id") int groupId, @Path("user_id") int userId);

}
