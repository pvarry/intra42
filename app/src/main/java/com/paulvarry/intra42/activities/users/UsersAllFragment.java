package com.paulvarry.intra42.activities.users;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.GridAdapterUsers;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicFragmentCallGrid;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Pagination;
import retrofit2.Call;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersAllFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersAllFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersAllFragment extends BasicFragmentCallGrid<UsersLTE, GridAdapterUsers> {

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public Call<List<UsersLTE>> getCall(ApiService apiService, @Nullable List<UsersLTE> list) {

        int campus = AppSettings.getAppCampus((AppClass) getActivity().getApplication());

        if (campus != -1 && campus != 0)
            return apiService.getUsersCampus(campus, Pagination.getPage(list));
        else
            return apiService.getUsers(Pagination.getPage(list));

    }

    @Override
    public void onItemClick(UsersLTE item) {
        UserActivity.openIt(getActivity(), item);
    }

    @Override
    public boolean onItemLongClick(UsersLTE item) {
        return false;
    }

    @Override
    public GridAdapterUsers generateAdapter(List<UsersLTE> list) {
        return new GridAdapterUsers(getContext(), list);
    }

    @Override
    public String getEmptyMessage() {
        return null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        UsersActivity activity = (UsersActivity) getActivity();

        if (activity != null && activity.menuItemFilter != null && false) {// it is not finish
            activity.menuItemFilter.setVisible(isVisibleToUser);
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
