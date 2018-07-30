package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.BaseListAdapterSummary;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.ClusterMapContributeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Response;

public class ClusterMapContributeActivity
        extends BasicThreadActivity
        implements View.OnClickListener, BasicThreadActivity.GetDataOnThread, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ImageView imageViewExpand;
    private TextView textViewExplanations;
    private ViewGroup linearLayoutExplanations;

    private List<Campus> listCampus;
    private List<Master> masters;
    private Master newMaster;

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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        imageViewExpand = findViewById(R.id.imageViewExpand);
        textViewExplanations = findViewById(R.id.textViewExplanations);
        linearLayoutExplanations = findViewById(R.id.linearLayoutExplanations);

        linearLayoutExplanations.setOnClickListener(this);

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

        if (masters == null || masters.isEmpty()) {
            setViewState(StatusCode.EMPTY);
            return;
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);

        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(this);

        BaseListAdapterSummary<Master> adapter = new BaseListAdapterSummary<>(app, masters);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == fabBaseActivity) {
            openEditMetadataDialog(null, null);
        } else if (v == linearLayoutExplanations) {
            if (textViewExplanations.getVisibility() == View.VISIBLE) {
                textViewExplanations.setVisibility(View.GONE);
                imageViewExpand.setImageResource(R.drawable.ic_expand_more_black_24dp);
            } else {
                textViewExplanations.setVisibility(View.VISIBLE);
                imageViewExpand.setImageResource(R.drawable.ic_expand_less_black_24dp);
            }
        }
    }

    /**
     * Triggered when the activity start.
     * <p>
     * This method is run on main Thread, so you can make api call.
     */
    @Override
    public void getDataOnOtherThread() throws RuntimeException {

        listCampus = CacheCampus.getAllowInternet(app.cacheSQLiteHelper, app);
        if (listCampus == null) {
            super.setViewStateThread(StatusCode.API_DATA_ERROR);
            return;
        }

        final Call<List<Master>> call = app.getApiServiceClusterMapContribute().getMasters();

        long start = System.nanoTime();
        try {
            Response<List<Master>> response = call.execute();
            long end = System.nanoTime();
            long diff = end - start;

            Log.d("cluster call time", String.valueOf(diff));

            List<Master> tmpMaster = response.body();
            masters = new ArrayList<>();
            for (Master m : tmpMaster) {

                if (newMaster == null && m.name == null)
                    newMaster = m;

                if (m.name != null)
                    masters.add(m);
            }
            Collections.sort(masters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickEditLayout(final Master master) {

        ClusterMapContributeUtils.loadClusterMapAndLock(this, this.app, master, new ClusterMapContributeUtils.LoadClusterMapCallback() {
            @Override
            public void finish(final Master master, final Cluster cluster) {
                Toast.makeText(app, R.string.opening_in_progress, Toast.LENGTH_SHORT).show();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ClusterMapContributeEditActivity.openIt(ClusterMapContributeActivity.this, cluster, master);
                    }
                });
            }

            @Override
            public void error(String error) {
                Toast.makeText(app, error, Toast.LENGTH_SHORT).show();
                refresh();
            }
        });
    }

    public void onClickEditMetadata(final Master master) {

        ClusterMapContributeUtils.loadClusterMap(ClusterMapContributeActivity.this, app, master, new ClusterMapContributeUtils.LoadClusterMapCallback() {
            @Override
            public void finish(final Master master, final Cluster cluster) {
                openEditMetadataDialog(master, cluster);
            }

            @Override
            public void error(String error) {
                Toast.makeText(app, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void openEditMetadataDialog(@Nullable final Master master, @Nullable final Cluster cluster) {
        final LayoutInflater inflater = LayoutInflater.from(ClusterMapContributeActivity.this);
        final View view = inflater.inflate(R.layout.fragment_dialog_cluster_map_contribute_cluster, null);
        final TextInputLayout textInputName = view.findViewById(R.id.textInputName);
        final TextInputLayout textInputNameShort = view.findViewById(R.id.textInputNameShort);
        final EditText editTextPrefix = view.findViewById(R.id.editTextPrefix);
        final EditText editTextName = view.findViewById(R.id.editTextName);
        final EditText editTextNameShort = view.findViewById(R.id.editTextNameShort);
        final EditText editTextPosition = view.findViewById(R.id.editTextPosition);
        final Spinner spinnerCampus = view.findViewById(R.id.spinnerCampus);
        final CheckBox checkboxReadyToPublish = view.findViewById(R.id.checkboxReadyToPublish);
        final EditText editTextComment = view.findViewById(R.id.editTextComment);

        final boolean isCreate = cluster == null;

        final FinalWrapper dialog = new FinalWrapper();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dialog.dialog != null) {
                    boolean disable = false;
                    if (editTextName.getText().toString().length() > textInputName.getCounterMaxLength()) {
                        textInputName.setError(getString(R.string.error_text_is_too_long));
                        disable = true;
                    } else
                        textInputName.setError(null);
                    if (editTextNameShort.getText().toString().length() > textInputNameShort.getCounterMaxLength()) {
                        textInputNameShort.setError(getString(R.string.error_text_is_too_long));
                        disable = true;
                    } else
                        textInputNameShort.setError(null);
                    dialog.dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!disable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        int selection = 0;
        if (listCampus != null) {
            List<String> listCampusString;
            listCampusString = new ArrayList<>();
            Campus c;
            for (int i = 0; i < listCampus.size(); i++) {
                c = listCampus.get(i);
                listCampusString.add(c.name + " (id: " + String.valueOf(c.id) + ")");
                if (cluster != null && c.id == cluster.campusId)
                    selection = i;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(ClusterMapContributeActivity.this, android.R.layout.simple_spinner_dropdown_item, listCampusString);
            spinnerCampus.setAdapter(adapter);
        }

        editTextName.addTextChangedListener(textWatcher);
        editTextNameShort.addTextChangedListener(textWatcher);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ClusterMapContributeActivity.this);
        if (cluster != null) {
            editTextPrefix.setText(cluster.hostPrefix);
            editTextNameShort.setText(cluster.nameShort);
            editTextName.setText(cluster.name);
            if (cluster.clusterPosition > 0)
                editTextPosition.setText(String.valueOf(cluster.clusterPosition));
            editTextComment.setText(cluster.comment);
            checkboxReadyToPublish.setChecked(cluster.isReadyToPublish);
        }
        spinnerCampus.setSelection(selection, false);

        builder.setTitle(R.string.cluster_map_contribute_dialog_metadata_title);
        builder.setView(view);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Cluster newCluster = cluster;
                if (newCluster == null)
                    newCluster = new Cluster(0, null, null);

                newCluster.name = editTextName.getText().toString();
                newCluster.nameShort = editTextNameShort.getText().toString();
                String stringPosition = editTextPosition.getText().toString();
                if (!stringPosition.isEmpty())
                    newCluster.clusterPosition = Integer.decode(stringPosition);
                newCluster.hostPrefix = editTextPrefix.getText().toString();
                newCluster.comment = editTextComment.getText().toString();
                newCluster.isReadyToPublish = checkboxReadyToPublish.isChecked();

                newCluster.campusId = 1;
                int pos = spinnerCampus.getSelectedItemPosition();
                if (listCampus != null && pos < listCampus.size()) {
                    Campus c = listCampus.get(pos);
                    newCluster.campusId = c.id;
                }

                ClusterMapContributeUtils.CreateSaveClusterMapCallback callback = new ClusterMapContributeUtils.CreateSaveClusterMapCallback() {
                    @Override
                    public void finish(List<Master> masters) {
                        refresh();
                    }

                    @Override
                    public void error(String error) {
                        Toast.makeText(app, error, Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                };

                if (isCreate)
                    ClusterMapContributeUtils.createCluster(app, ClusterMapContributeActivity.this, newCluster, callback);
                else
                    ClusterMapContributeUtils.saveClusterMapMetadata(ClusterMapContributeActivity.this, app, master, newCluster, callback);
            }
        });
        dialog.dialog = builder.show();
        //verify if text is too long
        if (dialog.dialog != null) {
            boolean disable = false;
            if (editTextName.getText().toString().length() > textInputName.getCounterMaxLength()) {
                textInputName.setError(getString(R.string.error_text_is_too_long));
                disable = true;
            }
            if (editTextNameShort.getText().toString().length() > textInputNameShort.getCounterMaxLength()) {
                textInputNameShort.setError(getString(R.string.error_text_is_too_long));
                disable = true;
            }
            dialog.dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!disable);
        }
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        refresh();
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] str = new String[]{
                getString(R.string.cluster_map_contribute_button_edit_metadata),
                getString(R.string.cluster_map_contribute_button_edit_layout)
        };
        builder.setItems(str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    onClickEditMetadata(masters.get(position));
                else if (which == 1)
                    onClickEditLayout(masters.get(position));
            }
        });
        builder.setTitle(masters.get(position).name);
        builder.show();
    }

    private class FinalWrapper {
        AlertDialog dialog;
    }
}
