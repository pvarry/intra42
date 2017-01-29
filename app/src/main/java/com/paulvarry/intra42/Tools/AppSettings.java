package com.paulvarry.intra42.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSettings {


    public static SharedPreferences getSharedPreferences(Context context) {
        if (context == null)
            return null;
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static class ContentOption {

        public static final String CURSUS = "list_cursus";
        public static final String CAMPUS = "list_campus";

        // cursus

        public static int getCampus(SharedPreferences settings) {
            return Integer.parseInt(settings.getString(CAMPUS, "-1"));
        }

        public static int getCampus(Context context) {
            if (context == null)
                return -1;
            return getCampus(getSharedPreferences(context));
        }

        public static void setCampus(Context context, int value) {
            SharedPreferences settings = getSharedPreferences(context);
            SharedPreferences.Editor edit = settings.edit();
            edit.putString(CAMPUS, String.valueOf(value));
            edit.apply();
        }

        //campus

        public static int getCursus(SharedPreferences settings) {
            return Integer.parseInt(settings.getString(CURSUS, "-1"));
        }

        public static int getCursus(Context context) {
            if (context == null)
                return -1;
            return getCursus(getSharedPreferences(context));
        }

        public static void setCursus(Context context, int value) {
            SharedPreferences settings = getSharedPreferences(context);
            SharedPreferences.Editor edit = settings.edit();
            edit.putString(CURSUS, String.valueOf(value));
            edit.apply();
        }

    }

    public static class Advanced {

        public static final String PREFERENCE_ADVANCED_ALLOW_ADVANCED = "switch_preference_advanced_allow_beta";
        public static final String PREFERENCE_ADVANCED_ALLOW_DATA = "switch_preference_advanced_allow_advanced_data";
        public static final String PREFERENCE_ADVANCED_ALLOW_MARKDOWN = "switch_preference_advanced_allow_markdown_renderer";
        public static final String PREFERENCE_ADVANCED_ALLOW_FRIENDS = "switch_preference_advanced_allow_friends";

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
    }

    static public class Notifications {

        public static final String ALLOW = "notifications_allow";
        public static final String FREQUENCY = "notifications_frequency";
        public static final String CHECKBOX_EVENTS = "check_box_preference_notifications_events";
        public static final String CHECKBOX_SCALES = "check_box_preference_notifications_scales";
        public static final String CHECKBOX_ANNOUNCEMENTS = "check_box_preference_notifications_announcements";

        public static boolean getNotificationsAllow(SharedPreferences settings) {
            return settings.getBoolean(ALLOW, true);
        }

        public static boolean getNotificationsAllow(Context context) {
            return context != null && getNotificationsAllow(getSharedPreferences(context));
        }

        public static int getNotificationsFrequency(SharedPreferences settings) {
            return Integer.parseInt(settings.getString(FREQUENCY, "-1"));
        }

        public static int getNotificationsFrequency(Context context) {
            if (context != null)
                return getNotificationsFrequency(getSharedPreferences(context));
            return -1;
        }


        public static boolean getNotificationsEvents(SharedPreferences settings) {
            return settings.getBoolean(CHECKBOX_EVENTS, false);
        }

        public static boolean getNotificationsEvents(Context context) {
            return context != null && getNotificationsEvents(getSharedPreferences(context));
        }

        public static boolean getNotificationsScales(SharedPreferences settings) {
            return settings.getBoolean(CHECKBOX_SCALES, false);
        }

        public static boolean getNotificationsScales(Context context) {
            return context != null && getNotificationsScales(getSharedPreferences(context));
        }

        public static boolean getNotificationsAnnouncements(SharedPreferences settings) {
            return settings.getBoolean(CHECKBOX_SCALES, false);
        }

        public static boolean getNotificationsAnnouncements(Context context) {
            return context != null && getNotificationsAnnouncements(getSharedPreferences(context));
        }

    }

}
