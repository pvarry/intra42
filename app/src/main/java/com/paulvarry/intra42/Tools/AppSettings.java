package com.paulvarry.intra42.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSettings {


    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static class ContentOption {

        public static final String PREFERENCE_CONTENT_CURSUS = "list_cursus";
        public static final String PREFERENCE_CONTENT_CAMPUS = "list_campus";

        // cursus

        public static int getCampus(SharedPreferences settings) {
            return Integer.parseInt(settings.getString(PREFERENCE_CONTENT_CAMPUS, "-1"));
        }

        public static int getCampus(Context context) {
            if (context == null)
                return -1;
            return getCampus(getSharedPreferences(context));
        }

        public static void setCampus(Context context, int value) {
            SharedPreferences settings = getSharedPreferences(context);
            SharedPreferences.Editor edit = settings.edit();
            edit.putString(PREFERENCE_CONTENT_CAMPUS, String.valueOf(value));
            edit.apply();
        }

        //campus

        public static int getCursus(SharedPreferences settings) {
            return Integer.parseInt(settings.getString(PREFERENCE_CONTENT_CURSUS, "-1"));
        }

        public static int getCursus(Context context) {
            if (context == null)
                return -1;
            return getCursus(getSharedPreferences(context));
        }

        public static void setCursus(Context context, int value) {
            SharedPreferences settings = getSharedPreferences(context);
            SharedPreferences.Editor edit = settings.edit();
            edit.putString(PREFERENCE_CONTENT_CURSUS, String.valueOf(value));
            edit.apply();
        }

    }

    public static class Advanced {

        public static final String PREFERENCE_ADVANCED_ALLOW_ADVANCED = "switch_preference_advanced_allow_beta";
        public static final String PREFERENCE_ADVANCED_ALLOW_DATA = "switch_preference_advanced_allow_advanced_data";
        public static final String PREFERENCE_ADVANCED_ALLOW_MARKDOWN = "switch_preference_advanced_allow_markdown_renderer";
        public static final String PREFERENCE_ADVANCED_ALLOW_FRIENDS = "switch_preference_advanced_allow_friends";
        public static final String PREFERENCE_ADVANCED_ALLOW_CLUSTER_MAP = "switch_preference_advanced_allow_cluster_map";

        public static boolean getAllowAdvanced(SharedPreferences settings) {
            return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_ADVANCED, false);
        }

        public static boolean getAllowAdvanced(Context context) {
            return context != null && getAllowAdvanced(getSharedPreferences(context));
        }

        // advanced data
        public static boolean getAllowAdvancedData(SharedPreferences settings) {
            boolean mDefault = false;
            return getAllowAdvanced(settings) && settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_DATA, false);
        }

        public static boolean getAllowAdvancedData(Context context) {
            return context != null && getAllowAdvancedData(getSharedPreferences(context));
        }

        // markdown renderer
        public static boolean getAllowMarkdownRenderer(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_MARKDOWN, true);
            else
                return true;
        }

        public static boolean getAllowMarkdownRenderer(Context context) {
            if (context == null)
                return false;
            return getAllowMarkdownRenderer(getSharedPreferences(context));
        }

        // friends
        public static boolean getAllowFriends(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_FRIENDS, true);
            else
                return true;
        }

        public static boolean getAllowFriends(Context context) {
            return context != null && getAllowFriends(getSharedPreferences(context));
        }

        // cluster map
        public static boolean getAllowClusterMap(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_CLUSTER_MAP, false);
            else
                return false;
        }

        public static boolean getAllowClusterMap(Context context) {
            return getAllowClusterMap(getSharedPreferences(context));
        }
    }

    static public class Notifications {

        public static final String PREFERENCE_NOTIFICATIONS_CHECKBOX_EVENTS = "check_box_preference_notifications_events";
        public static final String PREFERENCE_NOTIFICATIONS_CHECKBOX_SCALES = "check_box_preference_notifications_scales";
        public static final String PREFERENCE_NOTIFICATIONS_FREQUENCY = "notifications_frequency";

        public static boolean getNotificationsEvents(SharedPreferences settings) {
            return settings.getBoolean(PREFERENCE_NOTIFICATIONS_CHECKBOX_EVENTS, false);
        }

        public static boolean getNotificationsEvents(Context context) {
            return context != null && getSharedPreferences(context).getBoolean(PREFERENCE_NOTIFICATIONS_CHECKBOX_EVENTS, false);
        }

        public static boolean getNotificationsScales(SharedPreferences settings) {
            return settings.getBoolean(PREFERENCE_NOTIFICATIONS_CHECKBOX_SCALES, false);
        }

        public static boolean getNotificationsScales(Context context) {
            return context != null && getSharedPreferences(context).getBoolean(PREFERENCE_NOTIFICATIONS_CHECKBOX_SCALES, false);
        }

        public static boolean getNotificationsFrequency(SharedPreferences settings) {
            return settings.getBoolean(PREFERENCE_NOTIFICATIONS_FREQUENCY, false);
        }

        public static boolean getNotificationsFrequency(Context context) {
            return context != null && getSharedPreferences(context).getBoolean(PREFERENCE_NOTIFICATIONS_FREQUENCY, false);
        }

    }

}
