package com.paulvarry.intra42.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.model.EventsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;

public class Analytics {

    public static final String EVENT_ERROR_API_42TOOLS = "error_api_42tools";
    public static final String USER_PROPERTY_APP_THEME = "app_theme";
    public static final String USER_PROPERTY_APP_THEME_DARK = "app_theme_dark";

    private static final String EVENT_LOG_USER_ID = "user_id";
    private static final String EVENT_LOG_EVENT_ID = "event_id";
    private static final String EVENT_LOG_SOURCE = "source";
    private static final String EVENT_LOG_FRIEND_ID = "friend_id";

    private static FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(AppClass.instance());

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

    public static void slotSave(SlotsTools.SlotsGroup slotsGroup) {
        Bundle params = new Bundle();
        params.putSerializable("start_at", slotsGroup.beginAt);
        params.putSerializable("end_at", slotsGroup.endAt);
        firebaseAnalytics.logEvent("slot_save", params);
    }

    public static void slotCreate(SlotsTools.SlotsGroup slotsGroup) {
        Bundle params = new Bundle();
        params.putSerializable("start_at", slotsGroup.beginAt);
        params.putSerializable("end_at", slotsGroup.endAt);
        firebaseAnalytics.logEvent("slot_create", params);
    }

    public static void slotDelete(SlotsTools.SlotsGroup slotsGroup) {
        Bundle params = new Bundle();
        params.putSerializable("start_at", slotsGroup.beginAt);
        params.putSerializable("end_at", slotsGroup.endAt);
        firebaseAnalytics.logEvent("slot_delete", params);
    }

    public static void eventSubscribe(int eventId, int userId, EventSource source) {
        Bundle params = new Bundle();
        params.putInt(EVENT_LOG_EVENT_ID, eventId);
        params.putInt(EVENT_LOG_USER_ID, userId);
        params.putString(EVENT_LOG_SOURCE, source.name());
        firebaseAnalytics.logEvent("event_subscribe", params);
    }

    public static void eventUnsubscribe(EventsUsers event, EventSource source) {
        eventUnsubscribe(event.eventId, event.user_id, source);
    }

    public static void eventUnsubscribe(int eventId, int userId, EventSource source) {
        Bundle params = new Bundle();
        params.putInt(EVENT_LOG_EVENT_ID, eventId);
        params.putInt(EVENT_LOG_USER_ID, userId);
        params.putString(EVENT_LOG_SOURCE, source.name());
        firebaseAnalytics.logEvent("event_unsubscribe", params);
    }

    public static void friendAdd(UsersLTE friend, UsersLTE me) {
        Bundle params = new Bundle();
        params.putInt(EVENT_LOG_FRIEND_ID, friend.id);
        params.putInt(EVENT_LOG_USER_ID, me.id);
        firebaseAnalytics.logEvent("friend_add", params);
    }

    public static void friendRemove(UsersLTE friend, UsersLTE me) {
        Bundle params = new Bundle();
        params.putInt(EVENT_LOG_FRIEND_ID, friend.id);
        params.putInt(EVENT_LOG_USER_ID, me.id);
        firebaseAnalytics.logEvent("friend_remove", params);
    }

    public static void signInAttempt() {
        firebaseAnalytics.logEvent("sign_in_attempt", null);
    }

    public static void signInSuccess() {
        firebaseAnalytics.logEvent("sign_in_success", null);
    }

    public enum EventSource {
        NOTIFICATION, APPLICATION
    }
}
