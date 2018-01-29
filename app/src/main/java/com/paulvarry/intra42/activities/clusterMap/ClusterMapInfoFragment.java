package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterClusterMapInfo;
import com.paulvarry.intra42.utils.Theme;

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ListAdapterClusterMapInfo(getContext(), activity.clusters.clusterInfoList);
        listView.setAdapter(adapter);
        listView.setExpanded(true);

        textViewClusters.setTextColor(Theme.getColorAccent(getContext()));
        textViewLayer.setTextColor(Theme.getColorAccent(getContext()));

        editText.addTextChangedListener(this);
        buttonUpdate.setOnClickListener(this);
        listView.setOnItemClickListener(this);

        int layerSelection = 0;
        switch (activity.clusters.layerStatus) {
            case FRIENDS:
                layerSelection = 0;
                break;
            case COALITIONS:
                layerSelection = 1;
                break;
            case USER_HIGHLIGHT:
                layerSelection = 2;
                break;
            case PROJECT:
                layerSelection = 3;
                break;
        }
        spinnerMain.setSelection(layerSelection);
        spinnerMain.setOnItemSelectedListener(this);
        activity.layerTmpStatus = activity.clusters.layerStatus;

        setMainLayerTmpSelection(layerSelection);
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
            setMainLayerTmpSelection(position);
        }

    }

    void setMainLayerTmpSelection(int position) {
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

    /**
     * Return true if the layer settings have changed
     *
     * @return layer settings changed
     */
    boolean isLayerChanged() {
        return activity.clusters.layerStatus != activity.layerTmpStatus ||
                (activity.clusters.layerStatus == ClusterMapActivity.LayerStatus.USER_HIGHLIGHT && !activity.clusters.layerLogin.contentEquals(activity.layerTmpLogin));
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
        layoutLoading.setVisibility(View.VISIBLE);

        spinnerMain.setEnabled(false);
        editText.setEnabled(false);
        buttonUpdate.setClickable(false);


        // compute data


        if (activity.layerTmpStatus == ClusterMapActivity.LayerStatus.USER_HIGHLIGHT)
            activity.applyLayerUser(activity.layerTmpLogin);
        else if (activity.layerTmpStatus == ClusterMapActivity.LayerStatus.FRIENDS) {
            activity.applyLayerFriends();
        }
        activity.clusters.computeHighlightSpots();


        layoutLoading.setVisibility(View.GONE);
        listView.invalidate();
        adapter.notifyDataSetChanged();

        spinnerMain.setEnabled(true);
        editText.setEnabled(true);
        buttonUpdate.setClickable(true);

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
