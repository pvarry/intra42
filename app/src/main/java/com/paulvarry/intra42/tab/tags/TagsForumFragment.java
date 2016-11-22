package com.paulvarry.intra42.tab.tags;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.paulvarry.intra42.Adapter.ListAdapterTopics;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.activity.TopicActivity;
import com.paulvarry.intra42.api.Topics;
import com.paulvarry.intra42.ui.BasicFragmentCall;

import java.util.List;

import retrofit2.Call;

public class TagsForumFragment extends BasicFragmentCall<Topics, ListAdapterTopics> {

    private OnFragmentInteractionListener mListener;

    public TagsForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TagsForumFragment.
     */
    public static TagsForumFragment newInstance() {
        return new TagsForumFragment();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TagsForumFragment.OnFragmentInteractionListener) {
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
        TagsActivity activity = (TagsActivity) getActivity();

        return apiService.getTopicsTag(activity.tag.id, Pagination.getPage(list));
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
