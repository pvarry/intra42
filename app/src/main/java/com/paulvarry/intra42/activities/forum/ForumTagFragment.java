package com.paulvarry.intra42.activities.forum;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.paulvarry.intra42.activities.TopicActivity;
import com.paulvarry.intra42.adapters.ListAdapterTopics;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Tags;
import com.paulvarry.intra42.api.model.Topics;
import com.paulvarry.intra42.ui.BasicFragmentCallTag;
import com.paulvarry.intra42.utils.Pagination;
import retrofit2.Call;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ForumTagFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForumTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumTagFragment extends BasicFragmentCallTag<Topics, ListAdapterTopics> {

    private OnFragmentInteractionListener mListener;

    public ForumTagFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ForumTagFragment.
     */
    public static ForumTagFragment newInstance() {
        return new ForumTagFragment();
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
    public Call<List<Topics>> getCall(ApiService apiService, Tags tag, @Nullable List<Topics> list) {
        return apiService.getTopicsTag(tag.id, Pagination.getPage(list));
    }

    @Override
    public void onItemClick(Topics item) {
        TopicActivity.openIt(getContext(), item);
    }

    @Nullable
    @Override
    public ListAdapterTopics generateAdapter(List<Topics> list) {
        return new ListAdapterTopics(getContext(), list);
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
