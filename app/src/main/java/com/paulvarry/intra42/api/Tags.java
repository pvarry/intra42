package com.paulvarry.intra42.api;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.Tools.Pagination;
import com.plumillonforge.android.chipview.Chip;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class Tags implements Chip, Serializable {

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
        int pageSize = 30;

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

    @Override
    public String toString() {
        return name;
    }

    public boolean equals(Tags tag) {
        return tag != null && tag.id == id;
    }

    @Override
    public String getText() {
        return name;
    }
}
