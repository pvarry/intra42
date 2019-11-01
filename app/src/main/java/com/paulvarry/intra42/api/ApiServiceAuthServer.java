package com.paulvarry.intra42.api;

import com.paulvarry.intra42.ui.galaxy.model.ProjectDataIntra;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServiceAuthServer {

    @GET("https://intra42.paulvarry.com/galaxy")
    Call<List<ProjectDataIntra>> getGalaxy(
            @Query("cursus_id") int cursusId,
            @Query("campus_id") int campusId,
            @Query("login") String login);

}
