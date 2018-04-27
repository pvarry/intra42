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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.LocationHistoryActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Location;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;

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
    private Cluster clusterInfo;

    //    private ViewGroup viewGroupMain;
    private RecyclerView recyclerView;
    private TextView textViewEmpty;

    private LayoutInflater vi;
    private int itemPadding2dp;
    private int itemPadding3dp;

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
        vi = LayoutInflater.from(activity);
        itemPadding2dp = Tools.dpToPxInt(activity, 2);
        itemPadding3dp = Tools.dpToPxInt(activity, 3);
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
//        viewGroupMain = view.findViewById(R.id.viewGroupMain);
        recyclerView = view.findViewById(R.id.recyclerView);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clusters = activity.clusterStatus;
        clusterInfo = activity.clusterStatus.getCluster(clusterName);

        if (clusterInfo == null || clusterInfo.map == null || clusterInfo.map.length == 0 || clusterInfo.sizeY == 0 || clusterInfo.sizeX == 0) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
            makeMap();
        }
    }

    void makeMap() {

        // set base item size
        baseItemHeight = Tools.dpToPxInt(activity, 42);
        baseItemWidth = Tools.dpToPxInt(activity, 35);

        if (clusterInfo == null || clusterInfo.map == null)
            return;

//        gridLayout.removeAllViews();
//        gridLayout.removeAllViewsInLayout();
//        gridLayout.setColumnCount(clusterInfo.map.length);
//
//        gridLayout.setRowCount(clusterInfo.sizeY);
//        gridLayout.setColumnCount(clusterInfo.sizeX);
//
//        Location locationItem;
//        for (int y = 0; y < clusterInfo.sizeY; y++) {
//            for (int x = 0; x < clusterInfo.sizeX; x++) {
//
//                locationItem = clusterInfo.map[x][y];
//                if (locationItem == null)
//                    continue;
//
//                View view = makeMapItem(clusterInfo.map[x][y], x, y);
//                gridLayout.addView(view);
//            }
//        }

        ClusterMapAdapter adapter = new ClusterMapAdapter(getContext(), clusterInfo, clusters);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), clusterInfo.sizeX, LinearLayoutManager.VERTICAL, false));
    }

    private View makeMapItem(Location location, int x, int y) {

        View view;
        ImageView imageViewContent;
        GridLayout.LayoutParams paramsGridLayout;
        UsersLTE user;

        if (vi == null)
            return null;

        user = clusters.getUserInLocation(location);

        if (location.highlight) {
            view = vi.inflate(R.layout.grid_layout_cluster_map_highlight, recyclerView, false);
            imageViewContent = view.findViewById(R.id.imageView);
        } else {
//            view = vi.inflate(R.layout.grid_layout_cluster_map, gridLayout, false);
            view = new ImageView(activity);
            view.setLayoutParams(new GridLayout.LayoutParams());
            imageViewContent = (ImageView) view;
            imageViewContent.setAdjustViewBounds(true);
        }

        if (location.kind == Location.Kind.USER) {
            view.setTag(location);
            view.setOnClickListener(this);

            if (location.host == null) {
                imageViewContent.setImageResource(R.drawable.ic_missing_black_25dp);
                imageViewContent.setColorFilter(ContextCompat.getColor(activity, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                if (user != null)
                    UserImage.setImageSmall(activity, user, imageViewContent);
                else {
                    imageViewContent.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_desktop_mac_black_custom));
                    imageViewContent.setColorFilter(ContextCompat.getColor(activity, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
                }
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
        paramsGridLayout.height = (int) (baseItemHeight * location.sizeY);
        paramsGridLayout.width = (int) (baseItemWidth * location.sizeX);

        if (location.kind == Location.Kind.WALL) {
            int paddingLeft = itemPadding2dp;
            int paddingTop = itemPadding2dp;
            int paddingRight = itemPadding2dp;
            int paddingEnd = itemPadding2dp;

            Location left = getPosition(x - 1, y);
            if (left != null && left.kind == Location.Kind.WALL)
                paddingLeft = 0;
            Location top = getPosition(x, y - 1);
            if (top != null && top.kind == Location.Kind.WALL)
                paddingTop = 0;
            Location right = getPosition(x + 1, y);
            if (right != null && right.kind == Location.Kind.WALL)
                paddingRight = 0;
            Location end = getPosition(x, y + 1);
            if (end != null && end.kind == Location.Kind.WALL)
                paddingEnd = 0;

            imageViewContent.setPadding(paddingLeft, paddingTop, paddingRight, paddingEnd);
        } else
            imageViewContent.setPadding(itemPadding2dp, itemPadding2dp, itemPadding2dp, itemPadding2dp);

        view.setLayoutParams(paramsGridLayout);

        if (location.highlight) {
            FrameLayout.LayoutParams paramsFrameLayout = (FrameLayout.LayoutParams) imageViewContent.getLayoutParams();
            paramsFrameLayout.height = (int) (baseItemHeight * location.sizeY);
            paramsFrameLayout.width = (int) (baseItemWidth * location.sizeX);
            imageViewContent.setLayoutParams(paramsFrameLayout);

            imageViewContent.setPadding(0, 0, 0, 0);
            view.setPadding(itemPadding3dp, itemPadding3dp, itemPadding3dp, itemPadding3dp);

            TypedValue a = new TypedValue();
            activity.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
            imageViewContent.setBackgroundResource(a.resourceId);

            view.setBackgroundColor(ThemeHelper.getColorAccent(activity));
        }

        return view;
    }

    Location getPosition(int x, int y) {
        if (x < 0 ||
                y < 0 ||
                clusterInfo.map.length <= x ||
                clusterInfo.map[x] == null ||
                clusterInfo.map[x].length <= y)
            return null;
        return clusterInfo.map[x][y];
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
        Location location = null;
        UsersLTE user = null;

        if (v.getTag() instanceof Location)
            location = (Location) v.getTag();
        if (location == null || location.kind != Location.Kind.USER)
            return;

        if (clusters.locations != null)
            user = clusters.locations.get(location.host);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.cluster_map_dialog_action);
        String[] actions;
        if (user != null)
            actions = new String[]{String.format(getString(R.string.format__host_s_history), location.host), String.format(getString(R.string.format__user_profile), user.login)};
        else
            actions = new String[]{String.format(getString(R.string.format__host_s_history), location.host)};

        final UsersLTE finalUser = user;
        final String locationName = location.host;
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    LocationHistoryActivity.openItWithLocation(activity, locationName);
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
