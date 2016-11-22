package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Campus {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_TIME_ZONE = "time_zone";
    private static final String API_LANGUAGE = "language";
    private static final String API_USERS_COUNT = "users_count";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_TIME_ZONE)
    public String timeZone;
    @SerializedName(API_USERS_COUNT)
    public int userCount;
    @SerializedName(API_LANGUAGE)
    public Language language;

    public static List<String> getStrings(List<Campus> campusList) {
        List<String> l = new ArrayList<>();

        for (Campus c : campusList) {
            l.add(c.name);
        }

        return l;
    }

}
