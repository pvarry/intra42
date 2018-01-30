package com.paulvarry.intra42.activities.project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ExpandableListAdapterTeams;
import com.paulvarry.intra42.api.model.ProjectsUsers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectUserFragment extends Fragment {
    ProjectActivity activity;
    ExpandableListView listView;
    TextView textView;
    private OnFragmentInteractionListener mListener;

    public ProjectUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectUserFragment.
     */
    public static ProjectUserFragment newInstance() {
        return new ProjectUserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ProjectActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView);
        textView = view.findViewById(R.id.textView);

        if (activity.projectUser != null && activity.projectUser.user.teams != null && activity.projectUser.user.teams.size() != 0) {
            ExpandableListAdapterTeams adapter = new ExpandableListAdapterTeams(getActivity(), activity.projectUser.user.teams);
            listView.setAdapter(adapter);
            listView.expandGroup(0);
            textView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            if (activity.projectUser != null && activity.projectUser.user.status == ProjectsUsers.Status.PARENT) {
                textView.setText(R.string.project_you_cant_have_mark_for_this_project);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
