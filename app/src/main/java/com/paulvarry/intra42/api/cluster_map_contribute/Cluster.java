package com.paulvarry.intra42.api.cluster_map_contribute;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.IBaseItemSmall;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;

import java.io.Serializable;
import java.util.HashMap;

public class Cluster implements IBaseItemSmall, Serializable, Comparable<Cluster> {

    public String name;
    public String nameShort;
    public String slug;
    @SerializedName("host_prefix")
    public String hostPrefix;
    @SerializedName("campus_id")
    public int campusId;
    @SerializedName("position")
    public int clusterPosition;
    @SerializedName("width")
    public int sizeX;
    @SerializedName("height")
    public int sizeY;
    public Location map[][];
    public String comment;

    public transient int freePosts;
    public transient int highlightPosts;
    public transient int posts;

    public Cluster(int campusId, String name, String hostPrefix) {
        this.campusId = campusId;
        this.name = name;
        this.nameShort = name;
        this.hostPrefix = hostPrefix;
    }

    public void computeFreePosts(HashMap<String, UsersLTE> locations) {
        for (Location[] row : map)
            for (Location post : row) {

                if (post.kind == Location.Kind.USER) {
                    posts++;
                    if (!locations.containsKey(post.host))
                        freePosts++;
                }
            }
    }

    public void computeHighlightPosts(ClusterStatus clusters) {
        UsersLTE user;

        highlightPosts = 0;
        if (map == null)
            return;
        for (Location[] row : map) {
            if (row == null)
                continue;
            for (Location post : row) {
                if (post == null)
                    continue;

                user = clusters.locations.get(post.host);
                if (post.computeHighlightPosts(clusters, user)) {
                    highlightPosts++;
                }
            }
        }
    }

    public void computeHighlightAndFreePosts(ClusterStatus clusters, HashMap<String, UsersLTE> locations) {
        UsersLTE user;

        highlightPosts = 0;
        freePosts = 0;
        if (map == null)
            return;
        for (Location[] row : map) {
            if (row == null)
                continue;
            for (Location post : row) {
                if (post == null)
                    break;
                user = clusters.locations.get(post.host);
                if (post.computeHighlightPosts(clusters, user)) {
                    highlightPosts++;
                }
                if (post.kind == Location.Kind.USER) {
                    posts++;
                    if (!locations.containsKey(post.host))
                        freePosts++;
                }
            }
        }
    }

    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public String getSub(Context context) {
        return slug;
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }

    @Override
    public int compareTo(@NonNull Cluster o) {
        if (clusterPosition != o.clusterPosition) {
            if (clusterPosition > o.clusterPosition)
                return 1;
            else
                return -1;
        }
        return name.compareTo(o.name);

    }

    @Override
    public int getId() {
        return 0;
    }
}
