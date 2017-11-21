package com.paulvarry.intra42.api.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.BaseItem;
import com.paulvarry.intra42.utils.DateTool;

import java.util.Date;

public class Locations implements BaseItem {

    private static final String API_ID = "id";
    private static final String API_BEGIN_AT = "begin_at";
    private static final String API_END_AT = "end_at";
    private static final String API_PRIMARY = "primary";
    private static final String API_HOST = "host";
    private static final String API_CAMPUS_ID = "campus_id";
    private static final String API_USER = "user";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_BEGIN_AT)
    public Date beginAt;
    @SerializedName(API_END_AT)
    public Date endAt;
    @SerializedName(API_PRIMARY)
    public boolean primary;
    @SerializedName(API_HOST)
    public String host;
    @SerializedName(API_CAMPUS_ID)
    public int campus;
    @SerializedName(API_USER)
    public UsersLTE user;

    @Override
    public String getName(Context context) {
        return user.login;
    }

    @Override
    public String getSub(Context context) {

        if (beginAt != null && endAt != null && !DateTool.sameDayOf(beginAt, endAt))
            return DateTool.getDateTimeLong(beginAt) + " - " + DateTool.getDateTimeLong(endAt);

        String ret = "";
        if (beginAt == null)
            ret += context.getString(R.string.location_undetermined);
        else
            ret += DateTool.getTimeShort(beginAt);
        ret += " - ";
        if (endAt == null)
            ret += context.getString(R.string.location_undetermined);
        else
            ret += DateTool.getTimeShort(endAt);

        return ret;
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }
}
