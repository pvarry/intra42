package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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

    private SparseArray<SparseArray<Location>> allLocations;

    public static void openIt(Context context, Cluster cluster) {
        Intent intent = new Intent(context, ClusterMapContributeEditActivity.class);

        if (cluster != null) {
            intent.putExtra(INTENT_CAMPUS, cluster.campusId);
            intent.putExtra(INTENT_HOST_PREFIX, cluster.hostPrefix);
        }
        context.startActivity(intent);
    }

    public static void openIt(Context context, String prefix, int campus) {
        Intent intent = new Intent(context, ClusterMapContributeEditActivity.class);

        intent.putExtra(INTENT_CAMPUS, campus);
        intent.putExtra(INTENT_HOST_PREFIX, prefix);

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

        gridLayout = findViewById(R.id.gridLayout);

        allLocations = new SparseArray<>();
        if (cluster.map != null) {
            for (int r = 0; r < cluster.map.length; r++) {

                for (int p = 0; p < cluster.map[r].length; p++) {

                    setLocation(cluster.map[r][p], p, r);
                    if (p > cluster.sizeX)
                        cluster.sizeX = p;
                }
                if (r > cluster.sizeY)
                    cluster.sizeY = r;
            }
            cluster.sizeX++;
            cluster.sizeY++;
        } else {
            cluster.sizeX = 10;
            cluster.sizeY = 10;
        }
        makeMap();

        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnFab();
            }
        });

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

    void makeMap() {

        // set base item size
        baseItemHeight = Tools.dpToPx(this, 42);
        baseItemWidth = Tools.dpToPx(this, 35);

        if (cluster == null)
            return;

        gridLayout.removeAllViews();
        gridLayout.removeAllViewsInLayout();
        gridLayout.setRowCount(cluster.sizeY);
        gridLayout.setColumnCount(cluster.sizeX);

        for (int x = 0; x < cluster.sizeX; x++) {

            for (int y = 0; y < cluster.sizeY; y++) {

                View view = makeMapItem(x, y);
                gridLayout.addView(view);
            }
        }
    }

    private View makeMapItem(int x, int y) {
        @Nullable
        Location location = getLocation(x, y);

        View view;
        LayoutInflater vi = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageViewContent;
        TextView textView;
        int padding = Tools.dpToPx(this, 2);
        GridLayout.LayoutParams paramsGridLayout;

        if (vi == null)
            return null;

        view = vi.inflate(R.layout.grid_layout_cluster_map_edit, gridLayout, false);
        view.setTag(new LocationWrapper(x, y, location));
        imageViewContent = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        view.setOnClickListener(this);

        textView.setVisibility(View.GONE);
        if (location != null)
            if (location.locationKind == Location.Kind.USER) {
                if (location.host == null ||
                        location.host.isEmpty() ||
                        location.host.contentEquals("null") ||
                        location.host.contentEquals("TBD"))
                    imageViewContent.setImageResource(R.drawable.ic_close_black_24dp);
                else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(location.host);
                    imageViewContent.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_desktop_mac_black_custom));
                }

            } else if (location.locationKind == Location.Kind.WALL)
                imageViewContent.setImageResource(R.color.colorClusterMapWall);
            else {
                imageViewContent.setImageResource(R.drawable.ic_add_black_24dp);
                imageViewContent.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.CLEAR);
            }

        paramsGridLayout = (GridLayout.LayoutParams) view.getLayoutParams();
        paramsGridLayout.columnSpec = GridLayout.spec(x);
        paramsGridLayout.rowSpec = GridLayout.spec(y);
        paramsGridLayout.setGravity(Gravity.FILL);
        paramsGridLayout.height = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsGridLayout.width = GridLayout.LayoutParams.WRAP_CONTENT;
        float sizeX = 1;
        float sizeY = 1;
        if (location != null) {
            sizeX = location.sizeX;
            sizeY = location.sizeY;
        }
        paramsGridLayout.height = (int) (baseItemHeight * sizeY);
        paramsGridLayout.width = (int) (baseItemWidth * sizeX);
        imageViewContent.setPadding(padding, padding, padding, padding);
        view.setLayoutParams(paramsGridLayout);

        return view;
    }

    /**
     * item location clicked
     *
     * @param v View
     */
    @Override
    public void onClick(final View v) {
        LocationWrapper location = null;

        if (v.getTag() instanceof LocationWrapper)
            location = (LocationWrapper) v.getTag();

        if (location == null)
            return;

        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.list_view_cluster_map_contribute_location, null);
        final EditText editText = view.findViewById(R.id.editTextHost);
        final TextInputLayout textInputLayoutHost = view.findViewById(R.id.textInputLayoutHost);
        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        final RadioButton radioButtonUser = view.findViewById(R.id.radioButtonUser);
        final RadioButton radioButtonCorridor = view.findViewById(R.id.radioButtonCorridor);
        final RadioButton radioButtonWall = view.findViewById(R.id.radioButtonWall);
//        Spinner spinnerLocationKind = view.findViewById(R.id.spinnerLocationKind);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        textInputLayoutHost.setVisibility(View.GONE);
        String title = "Edit: " + location.x + "," + location.y;
        if (location.location != null) {
            if (location.location.host != null)
                title += " (" + location.location.host + ")";

            editText.setText(location.location.host);
            if (location.location.locationKind == Location.Kind.USER) {
                radioButtonUser.setChecked(true);
                textInputLayoutHost.setVisibility(View.VISIBLE);
            } else if (location.location.locationKind == Location.Kind.CORRIDOR)
                radioButtonCorridor.setChecked(true);
            else if (location.location.locationKind == Location.Kind.WALL)
                radioButtonWall.setChecked(true);
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonUser:
                        textInputLayoutHost.setVisibility(View.VISIBLE);
                        break;
                    default:
                        textInputLayoutHost.setVisibility(View.GONE);
                }
            }
        });


        alert.setTitle(title);
        alert.setView(view);
        final LocationWrapper finalLocation = location;
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Location locationEdit = getLocation(finalLocation.x, finalLocation.y);
                if (locationEdit == null)
                    locationEdit = new Location();
                locationEdit.host = null;
                if (radioButtonUser.isChecked()) {
                    locationEdit.locationKind = Location.Kind.USER;
                    locationEdit.host = editText.getText().toString();
                } else if (radioButtonCorridor.isChecked())
                    locationEdit.locationKind = Location.Kind.CORRIDOR;
                else if (radioButtonWall.isChecked())
                    locationEdit.locationKind = Location.Kind.WALL;
                else return;

                setLocation(locationEdit, finalLocation.x, finalLocation.y);
                gridLayout.removeView(v);
                View view = makeMapItem(finalLocation.x, finalLocation.y);
                gridLayout.addView(view);
            }
        });
        alert.setNegativeButton(R.string.discard, null);
        alert.show();
    }

    private void clickOnFab() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClusterMapContributeEditActivity.this);
        String[] item = new String[]{"Add row on top", "Add row on bottom", "Add column on left", "Add column on right"};
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    cluster.sizeY++;
                    for (int i = 0; i < cluster.sizeX; i++) {
                        SparseArray<Location> row = allLocations.get(i);
                        if (row != null) {
                            for (int j = cluster.sizeY; j > 0; j--) {
                                row.put(j, row.get(j - 1));
                            }
                            row.remove(0);
                        }
                    }
                    makeMap();
                } else if (which == 1) {
                    cluster.sizeY++;
                    makeMap();
                } else if (which == 2) {
                    cluster.sizeX++;
                    for (int i = cluster.sizeX; i > 0; i--) {
                        allLocations.put(i, allLocations.get(i - 1));
                    }
                    allLocations.remove(0);
                    makeMap();
                } else if (which == 3) {
                    cluster.sizeX++;
                    makeMap();
                }
            }
        });
        builder.show();
    }

    @Nullable
    private Location getLocation(int x, int y) {
        SparseArray<Location> col = allLocations.get(x);
        if (col != null)
            return col.get(y);
        return null;
    }

    private void setLocation(Location location, int x, int y) {
        SparseArray<Location> col = allLocations.get(x);
        if (col == null) {
            allLocations.append(x, new SparseArray<Location>());
            col = allLocations.get(x);
        }
        col.put(y, location);
    }

    private class LocationWrapper {
        int x;
        int y;
        @Nullable
        Location location;

        LocationWrapper(int x, int y, @Nullable Location location) {
            this.x = x;
            this.y = y;
            this.location = location;
        }
    }
}
