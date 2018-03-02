package com.paulvarry.intra42.api.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.IBaseItem;

import java.util.List;
import java.util.StringJoiner;

public class ProjectsLTE implements IBaseItem {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_SLUG = "slug";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_SLUG)
    public String slug;

    public static String concatIds(List<ProjectsLTE> list) {
        if (list != null)
            return concatIds(list, 0, list.size());
        return null;
    }

    public static String concatIds(List<ProjectsLTE> list, int start, int size) {
        String eventsId;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            StringJoiner join = new StringJoiner(",");
            for (int i = 0; i < size; i++) {
                if (list.size() > start + i)
                    join.add(String.valueOf(list.get(start + i).id));
            }
            eventsId = join.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            String join = "";
            for (int i = 0; i < size; i++) {
                if (list.size() > start + i)
                    builder.append(join).append(String.valueOf(list.get(start + i).id));
                join = ",";
            }
            eventsId = builder.toString();
        }

        return eventsId;
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
}
