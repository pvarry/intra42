package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterClusterMapInfo;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClusterMapInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClusterMapInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterMapInfoFragment extends Fragment {

    private ClusterMapActivity activity;

    private Spinner spinnerMain;
    private Spinner spinnerSecondary;
    private ExpandableHeightListView listView;

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListAdapterClusterMapInfo adapter = new ListAdapterClusterMapInfo(getContext(), activity.clusterInfoList);
        listView.setAdapter(adapter);
        listView.setExpanded(true);
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
