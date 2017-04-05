package com.paulvarry.intra42.activity.project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterAttachments;
import com.paulvarry.intra42.Tools.Tools;
import com.paulvarry.intra42.api.model.Attachments;
import com.paulvarry.intra42.ui.BasicFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectAttachmentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectAttachmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectAttachmentsFragment extends BasicFragment<Attachments, ListAdapterAttachments> {

    private ProjectActivity activity;
    private OnFragmentInteractionListener mListener;

    public ProjectAttachmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectAttachmentsFragment.
     */
    public static ProjectAttachmentsFragment newInstance() {
        return new ProjectAttachmentsFragment();
    }

    @Override
    public List<Attachments> getData() {
        if (activity != null &&
                activity.projectUser != null &&
                activity.projectUser.project != null)
            return activity.projectUser.project.attachments;
        return null;
    }

    @Override
    public void onItemClick(Attachments item) {
        Tools.openAttachment(getActivity(), item);
    }

    @Override
    public ListAdapterAttachments generateAdapter(List<Attachments> list) {
        return new ListAdapterAttachments(getContext(), list);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ProjectActivity) getActivity();
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
