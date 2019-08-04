package com.paulvarry.intra42.api.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.utils.Pagination;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class Tags implements Serializable {

    static final String API_ID = "id";
    static final String API_NAME = "name";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;

    @Nullable
    public static List<Tags> getTags(ApiService api) {
        List<Tags> list = new ArrayList<>();
        int i = 0;
        int pageSize = 100;

        try {
            while (i < 10 && Pagination.canAdd(list, pageSize)) {
                Response<List<Tags>> response = api.getTags(pageSize, Pagination.getPage(list, pageSize)).execute();
                List<Tags> tmp = response.body();
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

    @Nullable
    public static List<Tags> getTagsUpdate(ApiService api, Date updateAtStart, Date updateAtEnd) {
        List<Tags> list = new ArrayList<>();
        int pageSize = 100;
        String range = ISO8601Utils.format(updateAtStart) + "," + ISO8601Utils.format(updateAtEnd);

        try {
            Response<List<Tags>> response = api.getTags(range, pageSize, Pagination.getPage(list, pageSize)).execute();
            if (!Tools.apiIsSuccessfulNoThrow(response))
                return null;

            int total = Integer.decode(response.headers().get("X-Total"));
            list.addAll(response.body());

            while (list.size() < total) {
                response = api.getTags(range, pageSize, Pagination.getPage(list, pageSize)).execute();
                if (!Tools.apiIsSuccessfulNoThrow(response))
                    return null;
                list.addAll(response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean equals(Tags tag) {
        return tag != null && tag.id == id;
    }
}
