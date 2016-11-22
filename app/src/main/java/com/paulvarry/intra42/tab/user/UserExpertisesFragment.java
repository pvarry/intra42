package com.paulvarry.intra42.tab.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterExpertises;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.ExpertisesUsers;
import com.paulvarry.intra42.ui.BasicFragmentCall;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserExpertisesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserExpertisesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserExpertisesFragment extends BasicFragmentCall<ExpertisesUsers, ListAdapterExpertises> {

    @Nullable
    private
    UserActivity activity;

    private OnFragmentInteractionListener mListener;

    public UserExpertisesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserExpertisesFragment.
     */
    public static UserExpertisesFragment newInstance() {
        return new UserExpertisesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (UserActivity) getActivity();
    }

    @Nullable
    @Override
    public Call<List<ExpertisesUsers>> getCall(ApiService apiService, @Nullable List<ExpertisesUsers> list) {
        if (activity != null && activity.user != null) {
            return apiService.getUserExpertises(activity.user.id, Pagination.getPage(list));
        }
        return null;
    }

    @Override
    public void onItemClick(ExpertisesUsers item) {

    }

    @Override
    public ListAdapterExpertises generateAdapter(List<ExpertisesUsers> list) {
        return new ListAdapterExpertises(getContext(), list);
    }

    @Override
    public String getEmptyMessage() {
        String login = activity.login;
        if (((AppClass) activity.getApplication()).me.id == activity.user.id)
            login = getString(R.string.you);
        return String.format(getString(R.string.format_dont_have_any_expertises_yet), login);
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
