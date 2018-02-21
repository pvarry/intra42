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
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Location;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;
import com.paulvarry.intra42.ui.BasicEditActivity;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.cluster_map_contribute.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClusterMapContributeEditActivity extends BasicEditActivity implements View.OnClickListener {

    private static final String INTENT_CLUSTER = "cluster";
    private static final String INTENT_MASTER = "master";

    float baseItemWidth;
    float baseItemHeight;
    private Cluster cluster;
    private Master master;
    private boolean createCluster = false;
    private boolean unsavedData = false;
    private GridLayout gridLayout;

    private Date lockEnd;
    private boolean saveChangesDisplayed;

    private SparseArray<SparseArray<Location>> allLocations;

    public static void openIt(Context context, Cluster cluster, Master master) {
        Intent intent = new Intent(context, ClusterMapContributeEditActivity.class);

        if (cluster != null) {
            intent.putExtra(INTENT_CLUSTER, cluster);
            intent.putExtra(INTENT_MASTER, master);
        }
        context.startActivity(intent);
    }

    public static void openIt(Context context, Cluster cluster) {
        Intent intent = new Intent(context, ClusterMapContributeEditActivity.class);

        if (cluster != null) {
            intent.putExtra(INTENT_CLUSTER, cluster);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_map_contribute_edit);
        setActionBarToggle(ActionBarToggle.ARROW);

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_MASTER)) {
            cluster = (Cluster) intent.getSerializableExtra(INTENT_CLUSTER);
            master = (Master) intent.getSerializableExtra(INTENT_MASTER);

            if (master.locked_at != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(master.locked_at);
                cal.add(Calendar.MINUTE, Utils.MINUTE_LOCK);
                lockEnd = cal.getTime();

                updateActionBar();
            }
        }

        gridLayout = findViewById(R.id.gridLayout);

        allLocations = new SparseArray<>();
        int sizeX = 0;
        int sizeY = 0;
        if (cluster.map != null) {
            for (int x = 0; x < cluster.map.length; x++) {

                for (int y = 0; y < cluster.map[x].length; y++) {

                    setLocation(cluster.map[x][y], x, y);
                    if (y > sizeY)
                        sizeY = y;
                }
                if (x > sizeX)
                    sizeX = x;
            }

            if (cluster.sizeX <= 0)
                cluster.sizeX = sizeX + 1;
            if (cluster.sizeY <= 0)
                cluster.sizeY = sizeY + 1;
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

        Timer timer = new Timer();
        timer.schedule(new RefreshActionBar(), new Date(), 500);

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
        else if (master != null)
            return master.name;
        else
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
    protected boolean isCreate() {
        return true;
    }

    @Override
    protected boolean haveUnsavedData() {
        return unsavedData;
    }

    @Override
    protected void onSave(final Callback callBack) {

        Location[][] tmp = new Location[allLocations.size()][];
        for (int i = 0; i < allLocations.size(); i++) {
            int keyI = allLocations.keyAt(i);
            SparseArray<Location> col = allLocations.get(keyI);
            tmp[i] = new Location[col.size()];

            for (int j = 0; j < col.size(); j++) {
                int keyJ = allLocations.keyAt(j);
                Location cell = col.get(keyJ);

                tmp[i][j] = cell;
                //height
            }
        }
        cluster.map = tmp;

        Utils.saveClusterMap(this, app, master, cluster, new Utils.SaveClusterMapCallback() {
            @Override
            public void finish() {
                callBack.succeed();
            }

            @Override
            public void error(String error) {
                callBack.message(error);
            }
        });
    }

    @Override
    protected void onDelete(Callback callBack) {

    }

    private void updateActionBar() {
        Calendar calendar = Calendar.getInstance();
        long duration = lockEnd.getTime() - new Date().getTime();

        if (!saveChangesDisplayed && duration < 30 * 1000) { // 10 seconds

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Time up");
            b.setMessage("Lock will soon time up, you may consider saves your changes before someone else lock this cluster.");
            b.setPositiveButton(R.string.ok, null);
            b.show();

            saveChangesDisplayed = true;
        }
        if (duration < 0) {
            toolbar.setSubtitle("Save your changes asap");
            return;
        }
        calendar.setTimeInMillis(duration);
        int m = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, -m);
        int s = calendar.get(Calendar.SECOND);

        toolbar.setSubtitle("Cluster lock for: " + String.valueOf(m) + " m and " + String.valueOf(s) + " s");
    }

    void makeMap() {

        // set base item size
        baseItemHeight = Tools.dpToPx(this, 42);
        baseItemWidth = Tools.dpToPx(this, 35);

        if (cluster == null)
            return;

        gridLayout.removeAllViews();
        gridLayout.removeAllViewsInLayout();
        gridLayout.setRowCount(cluster.sizeY + 1);
        gridLayout.setColumnCount(cluster.sizeX + 1);

        for (int x = 0; x < cluster.sizeX; x++) {

            for (int y = 0; y < cluster.sizeY; y++) {

                View view = inflateClusterMapItem(x, y);
                gridLayout.addView(view);
            }

            View view = inflateClusterMapController(x, cluster.sizeY);
            gridLayout.addView(view);
        }

        for (int y = 0; y < cluster.sizeY + 1; y++) {
            View view = inflateClusterMapController(cluster.sizeX, y);
            gridLayout.addView(view);
        }
    }

    private View inflateClusterMapItem(int x, int y) {
        @Nullable
        Location location = getLocation(x, y);

        View view;
        LayoutInflater vi = LayoutInflater.from(this);
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

    private View inflateClusterMapController(int x, int y) {
        LayoutInflater vi = LayoutInflater.from(this);
        GridLayout.LayoutParams paramsGridLayout;
        Location location = null;

        View view = vi.inflate(R.layout.grid_layout_cluster_map_edit_controller, gridLayout, false);

        if (x == cluster.sizeX && y == cluster.sizeY)
            location = null;
        else if (x == cluster.sizeX)
            location = cluster.map[0][y];
        else if (y == cluster.sizeY)
            location = cluster.map[x][0];

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

        ImageView imageView = view.findViewById(R.id.imageView);
        if (location == null)
            imageView.setImageDrawable(null);
//            imageView.setImageResource(R.drawable.ic_add_black_24dp);

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
                View view = inflateClusterMapItem(finalLocation.x, finalLocation.y);
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

    class RefreshActionBar extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateActionBar();
                }
            });
        }
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
