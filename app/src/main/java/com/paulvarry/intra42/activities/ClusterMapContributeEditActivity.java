package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.ui.BasicEditActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.clusterMap.ClusterItem;

import java.io.IOException;

public class ClusterMapContributeEditActivity extends BasicEditActivity implements BasicThreadActivity.GetDataOnThread {

    private static final String INTENT_CAMPUS = "intent_campus";
    private static final String INTENT_HOST_PREFIX = "intent_topic";

    private ClusterItem cluster;
    private boolean createCluster = false;
    private boolean unsavedData = false;

    private int campus;
    private String hostPrefix;

    public static void openIt(Context context, ClusterItem cluster) {
        Intent intent = new Intent(context, ClusterMapContributeEditActivity.class);

        if (cluster != null) {
            intent.putExtra(INTENT_CAMPUS, cluster.campusId);
            intent.putExtra(INTENT_HOST_PREFIX, cluster.hostPrefix);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_map_contribute_edit);
        setActionBarToggle(ActionBarToggle.ARROW);

        Intent intent = getIntent();
        int tmpCampus = intent.getIntExtra(INTENT_CAMPUS, 0);
        String tmpPrefix = intent.getStringExtra(INTENT_HOST_PREFIX);
        if (tmpPrefix != null && !tmpPrefix.isEmpty() && tmpCampus != 0) {
            createCluster = true;
            cluster = new ClusterItem(0, null, null);
        } else
            super.registerGetDataOnOtherThread(this);
        onCreateFinished();
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public String getToolbarName() {
        if (createCluster)
            return getString(R.string.title_activity_cluster_map_contribute_edit);
        else
            return hostPrefix;
    }

    @Override
    protected void setViewContent() {

    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    protected boolean isCreate() {
        return createCluster;
    }

    @Override
    protected boolean haveUnsavedData() {
        return unsavedData;
    }

    @Override
    protected void onSave(Callback callBack) {

    }

    @Override
    protected void onDelete(Callback callBack) {

    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {

    }
}
