package com.paulvarry.intra42.activities.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.CoalitionsActivity;
import com.paulvarry.intra42.activities.FriendsActivity;
import com.paulvarry.intra42.activities.HolyGraphActivity;
import com.paulvarry.intra42.activities.MarvinMealsActivity;
import com.paulvarry.intra42.activities.TimeActivity;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.RequestCreator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ConstraintLayout constraintLayoutProfileHeader;
    private LinearLayout linearLayoutHolyGraph;
    private LinearLayout linearLayoutFriends;
    private LinearLayout linearLayoutCantinaMenu;
    private LinearLayout linearLayoutCoalitions;
    private LinearLayout linearLayoutClusterMap;
    private LinearLayout linearLayoutTimeOnCampus;
    private TextView textViewStatus;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewWallet;
    private TextView textViewCP;
    private TextView textViewName;
    private ImageView imageViewProfile;
    private ProgressBar progressBarLevel;
    private TextView textViewLevel;

    private HomeFragment fragment;
    private HomeActivity activity;
    private AppClass app;
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = this;
        activity = (HomeActivity) getActivity();
        if (activity != null)
            app = (AppClass) activity.getApplication();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        constraintLayoutProfileHeader = view.findViewById(R.id.constraintLayoutProfileHeader);
        linearLayoutHolyGraph = view.findViewById(R.id.linearLayoutHolyGraph);
        linearLayoutFriends = view.findViewById(R.id.linearLayoutFriends);
        linearLayoutCantinaMenu = view.findViewById(R.id.linearLayoutCantinaMenu);
        linearLayoutTimeOnCampus = view.findViewById(R.id.linearLayoutTimeOnCampus);
        linearLayoutClusterMap = view.findViewById(R.id.linearLayoutClusterMap);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        textViewWallet = view.findViewById(R.id.textViewWallet);
        textViewCP = view.findViewById(R.id.textViewCP);
        textViewName = view.findViewById(R.id.textViewName);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        progressBarLevel = view.findViewById(R.id.progressBarLevel);
        textViewLevel = view.findViewById(R.id.textViewLevel);
        linearLayoutCoalitions = view.findViewById(R.id.linearLayoutCoalitions);

        swipeRefreshLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (app.me != null && AppSettings.getAppCampus(app) != 7)
            linearLayoutCantinaMenu.setVisibility(View.GONE);
        else
            linearLayoutCantinaMenu.setVisibility(View.VISIBLE);

        setView();
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
        if (v == constraintLayoutProfileHeader)
            UserActivity.openIt(activity, app.me);
        else if (v == linearLayoutHolyGraph)
            HolyGraphActivity.openIt(getContext());
        else if (v == linearLayoutFriends)
            FriendsActivity.openIt(getContext());
        else if (v == linearLayoutTimeOnCampus)
            TimeActivity.openIt(getContext());
        else if (v == linearLayoutClusterMap)
            ClusterMapActivity.openIt(getContext());
        else if (v == linearLayoutCantinaMenu)
            MarvinMealsActivity.openIt(getContext());
        else if (v == linearLayoutCoalitions)
            CoalitionsActivity.openIt(getContext());
    }

    public void getData(boolean forceApi) {
        if (app.me != null)
            app.initCache(forceApi);
    }

    public void setView() {
        if (app.me == null) {
            swipeRefreshLayout.setVisibility(View.GONE);
            textViewStatus.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            textViewStatus.setVisibility(View.GONE);

            linearLayoutHolyGraph.setOnClickListener(this);
            linearLayoutFriends.setOnClickListener(this);
            linearLayoutTimeOnCampus.setOnClickListener(this);
            linearLayoutClusterMap.setOnClickListener(this);
            linearLayoutCantinaMenu.setOnClickListener(this);
            linearLayoutCoalitions.setOnClickListener(this);

            constraintLayoutProfileHeader.setOnClickListener(fragment);
            textViewName.setText(app.me.displayName);
            textViewWallet.setText(String.valueOf(app.me.wallet));
            textViewCP.setText(String.valueOf(app.me.correction_point));

            if (app.me.cursusUsers != null && app.me.cursusUsers.size() != 0) {
                progressBarLevel.setVisibility(View.VISIBLE);
                textViewLevel.setVisibility(View.VISIBLE);

                int cursus = AppSettings.getUserCursus(app);
                CursusUsers mainCursus = null;
                CursusUsers cursus42 = null;

                for (CursusUsers c : app.me.cursusUsers) {
                    if (c.cursusId == cursus)
                        mainCursus = c;
                    if (c.cursus.id == 1) {
                        cursus42 = c;
                    }
                }
                if (mainCursus == null && cursus42 != null)
                    mainCursus = cursus42;
                if (mainCursus == null)
                    mainCursus = app.me.cursusUsers.get(0);

                progressBarLevel.setProgress((int) (mainCursus.level / 21.0 * 100.0));
                textViewLevel.setText(String.valueOf(mainCursus.level));

            } else {
                progressBarLevel.setVisibility(View.GONE);
                textViewLevel.setVisibility(View.GONE);
            }

            RequestCreator picassoCorned = UserImage.getPicassoCorned(app, app.me);
            if (picassoCorned != null)
                picassoCorned.into(imageViewProfile);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData(true);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded())
                            setView();
                    }
                });
            }
        }).start();
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
