package com.paulvarry.intra42.api.tools42;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.BaseItem;

import java.util.List;
import java.util.Locale;

public class Group extends GroupSmall implements BaseItem {

    @SerializedName("user_ids")
    public List<Integer> users;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSub() {
        if (users != null && users.size() != 0)
            if (users.size() == 1)
                return String.format(Locale.getDefault(), "%d friend", users.size());
            else
                return String.format(Locale.getDefault(), "%d friends", users.size());
        return "No friend";
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }
}