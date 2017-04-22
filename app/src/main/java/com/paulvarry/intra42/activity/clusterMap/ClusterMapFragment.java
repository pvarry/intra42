package com.paulvarry.intra42.activity.clusterMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.Tools.clusterMap.ClusterMap;
import com.paulvarry.intra42.Tools.clusterMap.ClusterMapFremontE1Z1;
import com.paulvarry.intra42.Tools.clusterMap.ClusterMapFremontE1Z2;
import com.paulvarry.intra42.Tools.clusterMap.ClusterMapFremontE1Z3;
import com.paulvarry.intra42.Tools.clusterMap.ClusterMapParis;
import com.paulvarry.intra42.Tools.clusterMap.LocationItem;
import com.paulvarry.intra42.activity.user.UserActivity;
import com.paulvarry.intra42.api.model.UsersLTE;

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
        gridLayout.setRowCount(cluster.length);

        for (int r = 0; r < cluster.length; r++) {

            gridLayout.setColumnCount(cluster[r].length);
            for (int p = 0; p < cluster[r].length; p++) {

                if (cluster[r][p] == null)
                    break;

                ImageView imageViewContent = new ImageView(getContext());
                if (cluster[r][p].kind == LocationItem.KIND_USER) {

                    if (cluster[r][p].locationName.contains("null") || cluster[r][p].locationName.contains("TBD"))
                        imageViewContent.setImageResource(R.drawable.ic_close_black_24dp);
                    else
                        imageViewContent.setImageResource(R.drawable.ic_desktop_mac_black_24dp);

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
                        final int finalR = r;
                        final int finalP = p;
                        imageViewContent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(activity, cluster[finalR][finalP].locationName, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else if (cluster[r][p].kind == LocationItem.KIND_WALL)
                    imageViewContent.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorClusterMapWall));
                else {
                    imageViewContent.setImageResource(R.drawable.ic_add_black_24dp);
                    imageViewContent.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.CLEAR);
                }

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = (int) (100 * cluster[r][p].sizeY);
                param.width = (int) (100 * cluster[r][p].sizeX);
                param.rightMargin = 5;
                param.topMargin = 5;
                param.setGravity(Gravity.FILL);
                param.columnSpec = GridLayout.spec(p);
                param.rowSpec = GridLayout.spec(r);
                imageViewContent.setLayoutParams(param);
//                imageViewContent.setRotation(10);
                gridLayout.addView(imageViewContent);
            }
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
