package com.paulvarry.intra42.api.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.IBaseItemMedium;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    static public List<Partnerships> get(JSONArray jsonArray) {
        List<Partnerships> cursusArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                cursusArrayList.add(get(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cursusArrayList;
    }

    static public Partnerships get(JSONObject jsonObject) {
        Partnerships partnerships = null;

        if (jsonObject != null) {
            partnerships = new Partnerships();

            try {
                partnerships.id = jsonObject.getInt(API_ID);
                partnerships.name = jsonObject.getString(API_NAME);
                partnerships.tier = jsonObject.getInt(API_TIER);
                partnerships.slug = jsonObject.getString(API_SLUG);
                partnerships.url = jsonObject.getString(API_URL);
                partnerships.partnershipsUsersUrl = jsonObject.getString(API_PARTNERSHIPS_USERS_URL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return partnerships;
    }

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
        return "T" + String.valueOf(this.tier);
    }

    @Override
    public int getId() {
        return id;
    }
}
