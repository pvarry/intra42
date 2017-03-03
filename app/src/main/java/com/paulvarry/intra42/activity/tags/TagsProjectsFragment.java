package com.paulvarry.intra42.activity.tags;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterProjects;
import com.paulvarry.intra42.activity.project.ProjectActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.ui.BasicFragmentCall;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TagsProjectsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TagsProjectsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagsProjectsFragment extends BasicFragmentCall<Projects, ListAdapterProjects> {


    private OnFragmentInteractionListener mListener;

    public TagsProjectsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TagsProjectsFragment.
     */
    public static TagsProjectsFragment newInstance() {
        return new TagsProjectsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public Call<List<Projects>> getCall(ApiService apiService, @Nullable List<Projects> list) {
        return null;
    }

    @Override
    public void onItemClick(Projects item) {
        ProjectActivity.openIt(getContext(), item);
    }

    @Override
    public ListAdapterProjects generateAdapter(List<Projects> list) {
        return new ListAdapterProjects(getContext(), list);
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
