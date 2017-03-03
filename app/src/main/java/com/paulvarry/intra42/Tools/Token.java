package com.paulvarry.intra42.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.AccessToken;

public class Token {

    public static void save(Context context, AccessToken accessToken) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        prefs.edit().putString("accessToken", ServiceGenerator.getGson().toJson(accessToken)).apply();
    }

    public static AccessToken getTokenFromShared(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

        String str = prefs.getString("accessToken", "");
        if (!str.isEmpty())
            return ServiceGenerator.getGson().fromJson(str, AccessToken.class);
        else
            return null;
    }

    public static void removeToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        prefs.edit().remove("accessToken").apply();

    }

}
