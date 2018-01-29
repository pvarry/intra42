package com.paulvarry.intra42.utils.clusterMap;

import com.paulvarry.intra42.api.model.UsersLTE;

import java.util.HashMap;

public class ClusterItem {

    public int campusId;
    public String name;
    public String hostPrefix;
    public int freePosts;
    public int highlightPosts;
    public int posts;

    public LocationItem[][] map;

    public ClusterItem(int campusId, String name, String hostPrefix) {
        this.campusId = campusId;
        this.name = name;
        this.hostPrefix = hostPrefix;

        map = ClusterMapGenerator.getClusterMap(campusId, hostPrefix);
    }

    public void computeFreePosts(HashMap<String, UsersLTE> locations) {
        for (LocationItem[] row : map)
            for (LocationItem post : row) {
                if (post.kind == 0) {
                    posts++;
                    if (!locations.containsKey(post.locationName))
                        freePosts++;
                }
            }
    }

    public void computeHighlightPosts(ClusterStatus clusters) {
        UsersLTE user;

        highlightPosts = 0;
        for (LocationItem[] row : map)
            for (LocationItem post : row) {
                user = clusters.locations.get(post.locationName);
                if (post.computeHighlightPosts(clusters, user)) {
                    highlightPosts++;
                }
            }
    }

}
