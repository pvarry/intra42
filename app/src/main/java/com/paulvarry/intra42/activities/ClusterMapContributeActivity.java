package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterClusterMapInfo;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.utils.clusterMap.ClusterItem;

import java.util.ArrayList;
import java.util.List;

public class ClusterMapContributeActivity extends BasicActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView textViewDescription;
    private ExpandableHeightListView listView;

    private List<ClusterItem> clusters;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, ClusterMapContributeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_map_contribute);
        setActionBarToggle(ActionBarToggle.ARROW);

        textViewDescription = findViewById(R.id.textViewDescription);
        listView = findViewById(R.id.listView);

        clusters = new ArrayList<>(1);
        clusters.add(new ClusterItem(1, "Paris - E1", "e1"));

        listView.setExpanded(true);
        listView.setOnItemClickListener(this);
        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(this);

        listView.setAdapter(new ListAdapterClusterMapInfo(this, clusters));

        super.onCreateFinished();
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    @Override
    protected void setViewContent() {

    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void onClick(View v) {
        ClusterMapContributeEditActivity.openIt(this, null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ClusterMapContributeEditActivity.openIt(this, clusters.get(position));
    }
}
