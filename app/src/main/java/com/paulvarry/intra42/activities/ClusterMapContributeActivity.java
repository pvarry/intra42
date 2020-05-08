package com.paulvarry.intra42.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.BaseRecyclerAdapterItem;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.ui.BasicActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterMapContributeActivity
        extends BasicActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, ValueEventListener, BaseRecyclerAdapterItem.OnItemClickListener<Cluster> {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private List<Campus> listCampus;
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

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        new Thread(new Runnable() {
            @Override
            public void run() {
                listCampus = CacheCampus.getAllowInternet(app.cacheSQLiteHelper, app);
            }
        }).start();

        super.onCreateFinished();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupFirebaseListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeFirebaseListener();
    }

    void setupFirebaseListener() {
        if (app.firebaseRefClusterMapContribute != null) {
            setViewState(StatusCode.LOADING);
            app.firebaseRefClusterMapContribute.addValueEventListener(this);
        } else {
            setViewState(StatusCode.API_DATA_ERROR);
        }
    }

    void removeFirebaseListener() {
        if (app.firebaseRefClusterMapContribute != null) {
            app.firebaseRefClusterMapContribute.removeEventListener(this);
        }
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

    @SuppressLint("RestrictedApi")
    @Override
    protected void setViewContent() {

        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(this);

        if (clusters == null || clusters.isEmpty()) {
            setViewState(StatusCode.EMPTY);
            return;
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);

        BaseRecyclerAdapterItem<Cluster> adapter = new BaseRecyclerAdapterItem<>(clusters);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == fabBaseActivity) {
            openEditMetadataDialog(null);
        }
    }

    void openEditMetadataDialog(@Nullable final Cluster cluster) {
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
            listCampusString.add("[none]");
            for (int i = 0; i < listCampus.size(); i++) {
                c = listCampus.get(i);
                listCampusString.add(c.name + " (id: " + c.id + ")");
                if (cluster != null && c.id == cluster.campusId)
                    selection = i + 1;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(ClusterMapContributeActivity.this, android.R.layout.simple_spinner_dropdown_item, listCampusString);
            spinnerCampus.setAdapter(adapter);
        }

        editTextName.addTextChangedListener(textWatcher);
        editTextNameShort.addTextChangedListener(textWatcher);

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ClusterMapContributeActivity.this);
        if (cluster != null) {
            editTextPrefix.setText(cluster.hostPrefix);
            editTextNameShort.setText(cluster.nameShort);
            editTextName.setText(cluster.name);
            if (cluster.position > 0)
                editTextPosition.setText(String.valueOf(cluster.position));
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
                    newCluster.position = Integer.decode(stringPosition);
                newCluster.hostPrefix = editTextPrefix.getText().toString();
                newCluster.comment = editTextComment.getText().toString();
                newCluster.isReadyToPublish = checkboxReadyToPublish.isChecked();

                newCluster.campusId = 0;
                int pos = spinnerCampus.getSelectedItemPosition() - 1;
                if (listCampus != null && pos != -1 && pos < listCampus.size()) {
                    Campus c = listCampus.get(pos);
                    newCluster.campusId = c.id;
                }

                if (newCluster.slug == null)
                    newCluster.slug = newCluster.campusId + "_" + newCluster.hostPrefix + "_" + System.currentTimeMillis();
                DatabaseReference newData = app.firebaseRefClusterMapContribute.child(newCluster.slug);
                if (isCreate) {
                    newData.setValue(newCluster);
                    onRefresh();
                } else {
                    final Cluster finalNewCluster = newCluster;
                    newData.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            Cluster clusterMutable = mutableData.getValue(Cluster.class);
                            if (clusterMutable == null) {
                                return Transaction.success(mutableData);
                            }

                            clusterMutable.name = finalNewCluster.name;
                            clusterMutable.nameShort = finalNewCluster.nameShort;
                            clusterMutable.position = finalNewCluster.position;
                            clusterMutable.hostPrefix = finalNewCluster.hostPrefix;
                            clusterMutable.comment = finalNewCluster.comment;
                            clusterMutable.isReadyToPublish = finalNewCluster.isReadyToPublish;
                            clusterMutable.campusId = finalNewCluster.campusId;


                            Map<String, Object> export = clusterMutable.toMap();
                            mutableData.setValue(export);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                               DataSnapshot dataSnapshot) {
                            // Transaction completed
                            onRefresh();
                            if (databaseError != null) {
                                Log.d("ClusterMapContribute", "postTransaction:onComplete:" + databaseError);
                                FirebaseCrashlytics.getInstance().log("postTransaction:onComplete:" + databaseError);
                            }
                        }
                    });
                }
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
        removeFirebaseListener();
        setupFirebaseListener();
    }

    /**
     * Callback method to be invoked when an item in this Recycler has
     * been clicked.
     */
    @Override
    public void onItemClicked(final int position, Cluster item) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        String[] str = new String[]{
                getString(R.string.cluster_map_contribute_button_edit_metadata),
                getString(R.string.cluster_map_contribute_button_edit_layout)
        };
        builder.setItems(str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    openEditMetadataDialog(clusters.get(position));
                else if (which == 1)
                    ClusterMapContributeEditActivity.openIt(ClusterMapContributeActivity.this, clusters.get(position));
            }
        });
        builder.setTitle(clusters.get(position).name);
        builder.show();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        GenericTypeIndicator<HashMap<String, Cluster>> t = new GenericTypeIndicator<HashMap<String, Cluster>>() {
        };
        HashMap<String, Cluster> data = dataSnapshot.getValue(t);

        if (data != null) {
            clusters = new ArrayList<>(data.values());
            Collections.sort(clusters);
        }

        refresh();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        setViewState(StatusCode.API_DATA_ERROR);
    }

    private class FinalWrapper {
        AlertDialog dialog;
    }
}
