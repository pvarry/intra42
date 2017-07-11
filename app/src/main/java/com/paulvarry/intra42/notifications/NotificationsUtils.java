package com.paulvarry.intra42.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.EventActivity;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.model.Announcements;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.EventsUsers;
import com.paulvarry.intra42.api.model.ScaleTeams;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.DateTool;

import java.util.Date;

public class NotificationsUtils {

    static private NotificationCompat.Builder getBaseNotification(Context context) {
        return new NotificationCompat.Builder(context, "intra42")
                .setSmallIcon(R.drawable.logo_42)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    public static void notify(Context context, Events events) {
        notify(context, events, null, false, false);
    }

    public static void notify(Context context, Events events, EventsUsers eventsUsers) {
        notify(context, events, eventsUsers, true, false);
    }

    public static void notify(Context context, Events events, EventsUsers eventsUsers, boolean autoCancel) {
        notify(context, events, eventsUsers, true, autoCancel);
    }

    public static void notify(final Context context, final Events events, EventsUsers eventsUsers, boolean activeAction, boolean autoCancel) {

        Intent notificationIntent = EventActivity.getIntent(context, events);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        final NotificationCompat.Builder notificationBuilder = getBaseNotification(context)
                .setContentTitle(events.name)
                .setContentText(events.description.replace('\n', ' '))
                .setSubText(context.getString(R.string.notifications_events_sub_text))
                .setWhen(events.beginAt.getTime())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(events.description))
                .setGroup(context.getString(R.string.notifications_events_unique_id))
                .setGroupSummary(true)
                .setChannelId(context.getString(R.string.notifications_events_unique_id))
                .setContentIntent(intent);

        if (autoCancel) {
            notificationBuilder.addAction(R.drawable.ic_event_black_24dp, "auto canceling in 5s", null);

            Handler h = new Handler();
            long delayInMilliseconds = 5000;
            h.postDelayed(new Runnable() {
                public void run() {
                    NotificationManagerCompat.from(context).cancel(context.getString(R.string.notifications_events_unique_id), events.id);
                }
            }, delayInMilliseconds);

        } else if (activeAction) {
            Intent notificationIntentAction = new Intent(context, IntentEvent.class);
            if (eventsUsers != null) {
                notificationIntentAction.putExtra(IntentEvent.ACTION, IntentEvent.ACTION_DELETE);
                notificationIntentAction.putExtra(IntentEvent.CONTENT_EVENT_USER_ID, eventsUsers.id);
                notificationIntentAction.putExtra(IntentEvent.CONTENT_EVENT_ID, eventsUsers.eventId);
            } else {
                notificationIntentAction.putExtra(IntentEvent.ACTION, IntentEvent.ACTION_CREATE);
                notificationIntentAction.putExtra(IntentEvent.CONTENT_EVENT_ID, events.id);
            }
            // intentEvent.putExtra(EventActivity.ARG_EVENT, ServiceGenerator.getGson().toJson(event));

            PendingIntent intentAction = PendingIntent.getService(context, 1000000 + events.id, notificationIntentAction, PendingIntent.FLAG_UPDATE_CURRENT);

            if (eventsUsers == null) {
                notificationBuilder.addAction(R.drawable.ic_event_black_24dp, context.getString(R.string.subscribe), intentAction);
            } else if (!events.beginAt.after(new Date()))
                notificationBuilder.addAction(R.drawable.ic_event_black_24dp, context.getString(R.string.unsubscribe), null);
            else
                notificationBuilder.addAction(R.drawable.ic_event_black_24dp, context.getString(R.string.unsubscribe), intentAction);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setCategory(Notification.CATEGORY_EVENT);
        }

        Notification notification = notificationBuilder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManagerCompat.from(context).notify(context.getString(R.string.notifications_events_unique_id), events.id, notification);
    }

    static void notify(AppClass app, ScaleTeams scaleTeams, boolean imminentCorrection) {
        String title;
        Intent notificationIntent = null;
        PendingIntent pendingIntentOpen = null;

        UsersLTE userAction = null;
        Integer projectsAction = null;

        if (imminentCorrection)
            title = app.getString(R.string.bookings_title_imminent);
        else
            title = app.getString(R.string.bookings_title_new);

        String text = "";
        if (scaleTeams.corrector != null && scaleTeams.corrector.equals(app.me)) { // i'm the corrector

            if (scaleTeams.correcteds == null || scaleTeams.correcteds.size() == 0)
                text = app.getString(R.string.bookings_correct_somebody)
                        .replace("_date_", DateTool.getDateTimeLong(scaleTeams.beginAt));
            else {
                userAction = scaleTeams.correcteds.get(0);
                notificationIntent = UserActivity.getIntent(app, userAction);
                text = app.getString(R.string.bookings_correct_login)
                        .replace("_date_", DateTool.getDateTimeLong(scaleTeams.beginAt))
                        .replace("_login_", userAction.login);
            }
        } else { // i'm corrected

            if (scaleTeams.corrector == null && scaleTeams.scale != null) {
                text = app.getString(R.string.bookings_corrected_by)
                        .replace("_date_", DateTool.getDateTimeLong(scaleTeams.beginAt))
                        .replace("_project_", scaleTeams.scale.name);
            } else if (scaleTeams.corrector != null && scaleTeams.scale != null) {
                notificationIntent = UserActivity.getIntent(app, scaleTeams.corrector);
                userAction = scaleTeams.corrector;
                text = app.getString(R.string.bookings_corrected_by_login)
                        .replace("_date_", DateTool.getDateTimeLong(scaleTeams.beginAt))
                        .replace("_project_", scaleTeams.scale.name)
                        .replace("_login_", scaleTeams.corrector.login);
            }
        }

        if (scaleTeams.teams != null) {
            projectsAction = scaleTeams.teams.projectId;
        }

        if (notificationIntent != null) {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntentOpen = PendingIntent.getActivity(app, 0, notificationIntent, 0);
        }

        NotificationCompat.Builder builder = getBaseNotification(app)
                .setChannelId(app.getString(R.string.notifications_bookings_unique_id))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntentOpen)
                .setSubText(app.getString(R.string.notifications_bookings_sub_text))
                .setWhen(scaleTeams.beginAt.getTime());

        if (userAction != null) {
            Intent intentUserAction = UserActivity.getIntent(app, userAction);
            if (intentUserAction != null)
                intentUserAction.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntentUser = PendingIntent.getActivity(app, userAction.id, intentUserAction, 0);
            builder.addAction(R.drawable.ic_person_black_24dp, app.getString(R.string.notification_see_user), pendingIntentUser);
        }

        if (projectsAction != null) { // add action open project
            Intent intentProjectAction;
            if (userAction != null)
                intentProjectAction = ProjectActivity.getIntent(app, projectsAction, userAction);
            else
                intentProjectAction = ProjectActivity.getIntent(app, projectsAction);
            if (intentProjectAction != null)
                intentProjectAction.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntentProject = PendingIntent.getActivity(app, projectsAction, intentProjectAction, 0);
            builder.addAction(R.drawable.ic_class_black_24dp, app.getString(R.string.notification_see_project), pendingIntentProject);
        }

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(app.getString(R.string.notifications_bookings_unique_id), scaleTeams.id, notification);
    }

    static void notify(Context context, Announcements announcements) {

        Intent notificationIntent = new Intent(context, EventActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder = getBaseNotification(context)
                .setChannelId(context.getString(R.string.notifications_events_unique_id))
                .setContentTitle(announcements.title)
                .setContentText(announcements.text.replace('\n', ' '))
                .setSubText(context.getString(R.string.notifications_announcements_sub_text) + " • " + announcements.author)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(announcements.text))
                .setGroup(context.getString(R.string.notifications_events_unique_id))
                .setGroupSummary(true)
                .setContentIntent(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setCategory(Notification.CATEGORY_EVENT);
        }

        Notification notification = notificationBuilder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManagerCompat.from(context).notify(context.getString(R.string.notifications_announcements_unique_id), announcements.id, notification);
    }

    static String getDateSince(SharedPreferences settings) {

        if (settings == null)
            return null;
        int since = Integer.parseInt(settings.getString(AppSettings.Notifications.FREQUENCY, "15"));

        if (since == -1)
            return null;
        Date date_ago = new Date(new Date().getTime() - (60000 * since));
        return DateTool.getUTC(date_ago) + "," + DateTool.getNowUTC();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void generateNotificationChannel(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channelEvent = new NotificationChannel(
                context.getString(R.string.notifications_events_unique_id),
                context.getString(R.string.notifications_events_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        // Configure the notification channel.
        channelEvent.setDescription(context.getString(R.string.notifications_events_channel_description));
        channelEvent.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        channelEvent.setLightColor(Color.RED);
        channelEvent.enableVibration(false);
        mNotificationManager.createNotificationChannel(channelEvent);

        NotificationChannel channelBookings = new NotificationChannel(
                context.getString(R.string.notifications_bookings_unique_id),
                context.getString(R.string.notifications_bookings_channel_name),
                NotificationManager.IMPORTANCE_HIGH);
        // Configure the notification channel.
        channelBookings.setDescription(context.getString(R.string.notifications_bookings_channel_description));
        channelBookings.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        channelBookings.setLightColor(Color.RED);
        channelBookings.enableVibration(true);
        mNotificationManager.createNotificationChannel(channelBookings);

        NotificationChannel channelAnnouncements = new NotificationChannel(
                context.getString(R.string.notifications_announcements_unique_id),
                context.getString(R.string.notifications_announcements_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        // Configure the notification channel.
        channelAnnouncements.setDescription(context.getString(R.string.notifications_announcements_channel_description));
        channelAnnouncements.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        channelAnnouncements.setLightColor(Color.RED);
        channelAnnouncements.enableVibration(true);
        mNotificationManager.createNotificationChannel(channelAnnouncements);
    }
}
