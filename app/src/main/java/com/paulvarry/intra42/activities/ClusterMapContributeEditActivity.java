package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.ui.BasicEditActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.clusterMap.ClusterMapGenerator;
import com.paulvarry.intra42.utils.clusterMap.Firebase.Cluster;
import com.paulvarry.intra42.utils.clusterMap.Firebase.Location;

import java.io.IOException;

public class ClusterMapContributeEditActivity extends BasicEditActivity implements BasicThreadActivity.GetDataOnThread, View.OnClickListener {

    private static final String INTENT_CAMPUS = "intent_campus";
    private static final String INTENT_HOST_PREFIX = "intent_topic";
    float baseItemWidth;
    float baseItemHeight;
    private Cluster cluster;
    private boolean createCluster = false;
    private boolean unsavedData = false;
    private int campus;
    private String hostPrefix;
    private GridLayout gridLayout;

    public static void openIt(Context context, Cluster cluster) {
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
            cluster = new Cluster(tmpCampus, null, tmpPrefix);
            cluster.map = ClusterMapGenerator.getClusterMap(tmpCampus, tmpPrefix);
        } else
            super.registerGetDataOnOtherThread(this);
        onCreateFinished();

        gridLayout = findViewById(R.id.gridLayout);
        makeMap();
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

    void makeMap() {

        // set base item size
        baseItemHeight = Tools.dpToPx(this, 42);
        baseItemWidth = Tools.dpToPx(this, 35);

        if (cluster == null || cluster.map == null)
            return;

        gridLayout.removeAllViews();
        gridLayout.removeAllViewsInLayout();
        gridLayout.setRowCount(cluster.map.length);

        for (int r = 0; r < cluster.map.length; r++) {

            gridLayout.setColumnCount(cluster.map[r].length);
            for (int p = 0; p < cluster.map[r].length; p++) {

                if (cluster.map[r][p] == null)
                    break;

                View view = makeMapItem(cluster.map, r, p);
                gridLayout.addView(view);
            }
        }
    }

    private View makeMapItem(final Location[][] cluster, int r, int p) {

        final Location location = cluster[r][p];
        View view;
        LayoutInflater vi = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageViewContent;
        TextView textView;
        int padding = Tools.dpToPx(this, 2);
        GridLayout.LayoutParams paramsGridLayout;

        if (vi == null)
            return null;

        view = vi.inflate(R.layout.grid_layout_cluster_map_edit, gridLayout, false);
        imageViewContent = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);

        textView.setVisibility(View.GONE);
        if (location.locationKind == Location.Kind.USER) {
            view.setTag(location);
            view.setOnClickListener(this);

            textView.setVisibility(View.VISIBLE);
            textView.setText(location.host);
            if (location.host == null || location.host.contentEquals("null") || location.host.contentEquals("TBD"))
                imageViewContent.setImageResource(R.drawable.ic_close_black_24dp);
            else
                imageViewContent.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_desktop_mac_black_custom));

        } else if (location.locationKind == Location.Kind.WALL)
            imageViewContent.setImageResource(R.color.colorClusterMapWall);
        else {
            imageViewContent.setImageResource(R.drawable.ic_add_black_24dp);
            imageViewContent.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.CLEAR);
        }

        paramsGridLayout = (GridLayout.LayoutParams) view.getLayoutParams();
        paramsGridLayout.columnSpec = GridLayout.spec(p);
        paramsGridLayout.rowSpec = GridLayout.spec(r);
        paramsGridLayout.setGravity(Gravity.FILL);
        paramsGridLayout.height = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsGridLayout.width = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsGridLayout.height = (int) (baseItemHeight * location.sizeY);
        paramsGridLayout.width = (int) (baseItemWidth * location.sizeX);
        imageViewContent.setPadding(padding, padding, padding, padding);
        view.setLayoutParams(paramsGridLayout);

        return view;
    }

    @Override
    public void onClick(View v) {
        Location location = null;

        if (v.getTag() instanceof Location)
            location = (Location) v.getTag();

        if (location != null)
            Toast.makeText(app, location.host, Toast.LENGTH_SHORT).show();
    }
}
