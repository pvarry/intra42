package com.paulvarry.intra42.api;

import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiServiceClusterMapContribute {

    @GET("https://notepad.pw/raw/q79vdcc1t")
    Call<List<Cluster>> getClusters();

}
