package com.paulvarry.intra42.api;

import com.paulvarry.intra42.api.cantina.MarvinMeals;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiServiceCantina {

    @GET("https://cantina.42.us.org/marvins_meals")
    Call<List<MarvinMeals>> getMeals();

}