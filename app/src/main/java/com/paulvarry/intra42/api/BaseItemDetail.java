package com.paulvarry.intra42.api;

import android.content.Context;

public interface BaseItemDetail extends BaseItem {

    /**
     * Use to fill detail on cell
     *
     * @return The detail of the item
     */
    String getDetail(Context context);

}
