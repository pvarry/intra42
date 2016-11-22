package com.paulvarry.intra42.tab.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.paulvarry.intra42.Adapter.ListAdapterSkills;
import com.paulvarry.intra42.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserSkillsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserSkillsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSkillsFragment extends Fragment {

    @Nullable
    private UserActivity activity;
    private OnFragmentInteractionListener mListener;

    public UserSkillsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserSkillsFragment.
     */
    public static UserSkillsFragment newInstance() {
        return new UserSkillsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_skills, container, false);

        ListView listViewSkills = (ListView) rootView.findViewById(R.id.listViewSkills);
        TextView textViewNothingToShow = (TextView) rootView.findViewById(R.id.textViewNothingToShow);

        if (activity != null && activity.userCursus != null && !activity.userCursus.skills.isEmpty()) {
            ListAdapterSkills adapterSkills = new ListAdapterSkills(getActivity(), activity.userCursus.skills);
            listViewSkills.setAdapter(adapterSkills);
            textViewNothingToShow.setVisibility(View.GONE);
            listViewSkills.setVisibility(View.VISIBLE);
        } else {
            textViewNothingToShow.setVisibility(View.VISIBLE);
            listViewSkills.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserActivity)
            activity = (UserActivity) context;

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
