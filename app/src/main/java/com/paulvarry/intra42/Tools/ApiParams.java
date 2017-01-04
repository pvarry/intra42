package com.paulvarry.intra42.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

@Deprecated
public class ApiParams {

    public static final String PREFS_CURSUS = "list_cursus";
    public static final String PREFS_CAMPUS = "list_campus";

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getCursus(SharedPreferences settings) {
        return Integer.parseInt(settings.getString(PREFS_CURSUS, "-1"));
    }

    public static int getCursus(Context context) {
        return getCursus(getSharedPreferences(context));
    }

    public static void setCursus(Context context, int value) {
        SharedPreferences settings = getSharedPreferences(context);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(PREFS_CURSUS, String.valueOf(value));
        edit.apply();
    }

    public static int getCampus(SharedPreferences settings) {
        return Integer.parseInt(settings.getString(PREFS_CAMPUS, "-1"));
    }

    public static int getCampus(Context context) {
        return getCampus(getSharedPreferences(context));
    }

    public static void setCampus(Context context, int value) {
        SharedPreferences settings = getSharedPreferences(context);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(PREFS_CAMPUS, String.valueOf(value));
        edit.apply();
    }

}
