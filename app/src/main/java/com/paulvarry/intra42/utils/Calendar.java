package com.paulvarry.intra42.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;

import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.EventsUsers;

import java.util.ArrayList;
import java.util.TimeZone;

public class Calendar {

    public static boolean checkEventExist(Context context, int id) {

        ArrayList<String> nameOfEvent = new ArrayList<String>();
        ArrayList<String> startDates = new ArrayList<String>();
        ArrayList<String> endDates = new ArrayList<String>();
        ArrayList<String> descriptions = new ArrayList<String>();

        String selection = CalendarContract.Events.CUSTOM_APP_URI + " = ?";
        String[] selectionArgs = {getEventUri(id)};

        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, selection,
                        selectionArgs, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        for (int i = 0; i < CNames.length; i++) {

            nameOfEvent.add(cursor.getString(1));
            descriptions.add(cursor.getString(2));
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();

        }

        int count = cursor.getCount();

        cursor.close();
        //  return nameOfEvent;

        return count >= 1;
    }

    public static boolean syncEventCalendar(Context context, Events event) {
        return syncEventCalendar(context, event, null);
    }

    public static boolean syncEventCalendar(Context context, Events event, EventsUsers eventsUsers) {
        boolean calendarEnable = AppSettings.Notifications.getPutEventAfterSubscription(context);
        if (calendarEnable) {
            boolean eventOnCalendar = Calendar.checkEventExist(context, event.id);
            if (!eventOnCalendar && eventsUsers != null)
                addEventToCalendar(context, event);
            else if (eventOnCalendar && eventsUsers == null)
                removeEventFromCalendar(context, event);
            return true;
        }
        return false;
    }

    private static void addEventToCalendar(Context context, Events event) {

        long calID = AppSettings.Notifications.getSelectedCalendar(context);
        long startMillis;
        long endMillis;
        startMillis = event.beginAt.getTime();
        endMillis = event.endAt.getTime();

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, event.name);
        values.put(CalendarContract.Events.DESCRIPTION, event.description);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_LOCATION, event.location);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());

        values.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, BuildConfig.APPLICATION_ID);
        values.put(CalendarContract.Events.CUSTOM_APP_URI, getEventUri(event.id));

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    private static void removeEventFromCalendar(Context context, Events event) {

        Uri eventsUri = Uri.parse("content://com.android.calendar/events");

        ContentResolver resolver = context.getContentResolver();

        String selection = CalendarContract.Events.CUSTOM_APP_URI + " = ?";
        String[] selectionArgs = {getEventUri(event.id)};

        Cursor cursor = resolver.query(eventsUri, new String[]{"_id"}, selection, selectionArgs, null);
        while (cursor.moveToNext()) {
            long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
            resolver.delete(ContentUris.withAppendedId(eventsUri, eventId), null, null);
        }
        cursor.close();
    }

    private static String getEventUri(int id) {
        return "intra42://events/" + String.valueOf(id);
    }

    public static SparseArray<String> getCalendarList(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }


        SparseArray<String> calendar = new SparseArray<>();

        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
        };

        final ContentResolver cr = context.getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;

        Cursor cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

        while (cur.moveToNext()) {

            if (cur.getInt(3) < 300)
                continue;

            Long id = cur.getLong(0);
            String name = cur.getString(1);
            calendar.append(id.intValue(), name);
        }

        cur.close();

        return calendar;
    }

    public static SparseArray<String> getCalendarListPrimary(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }


        SparseArray<String> calendar = new SparseArray<>();

        final String[] EVENT_PROJECTION;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            EVENT_PROJECTION = new String[]{
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                    CalendarContract.Calendars.CALENDAR_COLOR,
                    CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                    CalendarContract.Calendars.IS_PRIMARY
            };
        } else
            EVENT_PROJECTION = new String[]{
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                    CalendarContract.Calendars.CALENDAR_COLOR,
                    CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                    CalendarContract.Calendars.OWNER_ACCOUNT,
                    CalendarContract.Calendars.ACCOUNT_NAME
            };

        final ContentResolver cr = context.getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;

        Cursor cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

        while (cur.moveToNext()) {

            if (cur.getInt(3) < 300)
                continue;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (cur.getInt(4) != 1)
                    continue;
            } else {
                if (!cur.getString(4).contentEquals(cur.getString(5)))
                    continue;
            }


            Long id = cur.getLong(0);
            String name = cur.getString(1);
            calendar.append(id.intValue(), name);
        }

        cur.close();

        return calendar;
    }

    public static boolean setEnableCalendarAutoSelectCalendar(Context context, boolean enable) {
        AppSettings.Notifications.setEnableCalendar(context, enable);
        if (enable && AppSettings.Notifications.getSelectedCalendar(context) == -1) {
            SparseArray<String> array = getCalendarListPrimary(context);
            if (array == null || array.size() == 0)
                return false;
            AppSettings.Notifications.setCalendarSelected(context, array.keyAt(0));
        }
        return true;
    }

}
