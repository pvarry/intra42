package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.api.cluster_map.Location;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.ui.BasicEditActivity;
import com.paulvarry.intra42.utils.Tools;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class ClusterMapContributeEditActivity extends BasicEditActivity implements View.OnClickListener, ValueEventListener {

    private static final String INTENT_CLUSTER_SLUG = "cluster_slug";
    float baseItemWidth;
    float baseItemHeight;
    private String clusterSlug;
    private Cluster cluster;

    private boolean createCluster = false;
    private GridLayout gridLayout;

    private LayoutInflater vi;
    private int paddingItem2dp;

    public static void openIt(Context context, Cluster cluster) {
        Intent intent = new Intent(context, ClusterMapContributeEditActivity.class);

        if (cluster != null) {
            intent.putExtra(INTENT_CLUSTER_SLUG, cluster.slug);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_map_contribute_edit);
        setActionBarToggle(ActionBarToggle.CROSS);

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_CLUSTER_SLUG)) {
            clusterSlug = intent.getStringExtra(INTENT_CLUSTER_SLUG);
        }

        gridLayout = findViewById(R.id.gridLayout);

        DataWrapper dataWrapper = (DataWrapper) getLastCustomNonConfigurationInstance();
        if (dataWrapper != null) {
            cluster = dataWrapper.cluster;
        }

        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnFab();
            }
        });

        paddingItem2dp = Tools.dpToPxInt(this, 2);
        vi = LayoutInflater.from(this);

        onCreateFinished();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.firebaseRefClusterMapContribute != null) {
            setViewState(StatusCode.LOADING);
            app.firebaseRefClusterMapContribute.child(clusterSlug).addValueEventListener(this);
        } else {
            setViewState(StatusCode.API_DATA_ERROR);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (app.firebaseRefClusterMapContribute != null) {
            app.firebaseRefClusterMapContribute.removeEventListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);
        if (menuItemSave != null)
            menuItemSave.setVisible(false);
        return b;
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
        else if (cluster != null)
            return cluster.name;
        else
            return null;
    }

    @Override
    protected void setViewContent() {
        // set base item size
        baseItemHeight = Tools.dpToPxInt(this, 42);
        baseItemWidth = Tools.dpToPxInt(this, 35);
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
        return false;
    }

    @Override
    protected void onSave(final Callback callBack) {

    }

    @Override
    protected void onDelete(Callback callBack) {

    }

    void save() {
        app.firebaseRefClusterMapContribute.child(clusterSlug).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Cluster clusterMutable = mutableData.getValue(Cluster.class);
                if (clusterMutable == null) {
                    return Transaction.success(mutableData);
                }

                clusterMutable.width = cluster.width;
                clusterMutable.height = cluster.height;
                clusterMutable.map = cluster.map;

                Map<String, Object> export = clusterMutable.toMap();
                mutableData.setValue(export);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                if (databaseError != null)
                    Crashlytics.log(0, ClusterMapActivity.class.getName(), "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    void refreshMap() {
        save();
        buildMap();
    }

    void buildMap() {

        long start = System.nanoTime();

        if (cluster == null)
            return;

        gridLayout.removeAllViews();
        gridLayout.removeAllViewsInLayout();
        gridLayout.setRowCount(cluster.height + 1);
        gridLayout.setColumnCount(cluster.width + 1);

        for (int x = 0; x < cluster.width; x++) {

            for (int y = 0; y < cluster.height; y++) {

                View view = inflateClusterMapItem(x, y);
                gridLayout.addView(view);
            }

            View view = inflateClusterMapController(x, cluster.height);
            gridLayout.addView(view);
        }

        for (int y = 0; y < cluster.height + 1; y++) {
            View view = inflateClusterMapController(cluster.width, y);
            gridLayout.addView(view);
        }

        long end = System.nanoTime();
        long duration = end - start;
        Log.d("map building took", String.valueOf(duration) + " nano seconds ; " + String.valueOf(duration / 1000000000.0d) + " seconds");
    }

    private View inflateClusterMapItem(int x, int y) {
        @Nullable
        Location location = cluster.map.get(x, y);

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
                    if (cluster.hostPrefix != null && location.host.startsWith(cluster.hostPrefix)) // set warning flag because host contain cluster prefix
                        view.setBackgroundColor(getResources().getColor(R.color.color_warning));
                    else
                        imageViewContent.setColorFilter(ContextCompat.getColor(this, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
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

        if (x == cluster.width && y == cluster.height) {
            isCorner = true;
        } else if (x == cluster.width)
            location = cluster.map.get(0, y);
        else if (y == cluster.height)
            location = cluster.map.get(x, 0);

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
                else if (cluster.hostPrefix != null && s.toString().startsWith(cluster.hostPrefix))
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
                Location locationEdit = cluster.map.require(finalLocation.x, finalLocation.y, cluster);
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
                save();
            }
        });
        alert.setNegativeButton(R.string.discard_changes, null);
        alert.show();
    }

    public void onClickController(final LocationWrapper wrapper) {
        boolean isRow = false;

        if (wrapper.x == cluster.width && wrapper.y == cluster.height)
            return;
        else if (wrapper.x == cluster.width)
            isRow = true;
        else if (wrapper.y == cluster.height)
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
                if (which == 0) { // add row top
                    cluster.height++;
                    for (int i = 0; i < cluster.width; i++) {
                        SparseArray<Location> row = cluster.map.get(i);
                        if (row != null) {
                            for (int j = cluster.height; j > 0; j--) {
                                row.put(j, row.get(j - 1));
                            }
                            row.remove(0);
                        }
                    }
                } else if (which == 1) // add row bottom
                    cluster.height++;
                else if (which == 2) { // add column left
                    cluster.width++;
                    for (int i = cluster.width; i > 0; i--) {
                        cluster.map.put(i, cluster.map.get(i - 1));
                    }
                    cluster.map.remove(0);
                } else if (which == 3) // add column right
                    cluster.width++;
                else
                    return;
                refreshMap();
            }
        });
        builder.show();
    }

    private void setRowScale(int y, float scale) {
        for (int i = 0; i < cluster.map.size(); i++) {
            Location cel = cluster.map.require(i, y, cluster);
            cel.sizeY = scale;
        }
    }

    private void setColumnScale(int x, float scale) {
        SparseArray<Location> col = cluster.map.require(x, cluster);
        for (int i = 0; i < cluster.height; i++) {
            Location cel = col.get(i);
            if (cel == null) {
                cel = new Location();
                col.append(i, cel);
            }
            cel.sizeX = scale;
        }
    }

    private void deleteColumn(int x) {
        if (cluster.map.size() == 0)
            return;
        for (int i = x; i < cluster.map.size() - 1; i++) {
            cluster.map.setValueAt(i, cluster.map.valueAt(i + 1));
        }
        cluster.map.removeAt(cluster.map.size() - 1);
        cluster.width--;
    }

    private void deleteRow(int y) {
        for (int i = 0; i < cluster.map.size(); i++) {
            SparseArray<Location> col;
            if ((col = cluster.map.get(i)) != null) {
                for (int j = y; j < col.size() - 1; j++) {
                    col.setValueAt(j, col.valueAt(j + 1));
                }
                col.removeAt(col.size() - 1);
            }
        }
        cluster.height--;
    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        DataWrapper d = new DataWrapper();
        d.cluster = cluster;
        return d;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        cluster = dataSnapshot.getValue(Cluster.class);

        if (cluster != null && (cluster.map == null || cluster.map.size() == 0)) {
            cluster.width = 10;
            cluster.height = 10;
        }
        refresh();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(app, R.string.error, Toast.LENGTH_SHORT).show();
        Crashlytics.log(0, ClusterMapActivity.class.getName(), "onCancelled:" + databaseError);
    }

    private class DialogFinalWrapper {
        AlertDialog dialog;
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
    }
}
