package com.paulvarry.intra42.api;

import android.content.Context;

public interface IBaseItemDetail extends IBaseItem {

    /**
     * Use to fill detail on cell
     *
     * @return The detail of the item
     */
    String getDetail(Context context);

}
