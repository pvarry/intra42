package com.paulvarry.intra42.activity.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterTopics;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.activity.TopicActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Topics;
import com.paulvarry.intra42.ui.BasicFragmentCall;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserForumFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserForumFragment extends BasicFragmentCall<Topics, ListAdapterTopics> {

    @Nullable
    private UserActivity activity;
    private OnFragmentInteractionListener mListener;

    public UserForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserForumFragment.
     */
    public static UserForumFragment newInstance() {
        return new UserForumFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (UserActivity) getActivity();

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
    public Call<List<Topics>> getCall(ApiService apiService, @Nullable List<Topics> list) {
        return apiService.getUserTopics(activity.login, Pagination.getPage(list));
    }

    @Override
    public void onItemClick(Topics item) {
        TopicActivity.openIt(getActivity(), item);
    }

    @Override
    public ListAdapterTopics generateAdapter(List<Topics> list) {
        return new ListAdapterTopics(getContext(), list);
    }

    @Override
    public String getEmptyMessage() {
        if (!isAdded())
            return null;
        AppClass app = (AppClass) activity.getApplication();
        String user = getString(R.string.you);
        if (activity != null && activity.user != null && (app == null || app.me == null || !app.me.equals(activity.user)))
            user = activity.user.login;
        return String.format(getString(R.string.format__dont_have_write_topic), user);
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
