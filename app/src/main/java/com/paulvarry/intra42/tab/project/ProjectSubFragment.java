package com.paulvarry.intra42.tab.project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterProjectsLTE;
import com.paulvarry.intra42.api.ProjectsLTE;
import com.paulvarry.intra42.ui.BasicFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectSubFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectSubFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectSubFragment extends BasicFragment<ProjectsLTE, ListAdapterProjectsLTE> {

    private ProjectActivity activity;

    private OnFragmentInteractionListener mListener;

    public ProjectSubFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectSubFragment.
     */
    public static ProjectSubFragment newInstance() {
        return new ProjectSubFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ProjectActivity) getActivity();
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
    public List<ProjectsLTE> getData() {
        return activity.projectUser.project.children;
    }

    @Override
    public void onItemClick(ProjectsLTE item) {
        if (activity.projectUser != null) {
            if (activity.projectUser.user != null && activity.projectUser.user.user != null)
                ProjectActivity.openIt(getContext(), item, activity.projectUser.user.user.id);
            else
                ProjectActivity.openIt(getContext(), item);
        }
    }

    @Override
    public ListAdapterProjectsLTE generateAdapter(List<ProjectsLTE> list) {
        return new ListAdapterProjectsLTE(activity, list);
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
