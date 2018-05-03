package com.paulvarry.intra42.adapters;

import android.content.Context;

import com.paulvarry.intra42.api.IBaseItem;

public class RecyclerItem<T extends IBaseItem> implements IBaseItem {

    public static final int HEADER = 0;
    public static final int ITEM = 1;

    public final int type;
    public final T item;
    public String title;

    public RecyclerItem(T item) {
        this.type = ITEM;
        this.item = item;
        title = null;
    }

    public RecyclerItem(String header) {
        this.type = HEADER;
        this.item = null;
        title = header;
    }

    @Override
    public String toString() {
        if (item != null)
            return item.getName(null);
        return title;
    }

    @Override
    public String getName(Context context) {
        if (item != null)
            return item.getName(context);
        return title;
    }

    @Override
    public String getSub(Context context) {
        if (item != null)
            return item.getSub(context);
        return title;
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }
}