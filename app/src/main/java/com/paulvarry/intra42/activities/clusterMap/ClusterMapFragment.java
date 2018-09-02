package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.LocationHistoryActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Location;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.clusterMap.ClusterData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClusterMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClusterMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterMapFragment extends Fragment implements ClusterMapAdapter.onLocationClickListener {
    private static final String ARG_HOST_PREFIX = "hostPrefix";

    private String clusterName;
    private ClusterMapActivity activity;
    private ClusterData clusters;

    //    private ViewGroup viewGroupMain;
    private RecyclerView recyclerView;
    private TextView textViewEmpty;

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
        recyclerView = view.findViewById(R.id.recyclerView);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clusters = activity.clusterData;
        Cluster clusterInfo = activity.clusterData.getCluster(clusterName);

        if (clusterInfo == null || clusterInfo.map == null || clusterInfo.map.length == 0 || clusterInfo.sizeY == 0 || clusterInfo.sizeX == 0) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);

            ClusterMapAdapter adapter = new ClusterMapAdapter(getContext(), clusterInfo, clusters);
            adapter.setOnLocationClickListener(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), clusterInfo.sizeX, LinearLayoutManager.VERTICAL, false));
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

    @Override
    public void onLocationClicked(Location location) {
        UsersLTE user = null;
        if (location == null || location.kind != Location.Kind.USER)
            return;

        if (clusters.locations != null)
            user = clusters.locations.get(location.host);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
