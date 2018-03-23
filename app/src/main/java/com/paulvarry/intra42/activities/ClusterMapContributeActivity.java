package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterClusterMapContribute;
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

import retrofit2.Call;
import retrofit2.Response;

public class ClusterMapContributeActivity
        extends BasicThreadActivity
        implements View.OnClickListener, BasicThreadActivity.GetDataOnThread, ListAdapterClusterMapContribute.OnEditClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView listView;
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
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ?
                        0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled((topRowVerticalPosition >= 0));
            }
        });

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

        ListAdapterClusterMapContribute adapter = new ListAdapterClusterMapContribute(app, masters);
        adapter.setOnEditListener(this);
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

    @Override
    public void onClickEditLayout(View finalConvertView, int groupPosition, final Master master) {

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

    @Override
    public void onClickEditMetadata(View finalConvertView, int groupPosition, final Master master) {

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

    private class FinalWrapper {
        AlertDialog dialog;
    }
}
