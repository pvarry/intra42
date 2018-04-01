package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.paulvarry.intra42.utils.ClusterMapContributeUtils;
import com.paulvarry.intra42.utils.Tools;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private boolean isTimeUpDialogDisplayed;
    private Timer timerRefreshActionBar;

    private MapStore allLocations;

    private LayoutInflater vi;
    private int paddingItem2dp;

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
                cal.add(Calendar.MINUTE, ClusterMapContributeUtils.MINUTE_LOCK);
                lockEnd = cal.getTime();

                updateActionBar();
            }
        }

        gridLayout = findViewById(R.id.gridLayout);

        DataWrapper dataWrapper = (DataWrapper) getLastCustomNonConfigurationInstance();
        if (dataWrapper != null) {
            isTimeUpDialogDisplayed = dataWrapper.isTimeUpDialogDisplayed;
            allLocations = dataWrapper.allLocations;
            cluster = dataWrapper.cluster;
        } else {
            allLocations = new MapStore();

            if (cluster.map != null) {
                int sizeX = cluster.map.length;
                int sizeY = 0;
                for (int x = 0; x < cluster.map.length; x++) {

                    if (cluster.map[x] != null) {
                        SparseArray<Location> col = allLocations.require(x);
                        if (cluster.map[x].length > sizeY)
                            sizeY = cluster.map[x].length;

                        for (int y = 0; y < cluster.map[x].length; y++) {
                            col.append(y, cluster.map[x][y]);
                        }
                    }
                }

                cluster.sizeX = sizeX;
                cluster.sizeY = sizeY;
            } else {
                cluster.sizeX = 10;
                cluster.sizeY = 10;
            }
        }

        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnFab();
            }
        });

        paddingItem2dp = Tools.dpToPx(this, 2);
        vi = LayoutInflater.from(this);

        onCreateFinished();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);
        if (menuItemDelete != null)
            menuItemDelete.setVisible(false);
        return b;
    }

    @Override
    public void onBackPressed() {
        long duration = lockEnd.getTime() - new Date().getTime();
        if (duration < 0) {
            ClusterMapContributeEditActivity.super.onBackPressed();
            return;
        }
        ClusterMapContributeUtils.unlockCluster(this, app, master, true, new ClusterMapContributeUtils.NoReturnCallback() {
            @Override
            public void finish() {
                ClusterMapContributeEditActivity.super.onBackPressed();
            }

            @Override
            public void error(String error) {
                ClusterMapContributeEditActivity.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        timerRefreshActionBar = new Timer();
        timerRefreshActionBar.schedule(new RefreshActionBar(), new Date(), 500);
    }

    @Override
    protected void onStop() {
        super.onStop();

        timerRefreshActionBar.cancel();
        timerRefreshActionBar.purge();
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
        // set base item size
        baseItemHeight = Tools.dpToPx(this, 42);
        baseItemWidth = Tools.dpToPx(this, 35);
        buildMap();
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
    protected void onSave(final Callback callBack) {

        Location[][] tmp = new Location[cluster.sizeX][];
        for (int i = 0; i < cluster.sizeX; i++) {
            SparseArray<Location> col = allLocations.get(i);
            tmp[i] = new Location[cluster.sizeY];

            if (col != null)
                for (int j = 0; j < cluster.sizeY; j++) {
                    Location cell = col.get(j);

                    tmp[i][j] = cell;
                }
        }
        cluster.map = tmp;

        ClusterMapContributeUtils.saveClusterMap(this, app, master, cluster, new ClusterMapContributeUtils.CreateSaveClusterMapCallback() {
            @Override
            public void finish(List<Master> masters) {
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

        if (!isTimeUpDialogDisplayed && duration < 30 * 1000 && duration > 0) { // 10 seconds
            isTimeUpDialogDisplayed = true;

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(R.string.cluster_map_contribute_dialog_time_up_title);
            b.setMessage(R.string.cluster_map_contribute_dialog_time_up_message);
            b.setPositiveButton(R.string.ok, null);
            b.show();
        }
        if (duration < 0) {
            toolbar.setSubtitle(R.string.cluster_map_contribute_toolbar_info_too_late);
            super.menuItemSave.setVisible(false);
            return;
        }
        calendar.setTimeInMillis(duration);
        int m = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, -m);
        int s = calendar.get(Calendar.SECOND);

        if (m != 0)
            toolbar.setSubtitle(
                    getString(R.string.cluster_map_contribute_toolbar_info_minute_second)
                            .replace("_minute_", String.valueOf(m))
                            .replace("_second_", String.valueOf(s)));
        else
            toolbar.setSubtitle(
                    getString(R.string.cluster_map_contribute_toolbar_info_second)
                            .replace("_second_", String.valueOf(s)));
    }

    void refreshMap() {
        unsavedData = true;
        buildMap();
    }

    void buildMap() {

        long start = System.nanoTime();

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

        long end = System.nanoTime();
        long duration = end - start;
        Log.d("map building took", String.valueOf(duration) + " nano seconds ; " + String.valueOf(duration / 1000000000.0d) + " seconds");
    }

    private View inflateClusterMapItem(int x, int y) {
        @Nullable
        Location location = allLocations.get(x, y);

        View view;
        ImageView imageViewContent;
        TextView textView;
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
            if (location.kind == Location.Kind.USER) {
                if (location.host == null ||
                        location.host.isEmpty() ||
                        location.host.contentEquals("null") ||
                        location.host.contentEquals("TBD")) {
                    imageViewContent.setImageResource(R.drawable.ic_missing_black_25dp);
                    view.setBackgroundColor(getResources().getColor(R.color.color_warning));
                } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(location.host);
                    imageViewContent.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_desktop_mac_black_custom));
                    if (location.host.startsWith(cluster.hostPrefix)) // set warning flag because host contain cluster prefix
                        view.setBackgroundColor(getResources().getColor(R.color.color_warning));
                }

            } else if (location.kind == Location.Kind.WALL)
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
        imageViewContent.setPadding(paddingItem2dp, paddingItem2dp, paddingItem2dp, paddingItem2dp);
        view.setLayoutParams(paramsGridLayout);

        return view;
    }

    private View inflateClusterMapController(int x, int y) {
        GridLayout.LayoutParams paramsGridLayout;
        Location location = null;
        boolean isCorner = false;

        View view = vi.inflate(R.layout.grid_layout_cluster_map_edit_controller, gridLayout, false);

        if (x == cluster.sizeX && y == cluster.sizeY) {
            location = null;
            isCorner = true;
        } else if (x == cluster.sizeX)
            location = allLocations.get(0, y);
        else if (y == cluster.sizeY)
            location = allLocations.get(x, 0);

        float sizeX = 1;
        float sizeY = 1;
        if (!isCorner) {
            if (location != null) {
                sizeX = location.sizeX;
                sizeY = location.sizeY;
            }

            final LocationWrapper wrapper = new LocationWrapper(x, y, location);
            view.setTag(wrapper);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickController(wrapper);
                }
            });
        }

        paramsGridLayout = (GridLayout.LayoutParams) view.getLayoutParams();
        paramsGridLayout.columnSpec = GridLayout.spec(x);
        paramsGridLayout.rowSpec = GridLayout.spec(y);
        paramsGridLayout.setGravity(Gravity.FILL);
        paramsGridLayout.height = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsGridLayout.width = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsGridLayout.height = (int) (baseItemHeight * sizeY);
        paramsGridLayout.width = (int) (baseItemWidth * sizeX);

        ImageView imageView = view.findViewById(R.id.imageView);
        if (isCorner)
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
        final View view = inflater.inflate(R.layout.fragment_dialog_cluster_map_contribute_location, null);
        final EditText editText = view.findViewById(R.id.editTextHost);
        final TextInputLayout textInputLayoutHost = view.findViewById(R.id.textInputLayoutHost);
        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        final RadioButton radioButtonUser = view.findViewById(R.id.radioButtonUser);
        final RadioButton radioButtonCorridor = view.findViewById(R.id.radioButtonCorridor);
        final RadioButton radioButtonWall = view.findViewById(R.id.radioButtonWall);
//        Spinner spinnerLocationKind = view.findViewById(R.id.spinnerLocationKind);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        textInputLayoutHost.setVisibility(View.GONE);

        String title;
        if (location.location != null && location.location.host != null)
            title = getString(R.string.cluster_map_contribute_dialog_location_title_host)
                    .replace("_x_", String.valueOf(location.x))
                    .replace("_y_", String.valueOf(location.y))
                    .replace("_host_", location.location.host);
        else
            title = getString(R.string.cluster_map_contribute_dialog_location_title)
                    .replace("_x_", String.valueOf(location.x))
                    .replace("_y_", String.valueOf(location.y));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutHost.setErrorEnabled(true);
                if (s.length() == 0)
                    textInputLayoutHost.setError(getString(R.string.cluster_map_contribute_warning_empty_host));
                else if (s.toString().startsWith(cluster.hostPrefix))
                    textInputLayoutHost.setError(getString(R.string.cluster_map_contribute_warning_host_prefix));
                else
                    textInputLayoutHost.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (location.location != null) {
            if (location.location.host != null && !location.location.host.contentEquals("TBD") && !location.location.host.contentEquals("null"))
                editText.setText(location.location.host);
            else
                editText.setText("");
            if (location.location.kind == Location.Kind.USER) {
                radioButtonUser.setChecked(true);
                textInputLayoutHost.setVisibility(View.VISIBLE);
            } else if (location.location.kind == Location.Kind.CORRIDOR)
                radioButtonCorridor.setChecked(true);
            else if (location.location.kind == Location.Kind.WALL)
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
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Location locationEdit = allLocations.require(finalLocation.x, finalLocation.y);
                locationEdit.host = null;
                if (radioButtonUser.isChecked()) {
                    locationEdit.kind = Location.Kind.USER;
                    locationEdit.host = editText.getText().toString();
                } else if (radioButtonCorridor.isChecked())
                    locationEdit.kind = Location.Kind.CORRIDOR;
                else if (radioButtonWall.isChecked())
                    locationEdit.kind = Location.Kind.WALL;
                else return;

                gridLayout.removeView(v);
                View view = inflateClusterMapItem(finalLocation.x, finalLocation.y);
                gridLayout.addView(view);
            }
        });
        alert.setNegativeButton(R.string.discard_changes, null);
        alert.show();
    }

    public void onClickController(final LocationWrapper wrapper) {
        boolean isRow = false;

        if (wrapper.x == cluster.sizeX && wrapper.y == cluster.sizeY)
            return;
        else if (wrapper.x == cluster.sizeX)
            isRow = true;
        else if (wrapper.y == cluster.sizeY)
            isRow = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] action;
        if (isRow) {
            builder.setTitle(R.string.cluster_map_contribute_dialog_controller_title_row);
            action = new String[]{getString(R.string.cluster_map_contribute_dialog_controller_action_scale_row),
                    getString(R.string.cluster_map_contribute_dialog_controller_action_delete_row)};
        } else {
            builder.setTitle(R.string.cluster_map_contribute_dialog_controller_action_column);
            action = new String[]{getString(R.string.cluster_map_contribute_dialog_controller_action_scale_column),
                    getString(R.string.cluster_map_contribute_dialog_controller_action_delete_column)};
        }

        final boolean finalIsRow = isRow;
        builder.setItems(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    onClickControllerScaleRowCol(finalIsRow, wrapper);
                else if (which == 1) {
                    onClickControllerDeleteRowCol(finalIsRow, wrapper);
                }
            }
        });
        builder.show();
    }

    public void onClickControllerScaleRowCol(final boolean finalIsRow, final LocationWrapper wrapper) {

        if (wrapper == null)
            return;

        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_dialog_cluster_map_contribute_scale, null);
        final EditText editTextScale = view.findViewById(R.id.editTextScale);
        final TextInputLayout textInputLayoutScale = view.findViewById(R.id.textInputLayoutScale);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        float baseValue = 1.0f;
        if (wrapper.location != null) {
            if (finalIsRow)
                baseValue = wrapper.location.sizeY;
            else
                baseValue = wrapper.location.sizeX;
        }

        editTextScale.setText(String.valueOf(baseValue));

        final DialogFinalWrapper dialog = new DialogFinalWrapper();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dialog.dialog != null) {
                    boolean disable = false;
                    String content = editTextScale.getText().toString();

                    textInputLayoutScale.setError(null);
                    if (!content.isEmpty()) {
                        float scale = 1.0f;
                        try {
                            scale = Float.valueOf(content);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (scale < 0.1) {
                            textInputLayoutScale.setError(getString(R.string.error_scale_is_too_small));
                            disable = true;
                        } else if (scale > 6) {
                            textInputLayoutScale.setError(getString(R.string.error_scale_is_too_big));
                            disable = true;
                        }
                    } else {
                        textInputLayoutScale.setError(getString(R.string.error_value_must_be_set));
                        disable = true;
                    }
                    dialog.dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!disable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextScale.addTextChangedListener(textWatcher);

        alert.setTitle(R.string.cluster_map_contribute_dialog_scale_title);
        alert.setView(view);
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = editTextScale.getText().toString();
                if (content.isEmpty())
                    return;
                float scale = 1.0f;
                try {
                    scale = Float.valueOf(content);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (finalIsRow)
                    setRowScale(wrapper.y, scale);
                else
                    setColumnScale(wrapper.x, scale);
                refreshMap();
            }
        });
        alert.setNegativeButton(R.string.discard_changes, null);

        dialog.dialog = alert.show();
    }

    public void onClickControllerDeleteRowCol(final boolean finalIsRow, final LocationWrapper wrapper) {

        if (wrapper == null)
            return;

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.cluster_map_contribute_dialog_delete_title);
        if (finalIsRow)
            alert.setMessage(app.getString(R.string.cluster_map_contribute_dialog_delete_message_row, wrapper.y));
        else
            alert.setMessage(app.getString(R.string.cluster_map_contribute_dialog_delete_message_col, wrapper.x));
        alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (finalIsRow)
                    deleteRow(wrapper.y);
                else
                    deleteColumn(wrapper.x);
                refreshMap();
            }
        });
        alert.setNegativeButton(R.string.cancel, null);
        alert.show();
    }

    private void clickOnFab() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClusterMapContributeEditActivity.this);
        String[] actions = new String[]{getString(R.string.cluster_map_contribute_dialog_add_action_row_top),
                getString(R.string.cluster_map_contribute_dialog_add_action_row_bottom),
                getString(R.string.cluster_map_contribute_dialog_add_action_column_left),
                getString(R.string.cluster_map_contribute_dialog_add_action_column_right)};
        builder.setItems(actions, new DialogInterface.OnClickListener() {
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
                } else if (which == 1)
                    cluster.sizeY++;
                else if (which == 2) {
                    cluster.sizeX++;
                    for (int i = cluster.sizeX; i > 0; i--) {
                        allLocations.put(i, allLocations.get(i - 1));
                    }
                    allLocations.remove(0);
                } else if (which == 3)
                    cluster.sizeX++;
                else
                    return;
                refreshMap();
            }
        });
        builder.show();
    }

    private void setRowScale(int y, float scale) {
        for (int i = 0; i < allLocations.size(); i++) {

            Location cel = allLocations.require(i, y);
            cel.sizeY = scale;
        }
    }

    private void setColumnScale(int x, float scale) {
        SparseArray<Location> col = allLocations.require(x);

        for (int i = 0; i < cluster.sizeY; i++) {
            Location cel = col.get(i);
            if (cel == null) {
                cel = new Location();
                col.append(i, cel);
            }
            cel.sizeX = scale;
        }

    }

    private void deleteColumn(int x) {
        for (int i = x; i < allLocations.size() - 1; i++) {
            allLocations.setValueAt(i, allLocations.valueAt(i + 1));
        }
        allLocations.removeAt(allLocations.size() - 1);
        cluster.sizeX--;
    }

    private void deleteRow(int y) {
        for (int i = 0; i < allLocations.size(); i++) {
            SparseArray<Location> col;
            if ((col = allLocations.get(i)) != null) {
                for (int j = y; j < col.size() - 1; j++) {
                    col.setValueAt(j, col.valueAt(j + 1));
                }
                col.removeAt(col.size() - 1);
            }
        }
        cluster.sizeY--;
    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        DataWrapper d = new DataWrapper();
        d.cluster = cluster;
        d.isTimeUpDialogDisplayed = isTimeUpDialogDisplayed;
        d.allLocations = allLocations;
        return d;
    }

    private class DialogFinalWrapper {
        AlertDialog dialog;
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

    private class DataWrapper {

        Cluster cluster;
        boolean isTimeUpDialogDisplayed;
        MapStore allLocations;
    }

    private class MapStore extends SparseArray<SparseArray<Location>> {

        @NonNull
        SparseArray<Location> require(int x) {
            if (x < 0)
                throw new ArrayIndexOutOfBoundsException("x= " + String.valueOf(x));

            SparseArray<Location> col = super.get(x);
            if (col == null) {
                col = new SparseArray<>(cluster.sizeY);
                super.append(x, col);
            }
            return col;
        }

        public Location get(int x, int y) {
            if (x < 0 || y < 0)
                return null;
            SparseArray<Location> col = super.get(x);
            if (col == null)
                return null;
            return col.get(y);
        }

        @NonNull
        Location require(int x, int y) {
            if (x < 0 || y < 0)
                throw new ArrayIndexOutOfBoundsException("x= " + String.valueOf(x) + " ; y= " + String.valueOf(y));

            SparseArray<Location> col = super.get(x);
            if (col == null) {
                col = new SparseArray<>(cluster.sizeY);
                super.append(x, col);
                col.append(y, new Location());
                return col.get(y);
            }

            Location cel = col.get(y);
            if (cel == null) {
                cel = new Location();
                col.append(y, cel);
            }
            return cel;
        }

        public void replace(int x, int y, Location item) {
            if (x < 0 || y < 0)
                throw new ArrayIndexOutOfBoundsException("x= " + String.valueOf(x) + " ; y= " + String.valueOf(y));

            SparseArray<Location> col = super.get(x);
            if (col == null) {
                col = new SparseArray<>(cluster.sizeY);
                super.append(x, col);
                col.append(y, item);
            } else
                col.append(y, item);
        }
    }
}
