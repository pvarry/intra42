package com.paulvarry.intra42.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.CampusUsers;
import com.paulvarry.intra42.api.CursusUsers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is a interface for app Settings set with {@link com.paulvarry.intra42.activity.SettingsActivity SettingsActivity}.
 * <p>
 * Default value for list settings:
 * <p>
 * -1 : Nothing selected or error.
 * <p>
 * 0 : No specific element selected, all.
 */
public class AppSettings {

    public static SharedPreferences getSharedPreferences(Context context) {
        if (context == null)
            return null;
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getUserCampus(AppClass appClass) {
        int appForce = Advanced.getContentForceCampus(appClass);
        if (appForce > 0)
            return appForce;
        else if (appClass.me == null || appClass.me.cursusUsers == null)
            return -1;
        else {
            int campusUserIntra = 0;

            for (CampusUsers campusUsers : appClass.me.campusUsers) {
                if (campusUsers.isPrimary) {
                    campusUserIntra = campusUsers.campusId;
                    break;
                }
            }
            return campusUserIntra;
        }
    }

    public static int getUserCursus(AppClass appClass) {
        int appForce = Advanced.getContentForceCursus(appClass);
        if (appForce > 0)
            return appForce;
        else if (appClass.me == null || appClass.me.cursusUsers == null)
            return -1;
        else {
            List<CursusUsers> campusUserActive = new ArrayList<>();
            boolean is_subscribe42 = false;
            for (CursusUsers cursusUsers : appClass.me.cursusUsers) { // check for active campus
                if (cursusUsers.begin_at == null || cursusUsers.begin_at.after(new Date())) {
                    if (cursusUsers.cursusId == 1)
                        is_subscribe42 = true;
                    campusUserActive.add(cursusUsers);
                }
            }
            if (campusUserActive.size() == 0)
                return 0;
            else if (is_subscribe42)
                return 1; // id of cursus 42 is 1
            else
                return campusUserActive.get(0).cursusId;
        }
    }

    public static class Advanced {

        public static final String PREFERENCE_ADVANCED_ALLOW_ADVANCED = "switch_preference_advanced_allow_beta";
        public static final String PREFERENCE_ADVANCED_ALLOW_DATA = "switch_preference_advanced_allow_advanced_data";
        public static final String PREFERENCE_ADVANCED_ALLOW_MARKDOWN = "switch_preference_advanced_allow_markdown_renderer";
        public static final String PREFERENCE_ADVANCED_ALLOW_FRIENDS = "switch_preference_advanced_allow_friends";
        public static final String PREFERENCE_ADVANCED_ALLOW_SAVE_LOGS = "switch_preference_advanced_allow_save_logs_on_file";
        public static final String PREFERENCE_ADVANCED_FORCE_CURSUS = "list_preference_advanced_force_cursus";
        public static final String PREFERENCE_ADVANCED_FORCE_CAMPUS = "list_preference_advanced_force_campus";

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

        // save logs
        public static boolean getAllowSaveLogs(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_SAVE_LOGS, true);
            else
                return false;
        }

        public static boolean getAllowSaveLogs(Context context) {
            return context != null && getAllowSaveLogs(getSharedPreferences(context));
        }

        // force campus
        public static int getContentForceCampus(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return Integer.parseInt(settings.getString(PREFERENCE_ADVANCED_FORCE_CAMPUS, "-1"));
            else
                return -1;
        }

        public static int getContentForceCampus(Context context) {
            if (context == null)
                return -1;
            else
                return getContentForceCampus(getSharedPreferences(context));
        }

        // force cursus
        public static int getContentForceCursus(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return Integer.parseInt(settings.getString(PREFERENCE_ADVANCED_FORCE_CURSUS, "-1"));
            else
                return -1;
        }

        public static int getContentForceCursus(Context context) {
            if (context == null)
                return -1;
            else
                return getContentForceCursus(getSharedPreferences(context));
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
