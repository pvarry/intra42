package com.paulvarry.intra42.utils;

import android.content.Context;
import android.content.res.Resources;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.cache.CacheCampus;

import java.util.List;

public class ClusterMapContributeUtils {

    //todo
    static private String buildMasterName(AppClass app, Cluster cluster) {
        String name;

        if (cluster == null)
            return null;

        name = "id: " + String.valueOf(cluster.campusId) + " - " + cluster.name;
        List<Campus> campus = CacheCampus.getAllowInternet(app.cacheSQLiteHelper, app);
        if (campus != null) {
            for (Campus c : campus)
                if (c.id == cluster.campusId) {
                    name = c.name + " [id: " + String.valueOf(cluster.campusId) + "] - " + cluster.name;
                    break;
                }
        }
        return name;
    }

    public static int getResId(Context context, int campus) {

        if (context == null)
            return 0;
        Resources resources = context.getResources();
        if (resources == null)
            return 0;
        return resources.getIdentifier("cluster_map_campus_" + campus, "raw", context.getPackageName());
    }

}
