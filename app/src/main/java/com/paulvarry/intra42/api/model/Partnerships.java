package com.paulvarry.intra42.api.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.IBaseItemMedium;

public class Partnerships implements IBaseItemMedium {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_SLUG = "slug";
    private static final String API_TIER = "tier";
    private static final String API_URL = "url";
    private static final String API_PARTNERSHIPS_USERS_URL = "partnerships_users_url";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_SLUG)
    public String slug;
    @SerializedName(API_TIER)
    public int tier;
    @SerializedName(API_URL)
    public String url;
    @SerializedName(API_PARTNERSHIPS_USERS_URL)
    public String partnershipsUsersUrl;


    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public String getSub(Context context) {
        return slug;
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }

    @Override
    public String getDetail(Context context) {
        return "T" + this.tier;
    }

    @Override
    public int getId() {
        return id;
    }
}
