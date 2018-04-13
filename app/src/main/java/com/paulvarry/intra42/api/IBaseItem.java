package com.paulvarry.intra42.api;

import android.content.Context;

public interface IBaseItem {

    /**
     * Usual use like primary text on a listView
     *
     * @param context Context
     * @return The name (title) of the item.
     */
    String getName(Context context);

    /**
     * Usual use like subtitle on a list view.
     *
     * @param context Context
     * @return The sub title.
     */
    String getSub(Context context);

    /**
     * Declare here the method to open this elem n a activity.
     *
     * @return A boolean to notice if opening is success.
     */
    boolean openIt(Context context);
}
