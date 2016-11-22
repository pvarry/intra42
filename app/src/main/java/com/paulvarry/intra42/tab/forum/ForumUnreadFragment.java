package com.paulvarry.intra42.tab.forum;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterTopics;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.activity.TopicActivity;
import com.paulvarry.intra42.api.Topics;
import com.paulvarry.intra42.ui.BasicFragmentCall;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ForumUnreadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForumUnreadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumUnreadFragment extends BasicFragmentCall<Topics, ListAdapterTopics> {

    private OnFragmentInteractionListener mListener;

    public ForumUnreadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ForumUnreadFragment.
     */
    public static ForumUnreadFragment newInstance() {
        return new ForumUnreadFragment();
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
        return apiService.getTopicsUnread(Pagination.getPage(list));
    }

    @Override
    public void onItemClick(Topics item) {
        TopicActivity.openIt(getContext(), item);
    }

    @Override
    public ListAdapterTopics generateAdapter(List<Topics> list) {
        return new ListAdapterTopics(getContext(), list);
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
