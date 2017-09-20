package com.paulvarry.intra42.activities.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.GridAdapterAchievements;
import com.paulvarry.intra42.api.model.Achievements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserAchievementsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserAchievementsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserAchievementsFragment extends Fragment {

    UserActivity activity;
    GridView gridView;
    TextView textViewStatus;
    private OnFragmentInteractionListener mListener;

    public UserAchievementsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserAchievementsFragment.
     */
    public static UserAchievementsFragment newInstance() {
        return new UserAchievementsFragment();
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
        return inflater.inflate(R.layout.fragment_user_achievements, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = view.findViewById(R.id.gridView);
        textViewStatus = view.findViewById(R.id.textViewStatus);

        if (activity.user == null || activity.user.achievements == null || activity.user.achievements.size() == 0) {
            textViewStatus.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            textViewStatus.setText(R.string.user_no_achievements);
        } else {
            textViewStatus.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);

            if (activity.picAchievements == null)
                activity.picAchievements = new HashMap<>();

            List<Achievements> achievements = new ArrayList<>();
            Achievements last = null;

            for (Achievements a : activity.user.achievements) {
                if (last == null || last.name.contentEquals(a.name))
                    last = a;
                else {
                    achievements.add(last);
                    last = a;
                }
            }
            achievements.add(last);
            GridAdapterAchievements adapterAchievements = new GridAdapterAchievements(activity, achievements);
            gridView.setAdapter(adapterAchievements);
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
