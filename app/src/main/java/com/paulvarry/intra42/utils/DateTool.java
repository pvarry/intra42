package com.paulvarry.intra42.utils;

import android.content.Context;

import androidx.annotation.Nullable;

import com.paulvarry.intra42.R;

import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateTool extends java.util.Date {

    static public String getDurationAgo(Date date) {
        PrettyTime p = new PrettyTime(Locale.getDefault());
        return p.format(date);
    }

    static public String getDuration(Date before, Date after) {
        if (before == null || after == null)
            return null;

        Date date = new Date();
        date.setTime(date.getTime() - (after.getTime() - before.getTime()));
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        List<Duration> duration = prettyTime.calculatePreciseDuration(date);
        prettyTime.formatDurationUnrounded(duration);

        return prettyTime.formatDurationUnrounded(duration);
    }


    static public String getDuration(Date date) {
        if (date == null)
            return null;

        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        List<Duration> duration = prettyTime.calculatePreciseDuration(date);
        prettyTime.formatDurationUnrounded(duration);

        return prettyTime.formatDurationUnrounded(duration);
    }

    static public boolean isToday(java.util.Date date) {
        if (date == null)
            return false;

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(date.getTime());

        Calendar now = Calendar.getInstance();

        return now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) &&
                now.get(Calendar.MONTH) == smsTime.get(Calendar.MONTH) &&
                now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR);
    }

    public static boolean isTomorrow(java.util.Date date) {
        if (date == null)
            return false;
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(date.getTime());

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 1);

        return now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) &&
                now.get(Calendar.MONTH) == smsTime.get(Calendar.MONTH) &&
                now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR);

    }

    public static boolean sameDayOf(java.util.Date date, java.util.Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Nullable
    static public Boolean isInFuture(@Nullable Date date) {
        if (date == null)
            return null;
        return date.after(new Date(System.currentTimeMillis()));
    }

    static public boolean isInPast(Date date) {
        return date != null && date.before(new java.util.Date(System.currentTimeMillis()));
    }

    // get

    public static String getDateTimeLong(java.util.Date date) {
        DateFormat timeFormatter =
                DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.getDefault());

        return timeFormatter.format(date);
    }

    public static String getDateLong(java.util.Date date) {
        DateFormat timeFormatter =
                DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

        return timeFormatter.format(date);
    }

    /**
     * @param date Date
     * @return the date on string like "7:03 AM"
     */
    public static String getTimeShort(java.util.Date date) {
        DateFormat timeFormatter =
                DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        return timeFormatter.format(date);
    }

    public static String getMonthMedium(java.util.Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MMM", Locale.getDefault());
        return format.format(date);
    }

    public static String getDay(java.util.Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd", Locale.getDefault());
        return format.format(date);
    }

    public static String getTodayTomorrow(Context context, java.util.Date date, boolean hyphen) {
        String str = "";
        if (DateTool.isToday(date)) {
            str = context.getString(R.string.today);
            if (hyphen)
                str += " - ";
        } else if (DateTool.isTomorrow(date)) {
            str = context.getString(R.string.tomorrow);
            if (hyphen)
                str += " - ";
        }
        return str;
    }

    public static String getNowUTC() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new java.util.Date(System.currentTimeMillis()));
    }

    public static String getUTC(java.util.Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }
}
