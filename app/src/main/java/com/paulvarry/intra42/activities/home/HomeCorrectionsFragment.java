package com.paulvarry.intra42.activities.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.ListAdapterCorrections;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.ScaleTeams;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicFragmentCall;
import retrofit2.Call;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeCorrectionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeCorrectionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeCorrectionsFragment extends BasicFragmentCall<ScaleTeams, ListAdapterCorrections> {

    @Nullable
    private HomeActivity activity;
    private OnFragmentInteractionListener mListener;

    public HomeCorrectionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeCorrectionsFragment.
     */
    public static HomeCorrectionsFragment newInstance() {
        return new HomeCorrectionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (HomeActivity) getActivity();
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

    @Nullable
    @Override
    public Call<List<ScaleTeams>> getCall(ApiService apiService, int page) {
        return apiService.getScaleTeamsMe(page);
    }

    @Override
    public void onItemClick(ScaleTeams scaleTeams) {

        if (scaleTeams.corrector != null && activity != null && !scaleTeams.corrector.isMe(activity.app))
            UserActivity.openIt(activity, scaleTeams.corrector);
        else if (scaleTeams.teams != null && scaleTeams.teams.users != null && !scaleTeams.teams.users.isEmpty()) {
            boolean corrected = true;
            for (UsersLTE u : scaleTeams.teams.users)
                if (u.isMe(activity.app))
                    corrected = false;
            if (corrected)
                UserActivity.openIt(activity, scaleTeams.teams.getLeader());
        }
    }

    @Override
    public ListAdapterCorrections generateAdapter(List<ScaleTeams> list) {
        if (activity != null)
            return new ListAdapterCorrections(activity, list);
        return null;
    }

    @Override
    public String getEmptyMessage() {
        return getString(R.string.evaluation_user_nothing);
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
