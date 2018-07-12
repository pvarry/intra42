package com.paulvarry.intra42.activities.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.ExpertiseEditActivity;
import com.paulvarry.intra42.adapters.ListAdapterExpertises;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.ExpertiseUsers;
import com.paulvarry.intra42.ui.BasicFragmentCall;
import retrofit2.Call;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserExpertisesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserExpertisesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserExpertisesFragment extends BasicFragmentCall<ExpertiseUsers, ListAdapterExpertises> implements View.OnClickListener {

    @Nullable
    private
    UserActivity activity;

    private OnFragmentInteractionListener mListener;

    public UserExpertisesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserExpertisesFragment.
     */
    public static UserExpertisesFragment newInstance() {
        return new UserExpertisesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (UserActivity) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activity != null) {
            AppClass app = (AppClass) activity.getApplication();
            if (app.me != null && app.me.equals(activity.user)) {
                fabBasicFragmentCall.setVisibility(View.VISIBLE);
                fabBasicFragmentCall.setOnClickListener(this);
                fabBasicFragmentCall.setImageResource(R.drawable.ic_add_black_24dp);
            } else
                fabBasicFragmentCall.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public Call<List<ExpertiseUsers>> getCall(ApiService apiService, int page) {
        if (activity != null && activity.user != null) {
            return apiService.getUserExpertises(activity.user.id, page);
        }
        return null;
    }

    @Override
    public void onItemClick(ExpertiseUsers item) {
        ExpertiseEditActivity.open(getContext(), item);
    }

    @Override
    public ListAdapterExpertises generateAdapter(List<ExpertiseUsers> list) {
        return new ListAdapterExpertises(getContext(), list);
    }

    @Override
    public String getEmptyMessage() {
        if (activity == null)
            return null;

        AppClass app = (AppClass) activity.getApplication();

        if (app.me != null && activity.user != null && app.me.id == activity.user.id)
            return getString(R.string.user_you_dont_have_any_expertise_yet);
        String login = activity.login;
        return String.format(getString(R.string.format_dont_have_any_expertise_yet), login);
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
    public void onClick(View v) {
        if (v == fabBasicFragmentCall) {
            ExpertiseEditActivity.open(getContext());
        }
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
