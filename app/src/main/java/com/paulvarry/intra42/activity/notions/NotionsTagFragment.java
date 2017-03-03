package com.paulvarry.intra42.activity.notions;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterNotions;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.activity.SubnotionListActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Notions;
import com.paulvarry.intra42.api.model.Tags;
import com.paulvarry.intra42.ui.BasicFragmentCallTag;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotionsTagFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotionsTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotionsTagFragment extends BasicFragmentCallTag<Notions, ListAdapterNotions> {

    private OnFragmentInteractionListener mListener;

    public NotionsTagFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ForumTagFragment.
     */
    public static NotionsTagFragment newInstance() {
        return new NotionsTagFragment();
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
    public Call<List<Notions>> getCall(ApiService apiService, Tags tags, @Nullable List<Notions> list) {
        return apiService.getNotionsTag(tags.id, Pagination.getPage(list));
    }

    @Override
    public void onItemClick(Notions item) {
        SubnotionListActivity.openIt(getContext(), item);
    }

    @Override
    public ListAdapterNotions generateAdapter(List<Notions> list) {
        return new ListAdapterNotions(getContext(), list);
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
