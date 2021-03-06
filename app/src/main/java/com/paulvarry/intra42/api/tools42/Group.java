package com.paulvarry.intra42.api.tools42;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.R;

import java.util.List;
import java.util.Locale;

public class Group extends GroupSmall {

    @SerializedName("user_ids")
    public List<Integer> users;

    @Override
    public String getSub(Context context) {
        if (users != null && users.size() != 0)
            if (users.size() == 1)
                return String.format(Locale.getDefault(), context.getString(R.string.friends_summary_format_friend), users.size());
            else
                return String.format(Locale.getDefault(), context.getString(R.string.friends_summary_format_friends), users.size());
        return context.getString(R.string.friends_summary_no_friends);
    }
}