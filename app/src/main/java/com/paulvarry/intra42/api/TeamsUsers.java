package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamsUsers extends UserLTE {

    private static final String API_LEADER = "leader";
    private static final String API_OCCURRENCE = "occurrence";
    private static final String API_VALIDATED = "validated";

    @SerializedName(API_LEADER)
    public boolean leader;
    @SerializedName(API_OCCURRENCE)
    public int occurrence;
    @SerializedName(API_VALIDATED)
    public boolean validated;

    public static TeamsUsers get(JSONObject jsonObject) {
        TeamsUsers user = new TeamsUsers();

        if (jsonObject != null)
            try {
                user.id = jsonObject.getInt(API_ID);
                user.login = jsonObject.getString(API_LOGIN);
                if (!jsonObject.isNull(API_URL))
                    user.url = jsonObject.getString(API_URL);
                user.leader = jsonObject.getBoolean(API_LEADER);
                user.occurrence = jsonObject.getInt(API_OCCURRENCE);
                user.validated = jsonObject.getBoolean(API_VALIDATED);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return user;
    }

    public static List<TeamsUsers> getList(JSONArray jsonArray) {
        List<TeamsUsers> user = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                user.add(get(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return user;
    }
}
