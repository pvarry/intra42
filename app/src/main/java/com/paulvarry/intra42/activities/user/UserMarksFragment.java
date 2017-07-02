package com.paulvarry.intra42.activities.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.adapters.ListAdapterMarks;
import com.paulvarry.intra42.api.model.ProjectsUsers;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserMarksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserMarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserMarksFragment extends Fragment implements AdapterView.OnItemClickListener {

    public ListView listView;
    Spinner spinnerCursus;
    TextView textViewNoItem;
    UserActivity activity;
    ListAdapterMarks adapterListMarks;

    List<ProjectsUsers> list;

    private OnFragmentInteractionListener mListener;

    public UserMarksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserMarksFragment.
     */
    public static UserMarksFragment newInstance() {
        return new UserMarksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (UserActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_marks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCursus = view.findViewById(R.id.spinnerCursus);
        listView = view.findViewById(R.id.listViewMarks);
        textViewNoItem = view.findViewById(R.id.textViewNoItem);

        listView.setOnItemClickListener(this);

        if (activity == null || activity.user == null || activity.user.projectsUsers == null || activity.user.projectsUsers.isEmpty()) {
            textViewNoItem.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
            textViewNoItem.setVisibility(View.GONE);

            list = activity.user.projectsUsers;
            list = ProjectsUsers.getListOnlyRoot(list);
            if (activity.userCursus != null)
                list = ProjectsUsers.getListCursus(list, activity.userCursus.cursusId);

            adapterListMarks = new ListAdapterMarks(activity, list);
            listView.setAdapter(adapterListMarks);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProjectActivity.openIt(getContext(), list.get(position));
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
