package com.paulvarry.intra42.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.model.Users;

/**
 * This class is a interface for app Settings set with {@link com.paulvarry.intra42.activities.SettingsActivity SettingsActivity}.
 * <p>
 * Default value for list settings:
 * <p>
 * -1 : Nothing selected or error.
 * <p>
 * 0 : No specific element selected, all.
 */
public class AppSettings {

    final static String PREFERENCE_POEDITOR_ACTIVATED = "POEditor_activated";

    public static SharedPreferences getSharedPreferences(Context context) {
        if (context == null)
            return null;
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Return the forced Campus if is set or the main Campus of the user logged.
     *
     * @param app AppClass.
     * @return The id of a Campus.
     * @see Users#getCampusUsersToDisplay(Context)
     */
    public static int getAppCampus(AppClass app) {
        int forced = Advanced.getContentForceCampus(app);
        if (forced > 0)
            return forced;
        return getUserCampus(app);
    }

    /**
     * Return the main Campus of the user logged.
     *
     * @param app AppClass.
     * @return The id of a Campus.
     * @see Users#getCampusUsersToDisplay(Context)
     */
    public static int getUserCampus(AppClass app) {
        if (app.me != null)
            return app.me.getCampusUsersToDisplayID(app);
        return -1;
    }

    /**
     * Return the forced Cursus if is set or the main Cursus of the user logged.
     *
     * @param app AppClass.
     * @return The id of a Cursus.
     * @see Users#getCursusUsersToDisplay(Context)
     */
    public static int getAppCursus(AppClass app) {
        int forced = Advanced.getContentForceCursus(app);
        if (forced > 0)
            return forced;
        return getUserCursus(app);
    }

    /**
     * Return the main Cursus of the user logged.
     *
     * @param app AppClass.
     * @return The id of a Cursus.
     * @see Users#getCursusUsersToDisplay(Context)
     */
    public static int getUserCursus(AppClass app) {
        if (app.me != null)
            return app.me.getCursusUsersToDisplayID(app);
        return -1;
    }

    public static boolean getPOEditorActivated(Context context) {
        if (context == null)
            return true;
        else
            return getPOEditorActivated(getSharedPreferences(context));
    }

    public static boolean getPOEditorActivated(SharedPreferences settings) {
        return settings.getBoolean(PREFERENCE_POEDITOR_ACTIVATED, true);
    }

    public static void setPOEditorActivated(Context context, boolean activated) {
        if (context == null)
            return;
        else
            setPOEditorActivated(getSharedPreferences(context), activated);
    }

    public static void setPOEditorActivated(SharedPreferences settings, boolean activated) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFERENCE_POEDITOR_ACTIVATED, activated);
        editor.apply();
    }

    public static class Advanced {

        public static final String PREFERENCE_ADVANCED_ALLOW_ADVANCED = "switch_preference_advanced_allow_beta";
        public static final String PREFERENCE_ADVANCED_ALLOW_DATA = "switch_preference_advanced_allow_advanced_data";
        public static final String PREFERENCE_ADVANCED_ALLOW_MARKDOWN = "switch_preference_advanced_allow_markdown_renderer";
        public static final String PREFERENCE_ADVANCED_ALLOW_PAST_EVENTS = "switch_preference_advanced_allow_past_events";
        public static final String PREFERENCE_ADVANCED_ALLOW_SAVE_LOGS = "switch_preference_advanced_allow_save_logs_on_file";
        public static final String PREFERENCE_ADVANCED_FORCE_CURSUS = "list_preference_advanced_force_cursus";
        public static final String PREFERENCE_ADVANCED_FORCE_CAMPUS = "list_preference_advanced_force_campus";

        public static boolean getAllowAdvanced(Context context) {
            return context != null && getAllowAdvanced(getSharedPreferences(context));
        }

        public static boolean getAllowAdvanced(SharedPreferences settings) {
            return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_ADVANCED, false);
        }

        public static boolean getAllowAdvancedData(Context context) {
            return context != null && getAllowAdvancedData(getSharedPreferences(context));
        }

        // advanced data
        public static boolean getAllowAdvancedData(SharedPreferences settings) {
            boolean mDefault = false;
            return getAllowAdvanced(settings) && settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_DATA, false);
        }

        public static boolean getAllowMarkdownRenderer(Context context) {
            if (context == null)
                return false;
            return getAllowMarkdownRenderer(getSharedPreferences(context));
        }

        // markdown renderer
        public static boolean getAllowMarkdownRenderer(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_MARKDOWN, true);
            else
                return true;
        }

        public static boolean getAllowPastEvents(Context context) {
            return context != null && getAllowPastEvents(getSharedPreferences(context));
        }

        // friends
        public static boolean getAllowPastEvents(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_PAST_EVENTS, false);
            else
                return false;
        }

        public static boolean getAllowSaveLogs(Context context) {
            return context != null && getAllowSaveLogs(getSharedPreferences(context));
        }

        // save logs
        public static boolean getAllowSaveLogs(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return settings.getBoolean(PREFERENCE_ADVANCED_ALLOW_SAVE_LOGS, true);
            else
                return false;
        }

        public static int getContentForceCampus(Context context) {
            if (context == null)
                return -1;
            else
                return getContentForceCampus(getSharedPreferences(context));
        }

        // force campus
        public static int getContentForceCampus(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return Integer.parseInt(settings.getString(PREFERENCE_ADVANCED_FORCE_CAMPUS, "-1"));
            else
                return -1;
        }

        public static int getContentForceCursus(Context context) {
            if (context == null)
                return -1;
            else
                return getContentForceCursus(getSharedPreferences(context));
        }

        // force cursus
        public static int getContentForceCursus(SharedPreferences settings) {
            if (getAllowAdvanced(settings))
                return Integer.parseInt(settings.getString(PREFERENCE_ADVANCED_FORCE_CURSUS, "-1"));
            else
                return -1;
        }
    }

    static public class Notifications {

        public static final String ENABLE_NOTIFICATIONS = "notifications_allow";
        public static final String FREQUENCY = "notifications_frequency";
        public static final String CHECKBOX_EVENTS = "check_box_preference_notifications_events";
        public static final String CHECKBOX_SCALES = "check_box_preference_notifications_scales";
        public static final String ENABLE_CALENDAR = "switch_preference_enable_calendar";
        public static final String CHECKBOX_ANNOUNCEMENTS = "check_box_preference_notifications_announcements";
        public static final String LIST_CALENDAR = "sync_events_calendars";
        public static final String CHECKBOX_SYNC_CALENDAR = "check_box_preference_notifications_sync_calendar";

        public static boolean permissionCalendarEnable(Context context) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
        }

        public static boolean getNotificationsAllow(Context context) {
            return context != null && getNotificationsAllow(getSharedPreferences(context));
        }

        public static boolean getNotificationsAllow(SharedPreferences settings) {
            return settings.getBoolean(ENABLE_NOTIFICATIONS, true);
        }

        public static void setNotificationsAllow(Context context, boolean allow) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ENABLE_NOTIFICATIONS, allow);
            editor.apply();
        }

        public static int getNotificationsFrequency(Context context) {
            if (context != null)
                return getNotificationsFrequency(getSharedPreferences(context));
            return -1;
        }

        public static int getNotificationsFrequency(SharedPreferences settings) {
            return Integer.parseInt(settings.getString(FREQUENCY, "60"));
        }

        public static boolean getNotificationsEvents(Context context) {
            return context != null && getNotificationsEvents(getSharedPreferences(context));
        }

        public static boolean getNotificationsEvents(SharedPreferences settings) {
            return settings.getBoolean(CHECKBOX_EVENTS, false);
        }

        public static boolean getNotificationsScales(Context context) {
            return context != null && getNotificationsScales(getSharedPreferences(context));
        }

        public static boolean getNotificationsScales(SharedPreferences settings) {
            return settings.getBoolean(CHECKBOX_SCALES, false);
        }

        public static boolean getEnableCalendar(Context context) {
            return context != null
                    && getSharedPreferences(context).getBoolean(ENABLE_CALENDAR, false)
                    && permissionCalendarEnable(context);
        }

        public static boolean containEnableCalendar(SharedPreferences settings) {
            return settings.contains(ENABLE_CALENDAR);
        }

        public static boolean containEnableCalendar(Context context) {
            return context != null && containEnableCalendar(getSharedPreferences(context));
        }

        public static void setEnableCalendar(Context context, boolean enable) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ENABLE_CALENDAR, enable);
            editor.apply();
        }

        public static boolean getNotificationsAnnouncements(Context context) {
            return context != null && getNotificationsAnnouncements(getSharedPreferences(context));
        }

        public static boolean getNotificationsAnnouncements(SharedPreferences settings) {
            return settings.getBoolean(CHECKBOX_SCALES, false);
        }

    }

}
