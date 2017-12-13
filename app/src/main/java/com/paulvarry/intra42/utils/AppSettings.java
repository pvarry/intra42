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
        public static final String SWITCH_PREFERENCE_ENABLE_PUT_TO_CALENDAR = "check_box_preference_put_to_calendar";
        public static final String SELECTED_CALENDAR = "sync_events_calendars";

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
            return getNotificationsAllow(settings) && settings.getBoolean(CHECKBOX_EVENTS, true);
        }

        public static boolean getNotificationsScales(Context context) {
            return context != null && getNotificationsScales(getSharedPreferences(context));
        }

        public static boolean getNotificationsScales(SharedPreferences settings) {
            return getNotificationsAllow(settings) && settings.getBoolean(CHECKBOX_SCALES, true);
        }

        /**
         * Return true when calendar sync is enable. This will check if calendar primary option is
         * enable in prefs and if this app have permissions for the calendar.
         *
         * @param context THe context.
         * @return Boolean
         */
        public static boolean getEnableCalendar(Context context) {
            return context != null
                    && getSharedPreferences(context).getBoolean(ENABLE_CALENDAR, false)
                    && permissionCalendarEnable(context);
        }

        /**
         * Check if calendar primary option have been touch, what event the value contained.
         *
         * @param settings Prefs.
         * @return true if the prefs contain calendar primary option.
         */
        public static boolean containEnableCalendar(SharedPreferences settings) {
            return settings.contains(ENABLE_CALENDAR);
        }

        /**
         * Check if calendar primary option have been touch, what event the value contained.
         *
         * @param context Context.
         * @return true if the prefs contain calendar primary option.
         */
        public static boolean containEnableCalendar(Context context) {
            return context != null && containEnableCalendar(getSharedPreferences(context));
        }

        /**
         * Set calendar primary param without check permission requirements. For better usability :
         * {@link Calendar#setEnableCalendarWithAutoSelect(Context, boolean)}
         *
         * @param context Context.
         * @param enable  value to set
         */
        public static void setEnableCalendar(Context context, boolean enable) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ENABLE_CALENDAR, enable);
            editor.apply();
        }

        /**
         * Check if put events to calendar after subscribe to a event from the app. Will also check
         * if primary option is enable and if calendar permissions are enable.
         * <p>
         * Note: this method will not verify if a calendar is correctly selected.
         *
         * @param context Context.
         * @return If you can put events to calendar after subscription.
         */
        public static boolean getCalendarAfterSubscription(Context context) {
            if (!getEnableCalendar(context))
                return false;
            SharedPreferences preferences = getSharedPreferences(context);
            return preferences.getBoolean(SWITCH_PREFERENCE_ENABLE_PUT_TO_CALENDAR, true);
        }

        /**
         * Check if auto sync of the events in the calendar is enable. If enable, subscribed events
         * will be periodically synchronised with the calendar.
         * <p>
         * Note: this method will not verify if a calendar is correctly selected.
         *
         * @param context Context.
         * @return If you can put events to calendar after subscription.
         */
        public static boolean getCalendarSync(Context context) {
            if (!getEnableCalendar(context))
                return false;
            SharedPreferences preferences = getSharedPreferences(context);
            return preferences.getBoolean(CHECKBOX_SYNC_CALENDAR, true);
        }

        /**
         * Get the id of the selected calendar of the phone to puts events.
         *
         * @param context The context.
         * @return Id of the selected calendar, -1 otherwise.
         */
        public static int getSelectedCalendar(Context context) {
            if (context == null)
                return -1;
            return getSelectedCalendar(getSharedPreferences(context));
        }

        /**
         * Get the id of the selected calendar of the phone to puts events.
         *
         * @param settings Prefs.
         * @return Id of the selected calendar, -1 otherwise.
         */
        public static int getSelectedCalendar(SharedPreferences settings) {
            return Integer.decode(settings.getString(SELECTED_CALENDAR, "-1"));
        }

        /**
         * Set the selected calendar.
         *
         * @param context  Context.
         * @param calendar Id of the selected calendar of the phone.
         */
        public static void setCalendarSelected(Context context, int calendar) {
            if (calendar == -1)
                return;
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SELECTED_CALENDAR, String.valueOf(calendar));
            editor.apply();
        }

        public static boolean getNotificationsAnnouncements(Context context) {
            return context != null && getNotificationsAnnouncements(getSharedPreferences(context));
        }

        public static boolean getNotificationsAnnouncements(SharedPreferences settings) {
            return settings.getBoolean(CHECKBOX_SCALES, false);
        }

    }

    static public class Theme {

        public static final String THEME = "list_preference_theme";
        public static final String ACTIONBAR_BACKGROUND = "switch_theme_actionbar_enable_background";

        public static boolean getActionBarBackgroundEnable(Context context) {
            SharedPreferences preferences = getSharedPreferences(context);
            return preferences.getBoolean(ACTIONBAR_BACKGROUND, true);
        }

        public static EnumTheme getEnumTheme(Context context) {
            SharedPreferences settings = getSharedPreferences(context);
            String string = settings.getString(THEME, "default");


            EnumTheme enumTheme;
            switch (string) {
                case "default":
                    enumTheme = EnumTheme.INTRA;
                    break;
                case "order":
                    enumTheme = EnumTheme.INTRA_ORDER;
                    break;
                case "assembly":
                    enumTheme = EnumTheme.INTRA_ASSEMBLY;
                    break;
                case "federation":
                    enumTheme = EnumTheme.INTRA_FEDERATION;
                    break;
                case "alliance":
                    enumTheme = EnumTheme.INTRA_ALLIANCE;
                    break;
                case "dark":
                    enumTheme = EnumTheme.STUDIOS_42_DARK;
                    break;
                case "android":
                    enumTheme = EnumTheme.ANDROID;
                    break;
                case "old":
                    enumTheme = EnumTheme.OLD;
                    break;
                default:
                    enumTheme = EnumTheme.INTRA;
            }

            return enumTheme;
        }

        public enum EnumTheme {
            DEFAULT,
            INTRA,
            INTRA_ORDER,
            INTRA_ASSEMBLY,
            INTRA_FEDERATION,
            INTRA_ALLIANCE,
            STUDIOS_42,
            STUDIOS_42_DARK,
            ANDROID,
            OLD
        }

    }

}
