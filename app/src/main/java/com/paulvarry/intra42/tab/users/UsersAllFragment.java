package com.paulvarry.intra42.tab.users;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.GridAdapterUsers;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.Tools.AppSettings;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.UserLTE;
import com.paulvarry.intra42.tab.user.UserActivity;
import com.paulvarry.intra42.ui.BasicFragmentCallGrid;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersAllFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersAllFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersAllFragment extends BasicFragmentCallGrid<UserLTE, GridAdapterUsers> {

    private OnFragmentInteractionListener mListener;

    public UsersAllFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UsersAllFragment.
     */
    public static UsersAllFragment newInstance() {
        return new UsersAllFragment();
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        UsersActivity activity = (UsersActivity) getActivity();

        if (activity != null && activity.menuItemFilter != null) {
            activity.menuItemFilter.setVisible(isVisibleToUser);
        }
    }

    @Nullable
    @Override
    public Call<List<UserLTE>> getCall(ApiService apiService, @Nullable List<UserLTE> list) {

        int campus = AppSettings.getUserCampus((AppClass) getActivity().getApplication());

        if (campus != -1 && campus != 0)
            return apiService.getUsersCampus(campus, Pagination.getPage(list));
        else
            return apiService.getUsers(Pagination.getPage(list));

    }

    @Override
    public void onItemClick(UserLTE item) {
        UserActivity.openIt(getContext(), item, getActivity());
    }

    @Override
    public boolean onItemLongClick(UserLTE item) {
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
