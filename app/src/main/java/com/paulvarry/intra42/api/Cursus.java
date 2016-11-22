package com.paulvarry.intra42.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.oauth.ServiceGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public String toString() {
        return name;
    }

    public String toJson() {
        Gson gson = ServiceGenerator.getGson();
        return gson.toJson(this);
    }
}
