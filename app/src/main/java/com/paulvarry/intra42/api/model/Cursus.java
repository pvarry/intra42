package com.paulvarry.intra42.api.model;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class Cursus implements Serializable {

    private final static String API_ID = "id";
    private final static String API_CREATED_AT = "created_at";
    private final static String API_NAME = "name";
    private final static String API_SLUG = "slug";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_CREATED_AT)
    public String created_at;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_SLUG)
    public String slug;

    public static List<String> getStrings(List<Cursus> cursuses) {
        List<String> l = new ArrayList<>();

        for (Cursus c : cursuses) {
            l.add(c.name);
        }

        return l;
    }

    @Nullable
    public static List<Cursus> getCursus(ApiService api) {
        List<Cursus> list = new ArrayList<>();
        int i = 0;
        int pageSize = 30;

        try {
            while (i < 10 && Pagination.canAdd(list, pageSize)) {
                Response<List<Cursus>> response = api.getCursus(pageSize, Pagination.getPage(list, pageSize)).execute();
                List<Cursus> tmp = response.body();
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

    public String toString() {
        return name;
    }

    public String toJson() {
        Gson gson = ServiceGenerator.getGson();
        return gson.toJson(this);
    }
}
