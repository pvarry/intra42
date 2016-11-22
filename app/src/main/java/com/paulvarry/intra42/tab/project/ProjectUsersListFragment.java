package com.paulvarry.intra42.tab.project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.GridAdapterUsers;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.UserLTE;
import com.paulvarry.intra42.tab.user.UserActivity;
import com.paulvarry.intra42.ui.BasicFragmentCallGrid;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectUsersListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectUsersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectUsersListFragment extends BasicFragmentCallGrid<UserLTE, GridAdapterUsers> {

    @Nullable
    private ProjectActivity activity;
    private OnFragmentInteractionListener mListener;

    public ProjectUsersListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectUsersListFragment.
     */
    public static ProjectUsersListFragment newInstance() {
        return new ProjectUsersListFragment();
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

    @Nullable
    @Override
    public Call<List<UserLTE>> getCall(ApiService apiService, @Nullable List<UserLTE> list) {
        return apiService.getProjectUsers(activity.projectUser.project.id, Pagination.getPage(list));
    }

    @Override
    public void onItemClick(UserLTE item) {
        UserActivity.openIt(getContext(), item, getActivity());
    }

    @Override
    public boolean onItemLongClick(UserLTE item) {
        if (activity != null) {
            ProjectActivity.openIt(getContext(), activity.projectUser.project, item.id);
            return true;
        }
        return false;
    }

    @Override
    public GridAdapterUsers generateAdapter(List<UserLTE> list) {
        return new GridAdapterUsers(getContext(), list);
    }

    @Override
    public String getEmptyMessage() {
        return null;
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
