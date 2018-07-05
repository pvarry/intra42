package com.paulvarry.intra42.api.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.IBaseItemSmall;

import java.util.Date;
import java.util.List;

public class Notions implements IBaseItemSmall {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_SLUG = "slug";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_SUBNOTIONS = "subnotions";
    private static final String API_TAGS = "tags";
    private static final String API_CURSUS = "cursus";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_SLUG)
    public String slug;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_SUBNOTIONS)
    public List<Subnotions> subnotions;
    @SerializedName(API_TAGS)
    public List<Tags> tags;
    @SerializedName(API_CURSUS)
    public List<Campus> cursus;

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
    public int getId() {
        return id;
    }

    static class Subnotions {

        private static final String API_ID = "id";
        private static final String API_NAME = "name";
        private static final String API_SLUG = "slug";
        private static final String API_CREATED_AT = "created_at";
        private static final String API_NOTEPAD = "notepad";

        public int id;
        public String name;
        public String slug;
        public Date createdAt;
//        public String notepad;
    }
}
