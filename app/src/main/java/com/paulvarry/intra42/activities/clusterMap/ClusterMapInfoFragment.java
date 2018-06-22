package com.paulvarry.intra42.activities.clusterMap;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        implements AdapterView.OnItemSelectedListener, TextWatcher, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, RecyclerAdapterClusterMapInfo.OnItemClickListener {

    private ClusterMapActivity activity;

    private RecyclerAdapterClusterMapInfo adapter;

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

    private OnFragmentInteractionListener mListener;

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

        if (activity.clusterStatus.clusters == null || activity.clusterStatus.clusters.size() == 0) {
            textViewNoClusterMap.setVisibility(View.VISIBLE);
            textViewClusters.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            textViewLayerTitle.setVisibility(View.GONE);
            textViewLayerDescription.setVisibility(View.GONE);
            layoutLayerContent.setVisibility(View.GONE);
        } else {
            textViewNoClusterMap.setVisibility(View.GONE);
            adapter = new RecyclerAdapterClusterMapInfo(getContext(), activity.clusterStatus);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setNestedScrollingEnabled(false);
        }

        textViewClusters.setTextColor(ThemeHelper.getColorAccent(getContext()));
        textViewLayerTitle.setTextColor(ThemeHelper.getColorAccent(getContext()));
        textViewContributeTitle.setTextColor(ThemeHelper.getColorAccent(getContext()));

        editText.addTextChangedListener(this);
        buttonUpdate.setOnClickListener(this);

        int statusLayerSelection = activity.clusterStatus.layerStatus.getId();
        int projectStatusSelection = getProjectSelectionPosition();
        spinnerMain.setSelection(statusLayerSelection);
        spinnerMain.setOnItemSelectedListener(this);
        spinnerSecondary.setOnItemSelectedListener(this);
        spinnerSecondary.setSelection(projectStatusSelection);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private int getProjectSelectionPosition() {
        switch (activity.layerTmpProjectStatus) {
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

    public void updateButton() {
        buttonUpdate.setEnabled(true);
        buttonUpdate.setText(R.string.cluster_map_info_button_update);

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        boolean warning_42tools_enable = mFirebaseRemoteConfig.getBoolean(getString(R.string.firebase_remote_config_warning_42tools_enable));
        if (activity.haveErrorOnLayer.contains(activity.layerTmpStatus) && !warning_42tools_enable) {
            buttonUpdate.setEnabled(true);
            buttonUpdate.setText(R.string.retry);
        } else if (!isLayerChanged()) {
            buttonUpdate.setEnabled(false);
            buttonUpdate.setText(R.string.cluster_map_info_button_update_disabled);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spinnerMain) {
            setLayerSelectionTmpMain(position);
        } else if (parent == spinnerSecondary) {
            setLayerSelectionTmpSecondary(position);
        }

    }

    private void setLayerSelectionTmpMain(int position) {
        spinnerSecondary.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        switch (position) {
            case 0:
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.NONE;
                break;
            case 1:
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.FRIENDS;
                break;
            case 2:
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.USER;
                editText.setVisibility(View.VISIBLE);
                editText.setHint(R.string.cluster_map_info_layer_input_login);
                editText.setText(activity.layerTmpLogin);
                break;
            case 3:
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.PROJECT;
                editText.setVisibility(View.VISIBLE);
                spinnerSecondary.setVisibility(View.VISIBLE);
                editText.setHint(R.string.cluster_map_info_layer_input_project);
                editText.setText(activity.layerTmpProjectSlug);
                break;
            case 4:
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.LOCATION;
                editText.setVisibility(View.VISIBLE);
                editText.setHint(R.string.cluster_map_info_layer_input_location);
                editText.setText(activity.layerTmpLocation);
                break;
        }

        if (activity.haveErrorOnLayer.contains(activity.layerTmpStatus))
            cardViewApiError.setVisibility(View.VISIBLE);
        else
            cardViewApiError.setVisibility(View.GONE);

        layoutDisabledLayer.setVisibility(View.GONE);
        if (activity.layerTmpStatus == ClusterMapActivity.LayerStatus.FRIENDS) {
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

                            if (activity.layerTmpStatus == ClusterMapActivity.LayerStatus.FRIENDS) { // add this condition a second time in case layout changed
                                boolean warning_42tools_enable = mFirebaseRemoteConfig.getBoolean(getString(R.string.firebase_remote_config_warning_42tools_enable));
                                if (warning_42tools_enable) {

                                    String message = mFirebaseRemoteConfig.getString(getString(R.string.firebase_remote_config_warning_42tools_message));
                                    if (message != null)
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

    private void setLayerSelectionTmpSecondary(int position) {
        switch (position) {
            case 0:
                activity.layerTmpProjectStatus = ProjectsUsers.Status.CREATING_GROUP;
                break;
            case 1:
                activity.layerTmpProjectStatus = ProjectsUsers.Status.SEARCHING_A_GROUP;
                break;
            case 2:
                activity.layerTmpProjectStatus = ProjectsUsers.Status.WAITING_TO_START;
                break;
            case 3:
                activity.layerTmpProjectStatus = ProjectsUsers.Status.IN_PROGRESS;
                break;
            case 4:
                activity.layerTmpProjectStatus = ProjectsUsers.Status.WAITING_FOR_CORRECTION;
                break;
            case 5:
                activity.layerTmpProjectStatus = ProjectsUsers.Status.FINISHED;
                break;

        }
        updateButton();
    }

    /**
     * Return true if the layer settings have changed
     *
     * @return layer settings changed
     */
    boolean isLayerChanged() {
        return activity.clusterStatus.layerStatus != activity.layerTmpStatus ||
                (activity.clusterStatus.layerStatus == ClusterMapActivity.LayerStatus.USER &&
                        !activity.clusterStatus.layerUserLogin.contentEquals(activity.layerTmpLogin)) ||
                (activity.clusterStatus.layerStatus == ClusterMapActivity.LayerStatus.PROJECT &&
                        (!activity.clusterStatus.layerProjectSlug.contentEquals(activity.layerTmpProjectSlug) ||
                                activity.clusterStatus.layerProjectStatus != activity.layerTmpProjectStatus)) ||
                (activity.clusterStatus.layerStatus == ClusterMapActivity.LayerStatus.LOCATION &&
                        !activity.clusterStatus.layerLocationPost.contentEquals(activity.layerTmpLocation));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        switch (activity.layerTmpStatus) {
            case USER:
                activity.layerTmpLogin = String.valueOf(s);
                break;
            case PROJECT:
                activity.layerTmpProjectSlug = String.valueOf(s);
                break;
            case LOCATION:
                activity.layerTmpLocation = String.valueOf(s);
        }
        updateButton();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {

        if (v == buttonContribute) {
            ClusterMapContributeActivity.openIt(getContext());
        } else if (v == buttonUpdate) {

            spinnerMain.setEnabled(false);
            spinnerSecondary.setEnabled(false);
            editText.setEnabled(false);
            buttonUpdate.setClickable(false);
            buttonUpdate.setEnabled(false);
            spinnerMain.setOnItemSelectedListener(null);
            spinnerSecondary.setOnItemSelectedListener(null);
            editText.removeTextChangedListener(this);
            swipeRefreshLayout.setEnabled(false);

            switch (activity.layerTmpStatus) {
                case NONE:
                    activity.applyLayerFriends();
                    activity.removeLayer();
                    break;
                case USER:
                    activity.applyLayerUser(activity.layerTmpLogin);
                    finishApplyLayer();
                    break;
                case FRIENDS:
                    if (activity.clusterStatus.friends == null || activity.haveErrorOnLayer.contains(ClusterMapActivity.LayerStatus.FRIENDS)) {
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
                    activity.applyLayerLocation(activity.layerTmpLocation);
                    finishApplyLayer();
            }
        }
    }

    public void loadingViewStartCircularReveal() {

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

    public void loadingViewStartCircularHide() {

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

    void loadDataFriends() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiService42Tools api = activity.app.getApiService42Tools();
                    final List<FriendsSmall> friendsTmp = Friends.getFriends(api);
                    activity.clusterStatus.friends = new SparseArray<>();
                    for (FriendsSmall f : friendsTmp) {
                        activity.clusterStatus.friends.put(f.id, f);
                    }
                    activity.haveErrorOnLayer.remove(ClusterMapActivity.LayerStatus.FRIENDS);
                } catch (IOException | RuntimeException e) {
                    finishApplyLayerOnThread(false);
                    e.printStackTrace();
                    if (!activity.haveErrorOnLayer.contains(ClusterMapActivity.LayerStatus.FRIENDS)) {
                        activity.haveErrorOnLayer.add(ClusterMapActivity.LayerStatus.FRIENDS);
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

    void layerProjectFindSlug() {

        if (activity.layerTmpProjectSlug != null &&
                activity.clusterStatus.layerProjectSlug != null &&
                activity.layerTmpProjectSlug.contentEquals(activity.clusterStatus.layerProjectSlug)) {
            activity.applyLayerProject(activity.layerTmpProjectStatus);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    List<Projects> projects = null;
                    final ApiService api = activity.app.getApiService();
                    Response<List<Projects>> response = api.getProjectsSearch(editText.getText().toString()).execute();
                    if (Tools.apiIsSuccessfulNoThrow(response))
                        projects = response.body();

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

    void layerProjectSelectRightProject(final List<Projects> projects) {

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

    void layerProjectApplySlug(final String slug) {
        final List<ProjectsUsers> projectsUsersList = new ArrayList<>();
        final int pageSize = 100;

        editText.setText(slug);
        activity.layerTmpProjectSlug = slug;

        progressBar.setRotation(0);
        progressBar.setMax((int) Math.ceil((float) activity.clusterStatus.locations.size() / (float) pageSize));
        progressBar.setIndeterminate(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    int id = 0;

                    while (id < activity.clusterStatus.locations.size()) {
                        loadingViewUpdateProgress((int) Math.ceil((float) id / (float) pageSize));
                        String ids = UsersLTE.concatIds(new ArrayList<>(activity.clusterStatus.locations.values()), id, pageSize);
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
                        activity.applyLayerProject(projectsUsersList, slug, activity.layerTmpProjectStatus);
                        finishApplyLayer();
                    }
                });
            }
        }).start();
    }

    void loadingViewUpdateProgress(final int progress) {
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
    void finishApplyLayer(final boolean successful) {
        loadingViewStartCircularHide();
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();

        spinnerMain.setEnabled(true);
        spinnerSecondary.setEnabled(true);
        editText.setEnabled(true);
        buttonUpdate.setClickable(true);
        buttonUpdate.setEnabled(true);
        editText.addTextChangedListener(this);
        spinnerMain.setOnItemSelectedListener(this);
        spinnerSecondary.setOnItemSelectedListener(this);
        swipeRefreshLayout.setEnabled(true);

        updateButton();

        if (!successful)
            Toast.makeText(activity, R.string.cluster_map_info_data_layer_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when all data are set and this view is ready to be updated
     */
    void finishApplyLayer() {
        finishApplyLayer(true);
    }

    /**
     * Called when all data are set and this view is ready to be updated
     */
    void finishApplyLayerOnThread(final boolean successful) {
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
