package com.paulvarry.intra42.utils.clusterMap;

import com.paulvarry.intra42.api.model.ProjectsUsers;

import androidx.annotation.Nullable;

public class ClusterLayersSettings {

    public LayerStatus layer = LayerStatus.FRIENDS;

    // user selection
    public String layerUserLogin = "";

    // project selection
    public String layerProjectSlug = "";
    public ProjectsUsers.Status layerProjectStatus = ProjectsUsers.Status.IN_PROGRESS;

    // location
    public String layerLocationPost = "";

    // level
    public float layerLevelMin = 0;
    public float layerLevelMax = -1;
    public int layerLevelCursus;
    public boolean layerLevelShowClosedCursusUser = false;

    public ClusterLayersSettings(ClusterLayersSettings old) {
        layer = old.layer;
        layerUserLogin = old.layerUserLogin;
        layerProjectSlug = old.layerProjectSlug;
        layerProjectStatus = old.layerProjectStatus;
        layerLocationPost = old.layerLocationPost;
        layerLevelMin = old.layerLevelMin;
        layerLevelMax = old.layerLevelMax;
        layerLevelCursus = old.layerLevelCursus;
        layerLevelShowClosedCursusUser = old.layerLevelShowClosedCursusUser;
    }

    public ClusterLayersSettings() {

    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ClusterLayersSettings))
            return false;
        ClusterLayersSettings o = (ClusterLayersSettings) obj;
        return layer == o.layer &&
                ((layer == LayerStatus.FRIENDS) ||
                        (layer == LayerStatus.USER && layerUserLogin.contentEquals(o.layerUserLogin)) ||
                        (layer == LayerStatus.PROJECT && layerProjectSlug.contentEquals(o.layerProjectSlug) && layerProjectStatus == o.layerProjectStatus) ||
                        (layer == LayerStatus.LOCATION && layerLocationPost.contentEquals(layerLocationPost)) ||
                        (layer == LayerStatus.LEVEL && layerLevelMin == o.layerLevelMin &&
                                layerLevelMax == o.layerLevelMax && layerLevelCursus == o.layerLevelCursus &&
                                layerLevelShowClosedCursusUser == o.layerLevelShowClosedCursusUser));
    }

    public enum LayerStatus {
        NONE(0), FRIENDS(1), PROJECT(2), USER(3), LOCATION(4), LEVEL(5);

        private final int id;

        LayerStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

}
