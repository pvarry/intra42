package com.paulvarry.intra42.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.AccessToken;

public class Token {

    private static final String ACCESS_TOKEN_INTRA_42 = "accessToken";
    private static final String ACCESS_TOKEN_42TOOLS = "accessToken42Tools";

    private static SharedPreferences getShared(Context context) {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    public static void save(Context context, AccessToken accessToken) {
        SharedPreferences prefs = getShared(context);
        prefs.edit().putString(ACCESS_TOKEN_INTRA_42, ServiceGenerator.getGson().toJson(accessToken)).apply();
    }

    public static void save(Context context, com.paulvarry.intra42.api.tools42.AccessToken accessToken) {
        SharedPreferences prefs = getShared(context);
        prefs.edit().putString(ACCESS_TOKEN_42TOOLS, ServiceGenerator.getGson().toJson(accessToken)).apply();
    }

    public static AccessToken getIntra42TokenFromShared(Context context) {
        SharedPreferences prefs = getShared(context);

        String str = prefs.getString(ACCESS_TOKEN_INTRA_42, "");
        if (!str.isEmpty())
            return ServiceGenerator.getGson().fromJson(str, AccessToken.class);
        else
            return null;
    }

    public static com.paulvarry.intra42.api.tools42.AccessToken get42ToolsTokenFromShared(Context context) {
        SharedPreferences prefs = getShared(context);

        String str = prefs.getString(ACCESS_TOKEN_42TOOLS, "");
        if (!str.isEmpty())
            return ServiceGenerator.getGson().fromJson(str, com.paulvarry.intra42.api.tools42.AccessToken.class);
        else
            return null;
    }

    public static void removeToken(Context context) {
        SharedPreferences prefs = getShared(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(ACCESS_TOKEN_INTRA_42);
        edit.remove(ACCESS_TOKEN_42TOOLS);
        edit.apply();
    }

}
