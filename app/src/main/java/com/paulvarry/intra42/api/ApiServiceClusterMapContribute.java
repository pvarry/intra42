package com.paulvarry.intra42.api;

import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiServiceClusterMapContribute {

    @GET("https://jsonblob.com/api/jsonBlob/9d4791dc-1bd4-11e8-88aa-9d6752c34362")
    Call<List<Master>> getMasters();

    @GET("https://jsonblob.com/api/jsonBlob/{key}")
    Call<Cluster> getCluster(@Path("key") String key);

    @PUT("https://jsonblob.com/api/jsonBlob/9d4791dc-1bd4-11e8-88aa-9d6752c34362")
    Call<Void> updateMaster(@Body List<Master> masters);

    @PUT("https://jsonblob.com/api/jsonBlob/{key}")
    Call<Void> updateCluster(@Body Cluster cluster, @Path("key") String key);

    @POST("https://jsonblob.com/api/jsonBlob")
    Call<Void> create(@Body Cluster cluster);

}
