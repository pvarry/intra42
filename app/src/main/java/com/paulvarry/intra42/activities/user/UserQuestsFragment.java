package com.paulvarry.intra42.activities.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterQuests;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Quests;
import com.paulvarry.intra42.api.model.QuestsUsers;
import com.paulvarry.intra42.ui.BasicFragmentCall;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserQuestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserQuestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserQuestsFragment extends BasicFragmentCall<QuestsUsers, ListAdapterQuests> {

    private UserActivity activity;

    private OnFragmentInteractionListener mListener;

    public UserQuestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserProjectsFragment.
     */
    public static UserQuestsFragment newInstance() {
        return new UserQuestsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (UserActivity) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        int padding = Math.round(getResources().getDimension(R.dimen.card_spacing_half));
        listView.setPadding(0, padding, 0, padding);
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
    public Call<List<QuestsUsers>> getCall(ApiService apiService, int page) {
        return apiService.getUsersQuests(activity.login);
    }

    @Override
    public String getEmptyMessage() {
        return null;
    }

    @Override
    public ListAdapterQuests generateAdapter(List<QuestsUsers> source) {
        SparseArray<List<QuestsUsers>> data = new SparseArray<>();

        for (QuestsUsers item : source) {
            if (item.quest.kind != Quests.QuestsKind.MANDATORY)
                continue;

            if (data.get(item.quest.id) == null)
                data.put(item.quest.id, new ArrayList<QuestsUsers>());

            data.get(item.quest.id).add(item);
        }

        return new ListAdapterQuests(getContext(), data);
    }

    @Override
    public void onItemClick(QuestsUsers item) {

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
