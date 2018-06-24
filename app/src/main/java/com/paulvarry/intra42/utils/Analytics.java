package com.paulvarry.intra42.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    public static final String EVENT_ERROR_API_42TOOLS = "error_api_42tools";
    public static final String USER_PROPERTY_APP_THEME = "app_theme";
    public static final String USER_PROPERTY_APP_THEME_DARK = "app_theme_dark";

    public static void settingUpdated(Context context) {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        SharedPreferences preferences = AppSettings.getSharedPreferences(context);

        mFirebaseAnalytics.setUserProperty(USER_PROPERTY_APP_THEME, AppSettings.Theme.getEnumTheme(preferences).name());
        if (AppSettings.Theme.getDarkThemeEnable(preferences))
            mFirebaseAnalytics.setUserProperty(USER_PROPERTY_APP_THEME_DARK, "DARK");
        else
            mFirebaseAnalytics.setUserProperty(USER_PROPERTY_APP_THEME_DARK, "LIGHT");
    }
}
