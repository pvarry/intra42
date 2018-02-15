package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterClusterMapContribute;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.ui.BasicActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ClusterMapContributeActivity extends BasicActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ExpandableListView listView;

    private List<Cluster> clusters;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, ClusterMapContributeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_map_contribute);
        setActionBarToggle(ActionBarToggle.ARROW);

        listView = findViewById(R.id.listView);

        clusters = new ArrayList<>(1);
        clusters.add(new Cluster(1, "Paris - E1", "e1"));
        clusters.add(new Cluster(1, "Paris - E2", "e2"));
        clusters.add(new Cluster(1, "Paris - E3", "e3"));
        clusters.add(new Cluster(7, "Fremont - E1Z1", "e1z1"));
        clusters.add(new Cluster(7, "Fremont - E1Z2", "e1z2"));
        clusters.add(new Cluster(7, "Fremont - E1Z3", "e1z3"));
        clusters.add(new Cluster(7, "Fremont - E1Z4", "e1z4"));

        // listView.setOnItemClickListener(this);
        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(this);

        listView.setAdapter(new ListAdapterClusterMapContribute(this, clusters));

        super.onCreateFinished();

        final Call<List<Cluster>> call = app.getApiServiceClusterMapContribute().getClusters();
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.nanoTime();
                try {
                    Response<List<Cluster>> res = call.execute();
                    long end = System.nanoTime();
                    long diff = end - start;
                    Log.d("cluster", String.valueOf(diff));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.list_view_cluster_map_contribute_cluster, null);
        final EditText editTextPrefix = view.findViewById(R.id.editTextPrefix);
        final EditText editTextCampus = view.findViewById(R.id.editTextCampus);
        final EditText editTextName = view.findViewById(R.id.editTextName);
        final EditText editTextNameShort = view.findViewById(R.id.editTextNameShort);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Create cluster");
        alert.setView(view);
        alert.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClusterMapContributeEditActivity.openIt(ClusterMapContributeActivity.this, editTextPrefix.getText().toString(), Integer.decode(editTextCampus.getText().toString()));
            }
        });
        alert.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ClusterMapContributeEditActivity.openIt(this, clusters.get(position));
    }
}
