package com.paulvarry.intra42.tab.projects;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterMarks;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.ProjectsUsers;
import com.paulvarry.intra42.tab.project.ProjectActivity;
import com.paulvarry.intra42.ui.BasicFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectsDoingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectsDoingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectsDoingFragment extends BasicFragment<ProjectsUsers, ListAdapterMarks> {

    private OnFragmentInteractionListener mListener;

    public ProjectsDoingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectsDoingFragment.
     */
    public static ProjectsDoingFragment newInstance() {
        return new ProjectsDoingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public List<ProjectsUsers> getData() {
        AppClass app = (AppClass) getActivity().getApplication();
        return ProjectsUsers.getListDoing(app.me.projectsUsers);
    }

    @Override
    public void onItemClick(ProjectsUsers item) {
        ProjectActivity.openIt(getContext(), item);
    }

    @Override
    public ListAdapterMarks generateAdapter(List<ProjectsUsers> list) {
        return new ListAdapterMarks(getActivity(), list);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
