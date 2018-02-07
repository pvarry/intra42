package com.paulvarry.intra42.activities.clusterMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.LocationHistoryActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.Theme;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;
import com.paulvarry.intra42.utils.clusterMap.ClusterItem;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;
import com.paulvarry.intra42.utils.clusterMap.LocationItem;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClusterMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClusterMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterMapFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_HOST_PREFIX = "hostPrefix";
    float baseItemWidth;
    float baseItemHeight;

    private String clusterName;
    private ClusterMapActivity activity;
    private ClusterStatus clusters;
    private ClusterItem clusterInfo;
    private GridLayout gridLayout;

    private OnFragmentInteractionListener mListener;

    public ClusterMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ClusterMapFragment.
     */
    public static ClusterMapFragment newInstance(String param1) {
        ClusterMapFragment fragment = new ClusterMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HOST_PREFIX, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clusterName = getArguments().getString(ARG_HOST_PREFIX);
        }
        activity = (ClusterMapActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cluster_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridLayout = view.findViewById(R.id.gridLayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clusters = activity.clusters;
        clusterInfo = activity.clusters.clusterInfoList.get(clusterName);
        makeMap();
    }

    void makeMap() {

        // set base item size
        baseItemHeight = Tools.dpToPx(getContext(), 42);
        baseItemWidth = Tools.dpToPx(getContext(), 35);

        if (clusterInfo == null || clusterInfo.map == null)
            return;

        gridLayout.removeAllViews();
        gridLayout.removeAllViewsInLayout();
        gridLayout.setRowCount(clusterInfo.map.length);

        for (int r = 0; r < clusterInfo.map.length; r++) {

            gridLayout.setColumnCount(clusterInfo.map[r].length);
            for (int p = 0; p < clusterInfo.map[r].length; p++) {

                if (clusterInfo.map[r][p] == null)
                    break;

                View view = makeMapItem(clusterInfo.map, r, p);
                gridLayout.addView(view);
            }
        }
    }

    private View makeMapItem(final LocationItem[][] cluster, int r, int p) {

        final LocationItem locationItem = cluster[r][p];
        View view;
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageViewContent;
        int padding = Tools.dpToPx(getContext(), 2);
        GridLayout.LayoutParams paramsGridLayout;
        UsersLTE user;

        if (vi == null)
            return null;

        user = clusters.getUserInLocation(locationItem);

        if (locationItem.highlight == null || locationItem.highlight)
            view = vi.inflate(R.layout.grid_layout_cluster_map_highlight, gridLayout, false);
        else
            view = vi.inflate(R.layout.grid_layout_cluster_map, gridLayout, false);

        imageViewContent = view.findViewById(R.id.imageView);
        if (locationItem.kind == LocationItem.KIND_USER) {
            view.setTag(locationItem);
            view.setOnClickListener(this);

            if (locationItem.locationName == null || locationItem.locationName.contentEquals("null") || locationItem.locationName.contentEquals("TBD"))
                imageViewContent.setImageResource(R.drawable.ic_close_black_24dp);
            else {
                if (user != null)
                    UserImage.setImageSmall(getContext(), user, imageViewContent);
                else
                    imageViewContent.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_desktop_mac_black_custom));
            }

        } else if (locationItem.kind == LocationItem.KIND_WALL)
            imageViewContent.setImageResource(R.color.colorClusterMapWall);
        else {
            imageViewContent.setImageResource(R.drawable.ic_add_black_24dp);
            imageViewContent.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.CLEAR);
        }

        paramsGridLayout = (GridLayout.LayoutParams) view.getLayoutParams();
        paramsGridLayout.columnSpec = GridLayout.spec(p);
        paramsGridLayout.rowSpec = GridLayout.spec(r);
        paramsGridLayout.setGravity(Gravity.FILL);
        paramsGridLayout.height = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsGridLayout.width = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsGridLayout.height = (int) (baseItemHeight * locationItem.sizeY);
        paramsGridLayout.width = (int) (baseItemWidth * locationItem.sizeX);
        imageViewContent.setPadding(padding, padding, padding, padding);
        view.setLayoutParams(paramsGridLayout);

        if (locationItem.highlight == null || locationItem.highlight) {
            padding = Tools.dpToPx(getContext(), 3);
            FrameLayout.LayoutParams paramsFrameLayout = (FrameLayout.LayoutParams) imageViewContent.getLayoutParams();
            paramsFrameLayout.height = (int) (baseItemHeight * locationItem.sizeY);
            paramsFrameLayout.width = (int) (baseItemWidth * locationItem.sizeX);
            imageViewContent.setLayoutParams(paramsFrameLayout);

            imageViewContent.setPadding(0, 0, 0, 0);
            view.setPadding(padding, padding, padding, padding);

            imageViewContent.setBackgroundResource(R.color.windowBackground);

            if (locationItem.highlight == null)
                view.setBackgroundColor(Theme.getColorPrimary(getContext()));
            else
                view.setBackgroundColor(Theme.getColorAccent(getContext()));
        }

        return view;
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

    @Override
    public void onClick(View v) {
        LocationItem locationItem = null;
        UsersLTE user = null;

        if (v.getTag() instanceof LocationItem)
            locationItem = (LocationItem) v.getTag();
        if (locationItem == null || locationItem.kind != LocationItem.KIND_USER)
            return;

        if (clusters.locations != null)
            user = clusters.locations.get(locationItem.locationName);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.cluster_map_dialog_action);
        String[] actions;
        if (user != null)
            actions = new String[]{String.format(getString(R.string.format__host_s_history), locationItem.locationName), String.format(getString(R.string.format__user_profile), user.login)};
        else
            actions = new String[]{String.format(getString(R.string.format__host_s_history), locationItem.locationName)};

        final UsersLTE finalUser = user;
        final String location = locationItem.locationName;
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    LocationHistoryActivity.openItWithLocation(activity, location);
                } else if (which == 1) {
                    UserActivity.openIt(activity, finalUser);
                }
            }
        });
        builder.show();

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
