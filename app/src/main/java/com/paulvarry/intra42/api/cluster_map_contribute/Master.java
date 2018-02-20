package com.paulvarry.intra42.api.cluster_map_contribute;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

public class Master implements Serializable {

    public String name;
    @Nullable
    public String locked_by;
    @Nullable
    public Date locked_at;
    public String url;
    public String key;

    public Master(String name, String key, String url) {
        this.name = name;
        this.key = key;
        this.url = url;
    }
}
