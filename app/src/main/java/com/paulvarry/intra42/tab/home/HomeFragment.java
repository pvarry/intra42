package com.paulvarry.intra42.tab.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.tab.user.UserActivity;
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

    private LinearLayout linearLayoutContent;
    private TextView textViewStatus;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView textViewWallet;
    private TextView textViewCP;
    private TextView textViewName;
    private ImageView imageViewProfile;
    private ProgressBar progressBarLevel;
    private TextView textViewLevel;
    private ImageButton imageButtonOpenProfile;

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
        app = (AppClass) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayoutContent = (LinearLayout) view.findViewById(R.id.linearLayoutContent);
        textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        textViewWallet = (TextView) view.findViewById(R.id.textViewWallet);
        textViewCP = (TextView) view.findViewById(R.id.textViewCP);
        textViewName = (TextView) view.findViewById(R.id.textViewName);
        imageViewProfile = (ImageView) view.findViewById(R.id.imageViewProfile);
        progressBarLevel = (ProgressBar) view.findViewById(R.id.progressBarLevel);
        textViewLevel = (TextView) view.findViewById(R.id.textViewLevel);
        imageButtonOpenProfile = (ImageButton) view.findViewById(R.id.imageButtonOpenProfile);

        linearLayoutContent.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);

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
        if (v == imageButtonOpenProfile)
            UserActivity.openIt(getContext(), app.me, activity);
    }

    public void getData(boolean forceApi) {
        if (app.me != null)
            app.initUser(forceApi);
    }

    public void setView() {
        if (app.me == null) {
            linearLayoutContent.setVisibility(View.GONE);
            textViewStatus.setVisibility(View.VISIBLE);
        } else {
            linearLayoutContent.setVisibility(View.VISIBLE);
            textViewStatus.setVisibility(View.GONE);
            imageButtonOpenProfile.setOnClickListener(fragment);
            textViewName.setText(app.me.displayName);
            textViewWallet.setText(String.valueOf(app.me.wallet));
            textViewCP.setText(String.valueOf(app.me.correction_point));
            progressBarLevel.setProgress((int) (app.me.cursusUsers.get(0).level / 21.0 * 100.0));
            textViewLevel.setText(String.valueOf(app.me.cursusUsers.get(0).level));

            RequestCreator p = UserImage.getPicassoCorned(app, app.me);
            if (p != null)
                p.into(imageViewProfile);
        }
        swipeRefreshLayout.setRefreshing(false);
        linearLayoutContent.setVisibility(View.VISIBLE);
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
