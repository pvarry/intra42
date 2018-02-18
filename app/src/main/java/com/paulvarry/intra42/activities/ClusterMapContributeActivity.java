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
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterClusterMapContribute;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.cluster_map_contribute.Utils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ClusterMapContributeActivity extends BasicThreadActivity implements View.OnClickListener, AdapterView.OnItemClickListener, BasicThreadActivity.GetDataOnThread, ListAdapterClusterMapContribute.OnEditClickListener {

    private ExpandableListView listView;

    private List<Cluster> clusters;
    private List<Master> masters;

    private String cookie;

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

        registerGetDataOnOtherThread(this);

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

        // listView.setOnItemClickListener(this);
        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(this);

        ListAdapterClusterMapContribute adapter = new ListAdapterClusterMapContribute(this, masters);
        adapter.setOnEditListener(this);
        listView.setAdapter(adapter);
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

    /**
     * Triggered when the activity start.
     * <p>
     * This method is run on main Thread, so you can make api call.
     *
     * @return Return ThreadStatusCode of what appending {@link GetDataOnMain#getDataOnMainThread()}.
     */
    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {

        final Call<List<Master>> call = app.getApiServiceClusterMapContribute().getMasters();

        long start = System.nanoTime();
        try {
            Response<List<Master>> response = call.execute();
            long end = System.nanoTime();
            long diff = end - start;

            this.masters = response.body();
            String setCookie = response.headers().get("set-cookie");
            cookie = setCookie.substring(0, setCookie.indexOf(';'));

            Log.d("cluster", String.valueOf(diff));
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
        List<Cluster> list = new ArrayList<>();

        list.add(new Cluster(1, "E1", "e1", true));
        list.add(new Cluster(1, "E2", "e2", true));
        list.add(new Cluster(1, "E3", "e3", true));
        list.add(new Cluster(7, "E1Z1", "e1z1", true));
        list.add(new Cluster(7, "E1Z2", "e1z2", true));
        list.add(new Cluster(7, "E1Z3", "e1z3", true));
        list.add(new Cluster(7, "E1Z4", "e1z4", true));

        */

/*
        List<Master> listMaster = new ArrayList<>();
        listMaster.add(new Master("Paris - E1", "66i0i1atv", "so144wxi"));

        String content = ServiceGenerator.getGson().toJson(listMaster);

        HashMap<String, String> map = new HashMap<>();
        map.put("key", "q79vdcc1t");
        map.put("pad", content);
        map.put("monospace", "1");

        Response<Void> ret = app.getApiServiceClusterMapContribute().save(map, cookie).execute();
*/
    }

    @Override
    public void onClickEditLayout() {

    }

    @Override
    public void onClickEditMetadata(View finalConvertView, int groupPosition, final Master item) {

        Utils.loadClusterMap(this, app.getApiServiceClusterMapContribute(), item, new Utils.LoadClusterMapCallback() {
            @Override
            public void finish(final Cluster cluster, final String cookie) {

                final LayoutInflater inflater = LayoutInflater.from(ClusterMapContributeActivity.this);
                final View view = inflater.inflate(R.layout.list_view_cluster_map_contribute_cluster, null);
                final EditText editTextPrefix = view.findViewById(R.id.editTextPrefix);
                final EditText editTextCampus = view.findViewById(R.id.editTextCampus);
                final EditText editTextName = view.findViewById(R.id.editTextName);
                final EditText editTextNameShort = view.findViewById(R.id.editTextNameShort);
                final EditText editTextPosition = view.findViewById(R.id.editTextPosition);

                final AlertDialog.Builder alert = new AlertDialog.Builder(ClusterMapContributeActivity.this);
                editTextPrefix.setText(cluster.hostPrefix);
                editTextCampus.setText(String.valueOf(cluster.campusId));
                editTextNameShort.setText(cluster.nameShort);
                editTextName.setText(cluster.name);
                editTextPosition.setText(String.valueOf(cluster.clusterPosition));

                alert.setTitle("Edit cluster metadata");
                alert.setView(view);
                alert.setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cluster.name = editTextName.getText().toString();
                        cluster.nameShort = editTextNameShort.getText().toString();
                        cluster.clusterPosition = Integer.decode(editTextPosition.getText().toString());
                        cluster.campusId = Integer.decode(editTextCampus.getText().toString());
                        cluster.hostPrefix = editTextPrefix.getText().toString();

                        Utils.saveClusterMap(ClusterMapContributeActivity.this, app, item, cluster, new Utils.SaveClusterMapCallback() {
                            @Override
                            public void finish() {
                                refresh();
                            }

                            @Override
                            public void error(String error) {
                                Toast.makeText(app, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alert.show();
            }

            @Override
            public void error(String error) {
                Toast.makeText(app, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
