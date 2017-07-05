package com.paulvarry.intra42.utils;

import android.content.Context;

import com.paulvarry.intra42.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateTool extends java.util.Date {

//    public static String getNow(Context context) {
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        format.setTimeZone(TimeZone.getDefault());
//        return format.format(new java.util.Date(System.currentTimeMillis()));
//    }
//
//
//    /**
//     * Example "12 Mar 12:16"
//     *
//     * @return The date
//     */
//    public String getDateTime() {
//
//        SimpleDateFormat format = new SimpleDateFormat("dd MMM HH:mm");
//        return format.format(this);
//    }
//
//    /**
//     * Example "12 Mar 12:16"
//     *
//     * @return The date
//     */
//    public String getDateTimeLarge() {
//
//        SimpleDateFormat format = new SimpleDateFormat("dd MMMM HH:mm");
//        return format.format(this);
//    }

//    /**
//     * Example "Thursday 21 April"
//     *
//     * @return date in string
//     */
//    public String getDate() {
//
//        SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMMM", locale);
//        return format.format(date);
//    }

//    /**
//     * Format : "dd MMMM"
//     * <br>
//     * Example "21 April"
//     *
//     * @return date in string
//     */
//    public String getDateLTE() {
//
//        SimpleDateFormat format = new SimpleDateFormat("dd MMMM");
//        return format.format(this);
//    }


//
//    @Deprecated
//    public String getRelativeTime() {
//        long now = System.currentTimeMillis();
//        return DateUtils.getRelativeTimeSpanString(this.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
//    }
//
//    public boolean sameDayOf(Date date) {
//        Calendar cal1 = Calendar.getInstance();
//        Calendar cal2 = Calendar.getInstance();
//        cal1.setTime(date.date);
//        cal2.setTime(this);
//        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
//    }
//


//    public String getTimeShort() {
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm", locale);
//        return format.format(date);
//    }
//
//    public String get(String format) {
//        SimpleDateFormat f = new SimpleDateFormat(format);
//        return f.format(this);
//    }
//
//    public String getDurationAgo() {
//        PrettyTime p = new PrettyTime(Locale.getDefault());
//        return p.format(this);
//    }

    static public String getDurationAgo(java.util.Date date) {
        PrettyTime p = new PrettyTime(Locale.getDefault());
        return p.format(date);
    }

//    public String getDuration(java.util.Date date) {
//        PrettyTime p = new PrettyTime(Locale.getDefault());
//        return p.formatApproximateDuration(date);
//    }



    /* NEW */

    // is

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

    static public boolean isInFuture(java.util.Date date) {
        return date.after(new java.util.Date(System.currentTimeMillis()));
    }

    static public boolean isInPast(java.util.Date date) {
        return date.before(new java.util.Date(System.currentTimeMillis()));
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
