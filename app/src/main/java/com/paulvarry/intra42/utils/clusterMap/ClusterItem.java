package com.paulvarry.intra42.utils.clusterMap;

import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.model.UsersLTE;

import java.util.HashMap;

public class ClusterItem extends Cluster {

    public int freePosts;
    public int highlightPosts;
    public int posts;

    public LocationItem[][] map;

    public ClusterItem(int campusId, String name, String hostPrefix) {
        super(campusId, name, hostPrefix);

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
