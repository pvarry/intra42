package com.paulvarry.intra42.activities.clusterMap;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterClusterMapInfo;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.Theme;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClusterMapInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClusterMapInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterMapInfoFragment extends Fragment implements AdapterView.OnItemSelectedListener, TextWatcher, View.OnClickListener, AdapterView.OnItemClickListener {

    private ClusterMapActivity activity;

    private ListAdapterClusterMapInfo adapter;

    private TextView textViewClusters;
    private TextView textViewLayer;
    private Spinner spinnerMain;
    private Spinner spinnerSecondary;
    private ExpandableHeightListView listView;
    private EditText editText;
    private Button buttonUpdate;
    private ViewGroup layoutLoading;
    private ProgressBar progressBar;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cluster_map_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listView);
        spinnerMain = view.findViewById(R.id.spinnerMain);
        spinnerSecondary = view.findViewById(R.id.spinnerSecondary);
        textViewClusters = view.findViewById(R.id.textViewClusters);
        textViewLayer = view.findViewById(R.id.textViewLayer);
        editText = view.findViewById(R.id.editText);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        progressBar = view.findViewById(R.id.progressBar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ListAdapterClusterMapInfo(getContext(), new ArrayList<>(activity.clusters.clusterInfoList.values()));
        listView.setAdapter(adapter);
        listView.setExpanded(true);

        textViewClusters.setTextColor(Theme.getColorAccent(getContext()));
        textViewLayer.setTextColor(Theme.getColorAccent(getContext()));

        editText.addTextChangedListener(this);
        buttonUpdate.setOnClickListener(this);
        listView.setOnItemClickListener(this);

        int statusLayerSelection = activity.clusters.layerStatus.getId();
        int projectStatusSelection = getProjectSelectionPosition();
        spinnerMain.setSelection(statusLayerSelection);
        spinnerMain.setOnItemSelectedListener(this);
        spinnerSecondary.setOnItemSelectedListener(this);
        spinnerSecondary.setSelection(projectStatusSelection);
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
        if (!isLayerChanged()) {
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
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.FRIENDS;
                break;
            case 1:
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.USER_HIGHLIGHT;
                editText.setVisibility(View.VISIBLE);
                editText.setHint(R.string.cluster_map_info_layer_input_login);
                editText.setText(activity.layerTmpLogin);
                break;
            case 2:
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.COALITIONS;
                break;
            case 3:
                activity.layerTmpStatus = ClusterMapActivity.LayerStatus.PROJECT;
                editText.setVisibility(View.VISIBLE);
                spinnerSecondary.setVisibility(View.VISIBLE);
                editText.setHint(R.string.cluster_map_info_layer_input_project);
                editText.setText(activity.layerTmpProjectSlug);
                break;
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
        return activity.clusters.layerStatus != activity.layerTmpStatus ||
                (activity.clusters.layerStatus == ClusterMapActivity.LayerStatus.USER_HIGHLIGHT &&
                        !activity.clusters.layerLogin.contentEquals(activity.layerTmpLogin)) ||
                (activity.clusters.layerStatus == ClusterMapActivity.LayerStatus.PROJECT &&
                        (!activity.clusters.layerProjectSlug.contentEquals(activity.layerTmpProjectSlug) ||
                                activity.clusters.layerProjectStatus != activity.layerTmpProjectStatus));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (activity.layerTmpStatus == ClusterMapActivity.LayerStatus.USER_HIGHLIGHT)
            activity.layerTmpLogin = String.valueOf(s);
        else if (activity.layerTmpStatus == ClusterMapActivity.LayerStatus.PROJECT)
            activity.layerTmpProjectSlug = String.valueOf(s);
        updateButton();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        spinnerMain.setEnabled(false);
        spinnerSecondary.setEnabled(false);
        editText.setEnabled(false);
        buttonUpdate.setClickable(false);
        buttonUpdate.setEnabled(false);
        spinnerMain.setOnItemSelectedListener(null);
        spinnerSecondary.setOnItemSelectedListener(null);
        editText.removeTextChangedListener(this);

        switch (activity.layerTmpStatus) {
            case USER_HIGHLIGHT:
                activity.applyLayerUser(activity.layerTmpLogin);
                finishApplyLayer();
                break;
            case FRIENDS:
                activity.applyLayerFriends();
                finishApplyLayer();
                break;
            case PROJECT:
                loadingViewStartCircularReveal();
                layerProjectFindSlug();
                break;
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

    void layerProjectFindSlug() {

        if (activity.layerTmpProjectSlug != null &&
                activity.clusters.layerProjectSlug != null &&
                activity.layerTmpProjectSlug.contentEquals(activity.clusters.layerProjectSlug)) {
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
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void layerProjectSelectRightProject(final List<Projects> projects) {

        if (projects == null || projects.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Not found");
            builder.setMessage("No project found for: " + editText.getText().toString());
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
        builder.setTitle("Select");
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
        final List<ProjectsUsers> list = new ArrayList<>();
        final int pageSize = 100;

        editText.setText(slug);
        activity.layerTmpProjectSlug = slug;

        progressBar.setRotation(0);
        progressBar.setMax((int) Math.ceil((float) activity.clusters.locations.size() / (float) pageSize));
        progressBar.setIndeterminate(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    int id = 0;

                    while (id < activity.clusters.locations.size()) {
                        loadingViewUpdateProgress((int) Math.ceil((float) id / (float) pageSize));
                        String ids = UsersLTE.concatIds(new ArrayList<>(activity.clusters.locations.values()), id, pageSize);
                        final ApiService api = activity.app.getApiService();
                        Response<List<ProjectsUsers>> response = api.getProjectsUsers(slug, ids, pageSize, 1).execute();
                        if (!Tools.apiIsSuccessfulNoThrow(response))
                            return;
                        list.addAll(response.body());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        id += pageSize;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.applyLayerProject(list, slug, activity.layerTmpProjectStatus);
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
    void finishApplyLayer() {
        layoutLoading.setVisibility(View.GONE);
        listView.invalidate();
        adapter.notifyDataSetChanged();

        spinnerMain.setEnabled(true);
        spinnerSecondary.setEnabled(true);
        editText.setEnabled(true);
        buttonUpdate.setClickable(true);
        buttonUpdate.setEnabled(true);
        editText.addTextChangedListener(this);
        spinnerMain.setOnItemSelectedListener(this);
        spinnerSecondary.setOnItemSelectedListener(this);

        updateButton();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
