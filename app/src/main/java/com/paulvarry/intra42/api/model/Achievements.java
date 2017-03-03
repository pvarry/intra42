package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Achievements {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_DESCRIPTION = "description";
    private static final String API_TIER = "tier";
    private static final String API_KIND = "kind";
    private static final String API_VISIBLE = "visible";
    private static final String API_IMAGE = "image";
    private static final String API_NBR_OF_SUCCESS = "nbr_of_success";
    private static final String API_USERS_URL = "users_url";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_DESCRIPTION)
    public String description;
    @SerializedName(API_TIER)
    public String tier;
    @SerializedName(API_KIND)
    public String kind;
    @SerializedName(API_VISIBLE)
    public boolean visible;
    @SerializedName(API_IMAGE)
    public String imageUrl;
    @SerializedName(API_NBR_OF_SUCCESS)
    public int nbrOfSuccess;
    @SerializedName(API_USERS_URL)
    public String usersUrl;

    static public List<Achievements> get(JSONArray jsonArray) {
        List<Achievements> cursusArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                cursusArrayList.add(get(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cursusArrayList;
    }

    static public Achievements get(JSONObject jsonObject) {
        Achievements achievements = null;

        if (jsonObject != null) {
            achievements = new Achievements();

            try {
                achievements.id = jsonObject.getInt(API_ID);
                achievements.name = jsonObject.getString(API_NAME);
                achievements.description = jsonObject.getString(API_DESCRIPTION);
                achievements.tier = jsonObject.getString(API_TIER);
                achievements.kind = jsonObject.getString(API_KIND);
                achievements.visible = jsonObject.getBoolean(API_VISIBLE);
                achievements.imageUrl = jsonObject.getString(API_IMAGE);
                if (!jsonObject.isNull(API_NBR_OF_SUCCESS))
                    achievements.nbrOfSuccess = jsonObject.getInt(API_NBR_OF_SUCCESS);
                achievements.usersUrl = jsonObject.getString(API_USERS_URL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return achievements;
    }
}
