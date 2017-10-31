package com.paulvarry.intra42.utils;

import android.Manifest;
import android.content.ContentResolver;
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

    public static void addEventToCalendar(Context context, Events event) {

        long calID = 3;
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    private static String getEventUri(int id) {
        return "intra42://events/" + String.valueOf(id);
    }

    public static SparseArray<String> getCalendarList(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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

}
