package com.paulvarry.intra42.api;

import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiServiceClusterMapContribute {

    @GET("https://notepad.pw/raw/q79vdcc1t")
    Call<List<Master>> getMasters();

    @GET("https://notepad.pw/raw/{key}")
    Call<Cluster> getCluster(@Path("key") String key);

    @GET("https://notepad.pw/raw/{key}")
    Call<Void> getClusterEmpty(@Path("key") String key);

    @FormUrlEncoded
    @Headers("x-requested-with: XMLHttpRequest")
    @POST("https://notepad.pw/save")
    Call<Void> save(@FieldMap(encoded = false) Map<String, String> data, @Header("Cookie") String cookie);

    @GET("https://notepad.pw")
    Call<ResponseBody> getMainPage();

    @GET("https://notepad.pw/{url}")
    Call<ResponseBody> getPage(@Path("url") String key);

}
