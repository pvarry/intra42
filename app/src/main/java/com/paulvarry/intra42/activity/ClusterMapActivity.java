package com.paulvarry.intra42.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.paulvarry.intra42.Adapter.SpinnerAdapterCampus;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.api.Campus;
import com.paulvarry.intra42.api.Locations;
import com.paulvarry.intra42.api.UserLTE;
import com.paulvarry.intra42.tab.user.UserActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class ClusterMapActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    GridLayout gridLayout;
    Spinner spinnerCluster;
    Spinner spinnerCampus;

    ConstraintLayout constraintLayoutLoading;
    TextView textViewStatus;

    SpinnerAdapterCampus adapterCampus;

    AppClass app;
    HashMap<String, UserLTE> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_map);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        spinnerCluster = (Spinner) findViewById(R.id.spinnerCluster);
        spinnerCampus = (Spinner) findViewById(R.id.spinnerCampus);
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        constraintLayoutLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoading);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        gridLayout.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.VISIBLE);

        app = (AppClass) getApplication();

        List<Campus> campus = new ArrayList<>();
        if (app.allCampus != null) {
            for (Campus c : app.allCampus) {
                if (c.id == 1)
                    campus.add(c);
            }

            adapterCampus = new SpinnerAdapterCampus(this, campus);
            spinnerCampus.setAdapter(adapterCampus);
            spinnerCampus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    List<String> list = new ArrayList<String>();
                    list.add("e1");
                    list.add("e2");
                    list.add("e3");
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ClusterMapActivity.this, android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCluster.setAdapter(dataAdapter);

                    spinnerCluster.setOnItemSelectedListener(ClusterMapActivity.this);

                    refreshClusterMap();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }

    void refreshClusterMap() {
        final ApiService api = app.getApiService();
        gridLayout.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.VISIBLE);
        textViewStatus.setText("page 1");
        new Thread(new Runnable() {
            @Override
            public void run() {

                final List<Locations> locationsTmp = new ArrayList<>();

                int page = 1;
                int pageSize = 100;
                try {
                    while (true) {

                        Response<List<Locations>> r = api.getLocations(adapterCampus.getItem(spinnerCampus.getSelectedItemPosition()).id, pageSize, page).execute();
                        if (r.isSuccessful()) {
                            locationsTmp.addAll(r.body());
                            if (r.body().size() == pageSize) {
                                page++;
                                final int finalPage = page;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewStatus.setText("page " + String.valueOf(finalPage));
                                    }
                                });
                            } else
                                break;
                        } else
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locations = new HashMap<>();
                        for (Locations l : locationsTmp) {
                            locations.put(l.host, l.user);
                        }

                        constraintLayoutLoading.setVisibility(View.GONE);
                        gridLayout.setVisibility(View.VISIBLE);

                        makeMap();
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void makeMap() {

        final int cluster[][] = {
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}, //r13
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r12
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r11
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r10
                {0, 0, 0, 0, 0, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0}, //r9
                {0, 0, 0, 0, 0, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r8
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r7
                {0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 0, 0, 0, 0, 0, 0}, //r6
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}, //r5
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r4
                {0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0}, //r3
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r2
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}  //r1
        };

        gridLayout.removeAllViews();

        int row = cluster.length;
        int max_poste = 0;
        gridLayout.setRowCount(row + 1);

        for (int r = 0; r < cluster.length; r++) {
            int realP = 1;

            for (int p = 0; p < cluster[r].length; p++) {
                if (cluster[r].length > max_poste) {
                    max_poste = cluster[r].length;
                    gridLayout.setColumnCount(max_poste + 1);
                }

                View viewContent;


                ImageView oImageView = new ImageView(this);
                if (cluster[r][p] == 0) {
                    oImageView.setImageResource(R.drawable.ic_desktop_mac_black_24dp);

                    String clusterSelected = (String) spinnerCluster.getSelectedItem();
                    String poste = clusterSelected + "r" + String.valueOf(13 - r) + "p" + String.valueOf(realP);
                    if (locations != null && locations.containsKey(poste)) {
                        final UserLTE user = locations.get(poste);
                        UserImage.setImageSmall(this, user, oImageView);
                        oImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UserActivity.openIt(ClusterMapActivity.this, user, ClusterMapActivity.this);
                            }
                        });
                    }

                    realP++;
                } else if (cluster[r][p] == 2)
                    oImageView.setImageResource(R.drawable.ic_close_black_24dp);
                else {
                    oImageView.setImageResource(R.drawable.ic_add_black_24dp);
                    oImageView.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.CLEAR);
                }
                viewContent = oImageView;

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = 100;
                param.width = 100;
                param.rightMargin = 5;
                param.topMargin = 5;
                param.setGravity(Gravity.FILL);
                param.columnSpec = GridLayout.spec(p);
                param.rowSpec = GridLayout.spec(r);
                viewContent.setLayoutParams(param);
                gridLayout.addView(viewContent);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        makeMap();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
