package com.paulvarry.intra42.api.cantina;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MarvinMeals {

    static final String API_ID = "id";
    static final String API_MENU = "menu";
    static final String API_PRICE = "price";
    static final String API_BEGIN_AT = "begin_at";
    static final String API_END_AT = "end_at";
    static final String API_CREATED_AT = "created_at";
    static final String API_UPDATED_AT = "updated_at";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_MENU)
    public String menu;
    @SerializedName(API_PRICE)
    public int price;
    @SerializedName(API_BEGIN_AT)
    public Date beginAt;
    @SerializedName(API_END_AT)
    public Date endAt;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_UPDATED_AT)
    public Date updatedAt;

}
