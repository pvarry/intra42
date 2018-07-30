package com.paulvarry.intra42.activities.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ItemDecoration;
import com.paulvarry.intra42.adapters.RecyclerAdapterUser;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserPatronagesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserPatronagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserPatronagesFragment extends Fragment implements RecyclerAdapterUser.OnItemClickListener {

    private ProgressBar progressBar;
    private ScrollView scrollView;
    private TextView textViewPatronNone;
    private RecyclerView listViewPatron;
    private TextView textViewPatroningNone;
    private RecyclerView listViewPatroning;

    @Nullable
    private UserActivity activity;
    private OnFragmentInteractionListener mListener;
    @Nullable
    private Users user;
    private SparseArray<UsersLTE> users;

    public UserPatronagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserSkillsFragment.
     */
    public static UserPatronagesFragment newInstance() {
        return new UserPatronagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_patronages, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);
        scrollView = rootView.findViewById(R.id.scrollView);
        textViewPatronNone = rootView.findViewById(R.id.textViewPatronNone);
        listViewPatron = rootView.findViewById(R.id.listViewPatron);
        textViewPatroningNone = rootView.findViewById(R.id.textViewPatroningNone);
        listViewPatroning = rootView.findViewById(R.id.listViewPatroning);

        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        activity = (UserActivity) getActivity();
        if (activity != null && activity.user != null) {
            user = activity.user;
        }

        List<Integer> userIds = new ArrayList<>();
        if (user != null) {
            if (user.patroned != null && !user.patroned.isEmpty()) {
                for (Users.Patron p : user.patroned)
                    userIds.add(p.godfatherId);
            }
            if (user.patroning != null && !user.patroning.isEmpty()) {
                for (Users.Patron p : user.patroning)
                    userIds.add(p.userId);
            }
        }

        AppClass app = (AppClass) activity.getApplication();
        Call<List<UsersLTE>> call = app.getApiService().getUsers(Tools.concatIds(userIds));
        call.enqueue(new Callback<List<UsersLTE>>() {
            @Override
            public void onResponse(Call<List<UsersLTE>> call, Response<List<UsersLTE>> response) {
                users = new SparseArray<>();
                List<UsersLTE> body = response.body();
                if (Tools.apiIsSuccessfulNoThrow(response) && body != null)
                    for (UsersLTE u : body)
                        users.append(u.id, u);
                setView();
            }

            @Override
            public void onFailure(Call<List<UsersLTE>> call, Throwable t) {
                users = new SparseArray<>();
                setView();
            }
        });

        return rootView;
    }

    void setView() {

        if (!isAdded())
            return;

        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 90);

        if (user != null && user.patroned != null && !user.patroned.isEmpty()) {

            UsersLTE t;
            List<UsersLTE> patroned = new ArrayList<>();
            for (Users.Patron p : user.patroned) {
                t = users.get(p.godfatherId);
                if (t != null)
                    patroned.add(t);
            }

            textViewPatronNone.setVisibility(View.GONE);
            listViewPatron.setVisibility(View.VISIBLE);
            RecyclerAdapterUser adapterUsers = new RecyclerAdapterUser(getContext(), patroned);
            listViewPatron.setAdapter(adapterUsers);
            listViewPatron.setLayoutManager(new GridLayoutManager(getContext(), noOfColumns, LinearLayoutManager.VERTICAL, false));
            if (listViewPatron.getItemDecorationCount() == 0 || listViewPatron.getItemDecorationAt(0) == null)
                listViewPatron.addItemDecoration(new ItemDecoration(getResources().getDimensionPixelSize(R.dimen.list_spacing), noOfColumns), 0);
            listViewPatron.setNestedScrollingEnabled(false);
            adapterUsers.setOnItemClickListener(this);
        } else {
            textViewPatronNone.setVisibility(View.VISIBLE);
            listViewPatron.setVisibility(View.GONE);
        }
        if (user != null && user.patroning != null && !user.patroning.isEmpty()) {

            UsersLTE t;
            List<UsersLTE> patroning = new ArrayList<>();
            for (Users.Patron p : user.patroning) {
                t = users.get(p.userId);
                if (t != null)
                    patroning.add(t);
            }

            textViewPatroningNone.setVisibility(View.GONE);
            listViewPatroning.setVisibility(View.VISIBLE);
            RecyclerAdapterUser adapterUsers = new RecyclerAdapterUser(getContext(), patroning);
            listViewPatroning.setAdapter(adapterUsers);
            listViewPatroning.setLayoutManager(new GridLayoutManager(getContext(), noOfColumns, LinearLayoutManager.VERTICAL, false));
            if (listViewPatron.getItemDecorationCount() == 0 || listViewPatron.getItemDecorationAt(0) == null)
                listViewPatroning.addItemDecoration(new ItemDecoration(getResources().getDimensionPixelSize(R.dimen.list_spacing), noOfColumns), 0);
            listViewPatroning.setNestedScrollingEnabled(false);
            adapterUsers.setOnItemClickListener(this);
        } else {
            textViewPatroningNone.setVisibility(View.VISIBLE);
            listViewPatroning.setVisibility(View.GONE);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserActivity)
            activity = (UserActivity) context;

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
    public void onItemUserClick(int position, UsersLTE users) {
        UserActivity.openIt(getContext(), users);
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
