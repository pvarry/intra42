package com.paulvarry.intra42.api.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.ApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

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

    @Nullable
    public static List<Campus> getCampus(ApiService api) {
        List<Campus> list = new ArrayList<>();
        int i = 0;
        int pageSize = 30;

        try {
            while (i < 10 && Pagination.canAdd(list, pageSize)) {
                Response<List<Campus>> response = api.getCampus(pageSize, Pagination.getPage(list, pageSize)).execute();
                List<Campus> tmp = response.body();
                if (!response.isSuccessful())
                    break;
                list.addAll(tmp);
                ++i;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.isEmpty())
            return null;
        return list;
    }

}
