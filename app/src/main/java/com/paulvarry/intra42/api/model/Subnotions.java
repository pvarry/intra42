package com.paulvarry.intra42.api.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.IBaseItem;

import java.util.Date;
import java.util.List;

public class Subnotions implements IBaseItem {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_SLUG = "slug";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_NOTEPAD = "notepad";
    private static final String API_ATTACHMENTS = "attachments";
    private static final String API_NOTION = "notion";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_SLUG)
    public String slug;
    @SerializedName(API_CREATED_AT)
    public Date created_at;
    //    public String notepad;
    @SerializedName(API_ATTACHMENTS)
    public List<Attachments> attachments;

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
