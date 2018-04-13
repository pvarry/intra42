package com.paulvarry.intra42.api.cluster_map_contribute;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItem;
import com.paulvarry.intra42.utils.ClusterMapContributeUtils;
import com.paulvarry.intra42.utils.DateTool;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Master implements Serializable, Comparable<Master>, IBaseItem {

    public String name;
    @Nullable
    public String locked_by;
    @Nullable
    public Date locked_at;
    public String key;

    public Master(String name, String key) {
        this.name = name;
        this.key = key;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(@NonNull Master o) {
        return name.compareToIgnoreCase(o.name);
    }

    /**
     * Usual use like primary text on a listView
     *
     * @param context Context
     * @return The name (title) of the item.
     */
    @Override
    public String getName(Context context) {
        return name;
    }

    /**
     * Usual use like subtitle on a list view.
     *
     * @param context Context
     * @return The sub title.
     */
    @Override
    public String getSub(Context context) {

        if (ClusterMapContributeUtils.canIEdit(this, AppClass.instance()))
            return null;

        String lockString = context.getString(R.string.cluster_map_contribute_locked_indicator);
        Calendar c = Calendar.getInstance();
        c.setTime(locked_at);
        c.add(Calendar.MINUTE, ClusterMapContributeUtils.MINUTE_LOCK);
        if (locked_by != null)
            lockString = lockString.replace("_user_", locked_by);
        lockString = lockString.replace("_time_", DateTool.getTimeShort(locked_at));
        lockString = lockString.replace("_timeFuture_", DateTool.getTimeShort(c.getTime()));
        return lockString;
    }

    /**
     * Declare here the method to open this elem n a activity.
     *
     * @param context Context
     * @return A boolean to notice if opening is success.
     */
    @Override
    public boolean openIt(Context context) {
        return false;
    }
}
