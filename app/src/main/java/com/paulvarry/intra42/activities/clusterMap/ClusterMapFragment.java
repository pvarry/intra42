package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.LocationHistoryActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.UserImage;
import com.paulvarry.intra42.utils.clusterMap.ClusterMap;
import com.paulvarry.intra42.utils.clusterMap.ClusterMapFremontE1Z1;
import com.paulvarry.intra42.utils.clusterMap.ClusterMapFremontE1Z2;
import com.paulvarry.intra42.utils.clusterMap.ClusterMapFremontE1Z3;
import com.paulvarry.intra42.utils.clusterMap.ClusterMapParis;
import com.paulvarry.intra42.utils.clusterMap.LocationItem;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClusterMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClusterMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterMapFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String clusterName;
    private ClusterMapActivity activity;
    private HashMap<String, UsersLTE> locations;
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
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clusterName = getArguments().getString(ARG_PARAM1);
        }
        activity = (ClusterMapActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cluster_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locations = activity.locations;
        gridLayout = (GridLayout) view.findViewById(R.id.gridLayout);
        makeMap();

    }

    void makeMap() {

        final LocationItem[][] cluster;

        if (activity.campusId == 1)
            cluster = ClusterMapParis.getParisCluster(clusterName);
        else if (activity.campusId == 7) {
            if (clusterName.contentEquals("e1z1"))
                cluster = ClusterMapFremontE1Z1.getFremontCluster1Zone1();
            else if (clusterName.contentEquals("e1z2"))
                cluster = ClusterMapFremontE1Z2.getFremontCluster1Zone2();
            else if (clusterName.contentEquals("e1z3"))
                cluster = ClusterMapFremontE1Z3.getFremontCluster1Zone3();
            else
                cluster = ClusterMap.getFremontCluster(clusterName);
        } else
            return;

        gridLayout.removeAllViews();
        gridLayout.removeAllViewsInLayout();
        gridLayout.setRowCount(cluster.length);

        for (int r = 0; r < cluster.length; r++) {

            gridLayout.setColumnCount(cluster[r].length);
            for (int p = 0; p < cluster[r].length; p++) {

                if (cluster[r][p] == null)
                    break;

                Log.d("pos", String.valueOf(r) + " " + String.valueOf(p));
                View v = makeMapItem(cluster, r, p);
                gridLayout.addView(v);
            }
        }
    }

    View makeMapItem(final LocationItem[][] cluster, int r, int p) {

        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = vi.inflate(R.layout.grid_layout_cluster_map, gridLayout, false);

        ImageView imageViewContent = (ImageView) view.findViewById(R.id.imageView);
        if (cluster[r][p].kind == LocationItem.KIND_USER) {

            if (cluster[r][p].locationName.contains("null") || cluster[r][p].locationName.contains("TBD"))
                imageViewContent.setImageResource(R.drawable.ic_close_black_24dp);
            else {
                final int finalR = r;
                final int finalP = p;
                imageViewContent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LocationHistoryActivity.openIt(activity, cluster[finalR][finalP].locationName);
                        return true;
                    }
                });
                if (locations != null && locations.containsKey(cluster[r][p].locationName)) {
                    final UsersLTE user = locations.get(cluster[r][p].locationName);
                    UserImage.setImageSmall(getContext(), user, imageViewContent);
                    imageViewContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UserActivity.openIt(activity, user);
                        }
                    });
                } else {
                    imageViewContent.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_desktop_mac_black_24dp));
                    imageViewContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(activity, cluster[finalR][finalP].locationName, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        } else if (cluster[r][p].kind == LocationItem.KIND_WALL)
            imageViewContent.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorClusterMapWall));
        else {
            imageViewContent.setImageResource(R.drawable.ic_add_black_24dp);
            imageViewContent.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.CLEAR);
        }

        FrameLayout.LayoutParams paramsFrameLayout = (FrameLayout.LayoutParams) imageViewContent.getLayoutParams();
        paramsFrameLayout.height = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsFrameLayout.width = GridLayout.LayoutParams.WRAP_CONTENT;
        imageViewContent.setLayoutParams(paramsFrameLayout);
//        imageViewContent.setMaxHeight((int) (100 * cluster[r][p].sizeY));
//        imageViewContent.setMaxWidth((int) (100 * cluster[r][p].sizeX));

        GridLayout.LayoutParams paramsGridLayout = (GridLayout.LayoutParams) view.getLayoutParams();
        paramsGridLayout.height = (int) (100 * cluster[r][p].sizeY);
        paramsGridLayout.width = (int) (100 * cluster[r][p].sizeX);
        paramsGridLayout.rightMargin = 5;
        paramsGridLayout.topMargin = 5;
        paramsGridLayout.setGravity(Gravity.FILL);
        paramsGridLayout.columnSpec = GridLayout.spec(p);
        paramsGridLayout.rowSpec = GridLayout.spec(r);

        view.setLayoutParams(paramsGridLayout);
//                imageViewContent.setRotation(10);

        view.setPadding(1, 1, 1, 1);
        view.setBackgroundResource(R.color.colorAccent);

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
