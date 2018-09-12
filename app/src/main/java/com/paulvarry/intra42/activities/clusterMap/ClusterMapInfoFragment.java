package com.paulvarry.intra42.activities.clusterMap;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.ClusterMapContributeActivity;
import com.paulvarry.intra42.adapters.RecyclerAdapterClusterMapInfo;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.cluster_map.Cluster;
import com.paulvarry.intra42.api.model.Cursus;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.cache.CacheCursus;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.clusterMap.ClusterLayersSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.apptik.widget.MultiSlider;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClusterMapInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClusterMapInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterMapInfoFragment
        extends Fragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, RecyclerAdapterClusterMapInfo.OnItemClickListener {

    private ClusterMapActivity activity;

    private RecyclerAdapterClusterMapInfo adapter;
    private ArrayAdapter<String> adapterSpinnerSecondaryProject;
    private ArrayAdapter<String> adapterSpinnerSecondaryLevels;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewClusters;
    private TextView textViewLayerTitle;
    private TextView textViewLayerDescription;
    private ViewGroup layoutLayerContent;
    private Spinner spinnerMain;
    private Spinner spinnerSecondary;
    private RecyclerView recyclerView;
    private EditText editText;
    private Button buttonUpdate;
    private ViewGroup layoutLoading;
    private ProgressBar progressBar;
    private TextView textViewContributeTitle;
    private TextView textViewContributeDescription;
    private TextView textViewNoClusterMap;
    private Button buttonContribute;
    private CardView cardViewApiError;
    private ViewGroup layoutDisabledLayer;
    private TextView textViewWarningDisabledLayer;
    private ViewGroup layoutLevel;
    private EditText editTextLevelMin;
    private EditText editTextLevelMax;
    private MultiSlider multiSliderLevels;
    private CheckBox checkboxLevels;

    private OnFragmentInteractionListener mListener;

    private TextWatcher textWatcherMain = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (activity.layerSettingsInProgress.layer) {
                case USER:
                    activity.layerSettingsInProgress.layerUserLogin = String.valueOf(s);
                    break;
                case PROJECT:
                    activity.layerSettingsInProgress.layerProjectSlug = String.valueOf(s);
                    break;
                case LOCATION:
                    activity.layerSettingsInProgress.layerLocationPost = String.valueOf(s);
            }
            updateButton();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher textWatcherLevelMin = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            multiSliderLevels.setOnThumbValueChangeListener(null);
            try {
                activity.layerSettingsInProgress.layerLevelMin = Float.parseFloat(s.toString());
                multiSliderLevels.getThumb(0).setValue(Math.round(activity.layerSettingsInProgress.layerLevelMin));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            multiSliderLevels.setOnThumbValueChangeListener(onThumbValueChangeListener);
            updateButton();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private MultiSlider.OnThumbValueChangeListener onThumbValueChangeListener = new MultiSlider.OnThumbValueChangeListener() {
        @Override
        public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
            if (thumbIndex == 0) {
                activity.layerSettingsInProgress.layerLevelMin = value;
                editTextLevelMin.removeTextChangedListener(textWatcherLevelMin);
                editTextLevelMin.setText(String.valueOf(value));
                editTextLevelMin.addTextChangedListener(textWatcherLevelMin);
            } else {
                editTextLevelMax.removeTextChangedListener(textWatcherLevelMax);

                if (value == 22) {
                    activity.layerSettingsInProgress.layerLevelMax = -1f;
                    editTextLevelMax.setText("21+");
                } else {
                    activity.layerSettingsInProgress.layerLevelMax = value;
                    editTextLevelMax.setText(String.valueOf(value));
                }

                editTextLevelMax.addTextChangedListener(textWatcherLevelMax);
            }
            updateButton();
        }
    };
    private TextWatcher textWatcherLevelMax = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            multiSliderLevels.setOnThumbValueChangeListener(null);
            if (s.toString().contentEquals("21+")) {
                activity.layerSettingsInProgress.layerLevelMax = -1;
                multiSliderLevels.getThumb(1).setValue(22);
                multiSliderLevels.setOnThumbValueChangeListener(onThumbValueChangeListener);
            } else {
                try {
                    activity.layerSettingsInProgress.layerLevelMax = Float.parseFloat(s.toString());
                    multiSliderLevels.getThumb(1).setValue(Math.round(activity.layerSettingsInProgress.layerLevelMax));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } finally {
                    multiSliderLevels.setOnThumbValueChangeListener(onThumbValueChangeListener);
                }
            }
            updateButton();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private CompoundButton.OnCheckedChangeListener checkedChangeListenerLevel = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            activity.layerSettingsInProgress.layerLevelShowClosedCursusUser = isChecked;
            updateButton();
        }
    };

    public ClusterMapInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClusterMapFragment.
     */
    public static ClusterMapInfoFragment newInstance() {
        return new ClusterMapInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ClusterMapActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cluster_map_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.layoutParent);
        recyclerView = view.findViewById(R.id.recyclerView);
        spinnerMain = view.findViewById(R.id.spinnerMain);
        spinnerSecondary = view.findViewById(R.id.spinnerSecondary);
        textViewClusters = view.findViewById(R.id.textViewClusters);
        textViewLayerTitle = view.findViewById(R.id.textViewLayerTitle);
        textViewLayerDescription = view.findViewById(R.id.textViewLayerDescription);
        layoutLayerContent = view.findViewById(R.id.layoutLayerContent);
        editText = view.findViewById(R.id.editText);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        progressBar = view.findViewById(R.id.progressBar);
        textViewContributeTitle = view.findViewById(R.id.textViewContributeTitle);
        textViewContributeDescription = view.findViewById(R.id.textViewContributeDescription);
        textViewNoClusterMap = view.findViewById(R.id.textViewNoClusterMap);
        buttonContribute = view.findViewById(R.id.buttonContribute);
        cardViewApiError = view.findViewById(R.id.cardViewApiError);
        layoutDisabledLayer = view.findViewById(R.id.layoutDisabledLayer);
        textViewWarningDisabledLayer = view.findViewById(R.id.textViewWarningDisabledLayer);
        layoutLevel = view.findViewById(R.id.layoutLevel);
        editTextLevelMin = view.findViewById(R.id.editTextLevelMin);
        editTextLevelMax = view.findViewById(R.id.editTextLevelMax);
        multiSliderLevels = view.findViewById(R.id.multiSliderLevels);
        checkboxLevels = view.findViewById(R.id.checkbox);

        buttonContribute.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textViewClusters.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        textViewLayerTitle.setVisibility(View.VISIBLE);
        textViewLayerDescription.setVisibility(View.VISIBLE);
        layoutLayerContent.setVisibility(View.VISIBLE);
        textViewNoClusterMap.setVisibility(View.GONE);
        cardViewApiError.setVisibility(View.GONE);
        layoutDisabledLayer.setVisibility(View.GONE);
        layoutLevel.setVisibility(View.GONE);

        if (activity.clusterData.clusters == null || activity.clusterData.clusters.size() == 0) {
            textViewNoClusterMap.setVisibility(View.VISIBLE);
            textViewClusters.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            textViewLayerTitle.setVisibility(View.GONE);
            textViewLayerDescription.setVisibility(View.GONE);
            layoutLayerContent.setVisibility(View.GONE);
        } else {
            textViewNoClusterMap.setVisibility(View.GONE);
            adapter = new RecyclerAdapterClusterMapInfo(getContext(), activity.clusterData);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setNestedScrollingEnabled(false);
        }
        swipeRefreshLayout.setOnRefreshListener(this);

        editText.addTextChangedListener(textWatcherMain);
        editTextLevelMin.addTextChangedListener(textWatcherLevelMin);
        editTextLevelMax.addTextChangedListener(textWatcherLevelMax);
        buttonUpdate.setOnClickListener(this);
        multiSliderLevels.setOnThumbValueChangeListener(onThumbValueChangeListener);
        checkboxLevels.setOnCheckedChangeListener(checkedChangeListenerLevel);

        multiSliderLevels.setMax(22);
        multiSliderLevels.setMin(0);

        adapterSpinnerSecondaryProject = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.spinner_cluster_map_info_projects_kind));
        List<Cursus> cursus = CacheCursus.get(activity.app.cacheSQLiteHelper);
        if (cursus != null)
            adapterSpinnerSecondaryLevels = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, Cursus.getStrings(cursus));

        spinnerMain.setSelection(activity.layerSettings.layer.getId());
        spinnerMain.setOnItemSelectedListener(this);
        spinnerSecondary.setOnItemSelectedListener(this);
        if (activity.layerSettingsInProgress.layer == ClusterLayersSettings.LayerStatus.PROJECT) {
            spinnerSecondary.setAdapter(adapterSpinnerSecondaryProject);
            spinnerSecondary.setSelection(getSpinnerSecondaryPositionProject());
        } else if (activity.layerSettingsInProgress.layer == ClusterLayersSettings.LayerStatus.LEVEL) {
            spinnerSecondary.setAdapter(adapterSpinnerSecondaryLevels);
            spinnerSecondary.setSelection(getSpinnerSecondaryPositionLevel());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButton();
    }

    private int getSpinnerSecondaryPositionProject() {
        switch (activity.layerSettingsInProgress.layerProjectStatus) {
            case CREATING_GROUP:
                return 0;
            case SEARCHING_A_GROUP:
                return 1;
            case WAITING_TO_START:
                return 2;
            case IN_PROGRESS:
                return 3;
            case WAITING_FOR_CORRECTION:
                return 4;
            case FINISHED:
                return 5;
            default:
                return 3;
        }
    }

    private int getSpinnerSecondaryPositionLevel() {
        List<Cursus> cursusList = activity.clusterData.cursusList;
        for (int i = 0; i < cursusList.size(); i++) {
            if (cursusList.get(i).id == activity.layerSettingsInProgress.layerLevelCursus)
                return i;
        }
        return 0;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateButton() {

        if (!isAdded())
            return;

        buttonUpdate.setEnabled(true);
        buttonUpdate.setText(R.string.cluster_map_info_button_update);

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        boolean warning_42tools_enable = mFirebaseRemoteConfig.getBoolean(getString(R.string.firebase_remote_config_warning_42tools_enable));
        if (activity.haveErrorOnLayer.contains(activity.layerSettingsInProgress.layer) && !warning_42tools_enable) {
            buttonUpdate.setEnabled(true);
            buttonUpdate.setText(R.string.retry);
        } else if (activity.layerSettings.equals(activity.layerSettingsInProgress)) {
            buttonUpdate.setEnabled(false);
            buttonUpdate.setText(R.string.cluster_map_info_button_update_disabled);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spinnerMain) {
            setLayerSelectionMain(position);
        } else if (parent == spinnerSecondary) {
            setLayerSelectionSecondary(position);
        }

    }

    private void setLayerSelectionMain(int position) {
        spinnerSecondary.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        layoutLevel.setVisibility(View.GONE);
        switch (position) {
            case 0:
                activity.layerSettingsInProgress.layer = ClusterLayersSettings.LayerStatus.NONE;
                break;
            case 1:
                activity.layerSettingsInProgress.layer = ClusterLayersSettings.LayerStatus.FRIENDS;
                break;
            case 2:
                activity.layerSettingsInProgress.layer = ClusterLayersSettings.LayerStatus.USER;
                editText.setVisibility(View.VISIBLE);
                editText.setHint(R.string.cluster_map_info_layer_input_login);
                editText.setText(activity.layerSettingsInProgress.layerUserLogin);
                break;
            case 3:
                activity.layerSettingsInProgress.layer = ClusterLayersSettings.LayerStatus.PROJECT;
                editText.setVisibility(View.VISIBLE);
                spinnerSecondary.setAdapter(adapterSpinnerSecondaryProject);
                spinnerSecondary.setSelection(getSpinnerSecondaryPositionProject());
                spinnerSecondary.setVisibility(View.VISIBLE);
                editText.setHint(R.string.cluster_map_info_layer_input_project);
                editText.setText(activity.layerSettingsInProgress.layerProjectSlug);
                break;
            case 4:
                activity.layerSettingsInProgress.layer = ClusterLayersSettings.LayerStatus.LOCATION;
                editText.setVisibility(View.VISIBLE);
                editText.setHint(R.string.cluster_map_info_layer_input_location);
                editText.setText(activity.layerSettingsInProgress.layerLocationPost);
                break;
            case 5:
                activity.layerSettingsInProgress.layer = ClusterLayersSettings.LayerStatus.LEVEL;
                spinnerSecondary.setAdapter(adapterSpinnerSecondaryLevels);
                spinnerSecondary.setSelection(getSpinnerSecondaryPositionLevel());
                spinnerSecondary.setVisibility(View.VISIBLE);
                layoutLevel.setVisibility(View.VISIBLE);
                editTextLevelMin.setText(String.valueOf(activity.layerSettingsInProgress.layerLevelMin));
                if (activity.layerSettingsInProgress.layerLevelMax == -1f)
                    editTextLevelMax.setText("21+");
                else
                    editTextLevelMax.setText(String.valueOf(activity.layerSettingsInProgress.layerLevelMax));
                checkboxLevels.setChecked(activity.layerSettingsInProgress.layerLevelShowClosedCursusUser);
                break;
        }

        if (activity.haveErrorOnLayer.contains(activity.layerSettingsInProgress.layer))
            cardViewApiError.setVisibility(View.VISIBLE);
        else
            cardViewApiError.setVisibility(View.GONE);

        layoutDisabledLayer.setVisibility(View.GONE);
        if (activity.layerSettingsInProgress.layer == ClusterLayersSettings.LayerStatus.FRIENDS) {
            final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            mFirebaseRemoteConfig.fetch(AppClass.FIREBASE_REMOTE_CONFIG_CACHE_EXPIRATION)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                // After config data is successfully fetched, it must be activated before newly fetched
                                // values are returned.
                                mFirebaseRemoteConfig.activateFetched();
                            }
                            if (!isAdded())
                                return;

                            if (activity.layerSettingsInProgress.layer == ClusterLayersSettings.LayerStatus.FRIENDS) { // add this condition a second time in case layout changed
                                boolean warning_42tools_enable = mFirebaseRemoteConfig.getBoolean(getString(R.string.firebase_remote_config_warning_42tools_enable));
                                if (warning_42tools_enable) {

                                    String message = mFirebaseRemoteConfig.getString(getString(R.string.firebase_remote_config_warning_42tools_message));
                                    if (message != null && !message.isEmpty())
                                        textViewWarningDisabledLayer.setText(message);
                                    else
                                        textViewWarningDisabledLayer.setText(R.string.cluster_map_info_layer_warning_disabled);

                                    layoutDisabledLayer.setVisibility(View.VISIBLE);
                                    buttonUpdate.setEnabled(false);
                                    cardViewApiError.setVisibility(View.GONE);
                                } else
                                    layoutDisabledLayer.setVisibility(View.GONE);
                            }
                            updateButton();
                        }
                    });
        }

        updateButton();
    }

    private void setLayerSelectionSecondary(int position) {
        if (activity.layerSettingsInProgress.layer == ClusterLayersSettings.LayerStatus.PROJECT) {
            switch (position) {
                case 0:
                    activity.layerSettingsInProgress.layerProjectStatus = ProjectsUsers.Status.CREATING_GROUP;
                    break;
                case 1:
                    activity.layerSettingsInProgress.layerProjectStatus = ProjectsUsers.Status.SEARCHING_A_GROUP;
                    break;
                case 2:
                    activity.layerSettingsInProgress.layerProjectStatus = ProjectsUsers.Status.WAITING_TO_START;
                    break;
                case 3:
                    activity.layerSettingsInProgress.layerProjectStatus = ProjectsUsers.Status.IN_PROGRESS;
                    break;
                case 4:
                    activity.layerSettingsInProgress.layerProjectStatus = ProjectsUsers.Status.WAITING_FOR_CORRECTION;
                    break;
                case 5:
                    activity.layerSettingsInProgress.layerProjectStatus = ProjectsUsers.Status.FINISHED;
                    break;

            }
        } else if (activity.layerSettingsInProgress.layer == ClusterLayersSettings.LayerStatus.LEVEL) {
            activity.layerSettingsInProgress.layerLevelCursus = activity.clusterData.cursusList.get(position).id;
        }
        updateButton();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        if (v == buttonContribute) {
            ClusterMapContributeActivity.openIt(getContext());
        } else if (v == buttonUpdate) {

            spinnerMain.setEnabled(false);
            spinnerMain.setOnItemSelectedListener(null);
            spinnerSecondary.setEnabled(false);
            spinnerSecondary.setOnItemSelectedListener(null);
            editText.setEnabled(false);
            editText.removeTextChangedListener(textWatcherMain);
            buttonUpdate.setClickable(false);
            buttonUpdate.setEnabled(false);
            editTextLevelMin.setEnabled(false);
            editTextLevelMin.removeTextChangedListener(textWatcherLevelMin);
            editTextLevelMax.setEnabled(false);
            editTextLevelMax.removeTextChangedListener(textWatcherLevelMax);
            checkboxLevels.setEnabled(false);
            swipeRefreshLayout.setEnabled(false);
            multiSliderLevels.setEnabled(false);

            switch (activity.layerSettingsInProgress.layer) {
                case NONE:
                    activity.applyLayerFriends();
                    activity.removeLayer();
                    break;
                case USER:
                    activity.applyLayerUser(activity.layerSettingsInProgress.layerUserLogin);
                    finishApplyLayer();
                    break;
                case FRIENDS:
                    if (activity.clusterData.friends == null || activity.haveErrorOnLayer.contains(ClusterLayersSettings.LayerStatus.FRIENDS)) {
                        loadingViewStartCircularReveal();
                        loadDataFriends();
                    } else {
                        activity.applyLayerFriends();
                        finishApplyLayer();
                    }
                    break;
                case PROJECT:
                    loadingViewStartCircularReveal();
                    layerProjectFindSlug();
                    break;
                case LOCATION:
                    activity.applyLayerLocation(activity.layerSettingsInProgress.layerLocationPost);
                    finishApplyLayer();
                    break;
                case LEVEL:
                    loadingViewStartCircularReveal();
                    layerLevelGetData();
                    break;
            }
        }
    }

    private void loadingViewStartCircularReveal() {

        layoutLoading.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        final View view = layoutLoading;
        final View startView = buttonUpdate;
        int cx = (startView.getLeft() + startView.getRight()) / 2;
        int cy = (startView.getTop() + startView.getBottom()) / 2;
        int finalRadius = Math.max(Math.max(cy, view.getHeight() - cy), Math.max(cx, view.getWidth() - cx));
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(200);
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    private void loadingViewStartCircularHide() {

        layoutLoading.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        layoutLoading.setVisibility(View.VISIBLE);
        try {
            int cx = (buttonUpdate.getLeft() + buttonUpdate.getRight()) / 2;
            int cy = (buttonUpdate.getTop() + buttonUpdate.getBottom()) / 2;
            int finalRadius = Math.max(Math.max(cy, layoutLoading.getHeight() - cy), Math.max(cx, layoutLoading.getWidth() - cx));
            Animator anim = ViewAnimationUtils.createCircularReveal(layoutLoading, cx, cy, finalRadius, 0);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    layoutLoading.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    layoutLoading.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.setDuration(200);
            anim.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFriends() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiService42Tools api = activity.app.getApiService42Tools();
                    final List<FriendsSmall> friendsTmp = Friends.getFriends(api);
                    activity.clusterData.friends = new SparseArray<>();
                    for (FriendsSmall f : friendsTmp) {
                        activity.clusterData.friends.put(f.id, f);
                    }
                    activity.haveErrorOnLayer.remove(ClusterLayersSettings.LayerStatus.FRIENDS);
                } catch (IOException | RuntimeException e) {
                    finishApplyLayerOnThread(false);
                    e.printStackTrace();
                    if (!activity.haveErrorOnLayer.contains(ClusterLayersSettings.LayerStatus.FRIENDS)) {
                        activity.haveErrorOnLayer.add(ClusterLayersSettings.LayerStatus.FRIENDS);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.applyLayerFriends();
                    }
                });
            }
        }).start();
    }

    private void layerProjectFindSlug() {

        if (activity.layerSettings.layerProjectSlug != null &&
                activity.layerSettingsInProgress.layerProjectSlug != null &&
                activity.layerSettings.layerProjectSlug.contentEquals(activity.layerSettingsInProgress.layerProjectSlug)) {
            activity.applyLayerProject(activity.layerSettingsInProgress.layerProjectStatus);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ApiService api = activity.app.getApiService();
                String searchedProject = editText.getText().toString();
                try {

                    if (searchedProject.contentEquals(searchedProject.toLowerCase())) {
                        // first try to use user's entered string as project slug
                        Response<Projects> responseProject = api.getProject(searchedProject).execute();
                        final Projects project = responseProject.body();
                        if (Tools.apiIsSuccessfulNoThrow(responseProject) && project != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    layerProjectApplySlug(project.slug);
                                }
                            });
                            return;
                        }
                    }

                    // secondary, search a project with this string
                    List<Projects> projects = null;
                    Response<List<Projects>> responseProjectList = api.getProjectsSearch(searchedProject).execute();
                    if (Tools.apiIsSuccessfulNoThrow(responseProjectList))
                        projects = responseProjectList.body();

                    final List<Projects> finalProjects = projects;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layerProjectSelectRightProject(finalProjects);
                        }
                    });

                } catch (IOException e) {
                    finishApplyLayerOnThread(false);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void layerLevelGetData() {
        if (activity.clusterData.cursusUsers != null &&
                activity.clusterData.cursusUsers.get(activity.layerSettingsInProgress.layerLevelCursus) != null) {
            activity.applyLayerLevel();
            return;
        }

        final int pageSize = 100;
        progressBar.setRotation(0);
        progressBar.setMax((int) Math.ceil((float) activity.clusterData.locations.size() / (float) pageSize));
        progressBar.setIndeterminate(false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                final List<CursusUsers> cursusUsersList = new ArrayList<>();

                final ApiService api = activity.app.getApiService();
                int cursusId = activity.layerSettingsInProgress.layerLevelCursus;
                int id = 0;

                try {
                    while (id < activity.clusterData.locations.size()) {
                        loadingViewUpdateProgress((int) Math.ceil((float) id / (float) pageSize));
                        String ids = UsersLTE.concatIds(new ArrayList<>(activity.clusterData.locations.values()), id, pageSize);
                        Response<List<CursusUsers>> response = api.getCursusUsers(ids, cursusId, pageSize, 1).execute();
                        if (!Tools.apiIsSuccessfulNoThrow(response)) {
                            finishApplyLayerOnThread(false);
                            return;
                        }
                        cursusUsersList.addAll(response.body());
                        id += pageSize;
                    }
                } catch (IOException e) {
                    finishApplyLayerOnThread(false);
                    e.printStackTrace();
                }

                if (activity.clusterData.cursusUsers == null)
                    activity.clusterData.cursusUsers = new SparseArray<>();
                activity.clusterData.cursusUsers.append(cursusId, new SparseArray<CursusUsers>()); // mark this cluster data called once, and reset it
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.applyLayerLevel(cursusUsersList);
                        finishApplyLayer();
                    }
                });
            }
        }).start();
    }

    private void layerProjectSelectRightProject(final List<Projects> projects) {

        if (projects == null || projects.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.cluster_map_info_project_dialog_not_found);
            builder.setMessage(String.format(Locale.getDefault(), getString(R.string.cluster_map_info_project_dialog_not_found_content_format), editText.getText().toString()));
            builder.setPositiveButton(R.string.ok, null);
            AlertDialog alert = builder.create();
            alert.show();
            finishApplyLayer();
            return;
        } else if (projects.size() == 1) {
            editText.setText(projects.get(0).slug);
            layerProjectApplySlug(projects.get(0).slug);
            return;
        }

        final CharSequence[] items = new CharSequence[projects.size()];
        int i = 0;
        for (Projects project : projects) {
            items[i] = project.name;
            ++i;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.select);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                layerProjectApplySlug(projects.get(item).slug);
            }
        });
        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finishApplyLayer();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void layerProjectApplySlug(final String slug) {
        final List<ProjectsUsers> projectsUsersList = new ArrayList<>();
        final int pageSize = 100;

        editText.setText(slug);
        activity.layerSettingsInProgress.layerProjectSlug = slug;

        progressBar.setRotation(0);
        progressBar.setMax((int) Math.ceil((float) activity.clusterData.locations.size() / (float) pageSize));
        progressBar.setIndeterminate(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    int id = 0;

                    while (id < activity.clusterData.locations.size()) {
                        loadingViewUpdateProgress((int) Math.ceil((float) id / (float) pageSize));
                        String ids = UsersLTE.concatIds(new ArrayList<>(activity.clusterData.locations.values()), id, pageSize);
                        final ApiService api = activity.app.getApiService();
                        Response<List<ProjectsUsers>> response = api.getProjectIDProjectsUsers(slug, ids, pageSize, 1).execute();
                        if (!Tools.apiIsSuccessfulNoThrow(response)) {
                            finishApplyLayerOnThread(false);
                            return;
                        }
                        projectsUsersList.addAll(response.body());
                        id += pageSize;
                    }
                } catch (IOException e) {
                    finishApplyLayerOnThread(false);
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.applyLayerProject(projectsUsersList, slug, activity.layerSettingsInProgress.layerProjectStatus);
                        finishApplyLayer();
                    }
                });
            }
        }).start();
    }

    private void loadingViewUpdateProgress(final int progress) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    progressBar.setProgress(progress, true);
                else
                    progressBar.setProgress(progress);

            }
        });
    }

    /**
     * Called when all data are set and this view is ready to be updated
     */
    private void finishApplyLayer(final boolean successful) {
        loadingViewStartCircularHide();
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();

        spinnerMain.setEnabled(true);
        spinnerMain.setOnItemSelectedListener(this);
        spinnerSecondary.setEnabled(true);
        spinnerSecondary.setOnItemSelectedListener(this);
        editText.setEnabled(true);
        editText.addTextChangedListener(textWatcherMain);
        buttonUpdate.setClickable(true);
        buttonUpdate.setEnabled(true);
        editTextLevelMin.setEnabled(true);
        editTextLevelMin.addTextChangedListener(textWatcherLevelMin);
        editTextLevelMax.setEnabled(true);
        editTextLevelMax.addTextChangedListener(textWatcherLevelMax);
        checkboxLevels.setEnabled(true);
        swipeRefreshLayout.setEnabled(true);
        multiSliderLevels.setEnabled(true);

        updateButton();

        if (!successful)
            Toast.makeText(activity, R.string.cluster_map_info_data_layer_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when all data are set and this view is ready to be updated
     */
    private void finishApplyLayer() {
        finishApplyLayer(true);
    }

    /**
     * Called when all data are set and this view is ready to be updated
     */
    private void finishApplyLayerOnThread(final boolean successful) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finishApplyLayer(successful);
            }
        });
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        if (activity != null)
            activity.refreshCluster();
    }

    @Override
    public void onItemClicked(int position, Cluster cluster) {
        activity.viewPager.setCurrentItem(position + 1, true);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
