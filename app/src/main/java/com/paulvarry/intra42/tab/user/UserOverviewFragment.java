package com.paulvarry.intra42.tab.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.paulvarry.intra42.Adapter.SpinnerAdapterCursusAccent;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.AppSettings;
import com.paulvarry.intra42.Tools.DateTool;
import com.paulvarry.intra42.Tools.Friends;
import com.paulvarry.intra42.Tools.Tag;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.api.Campus;
import com.paulvarry.intra42.api.Locations;
import com.paulvarry.intra42.api.User;
import com.plumillonforge.android.chipview.ChipView;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paulvarry.intra42.R.string.error;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserOverviewFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    @Nullable
    UserActivity activity;
    User user;

    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout linearLayoutMobile;
    LinearLayout linearLayoutPhone;
    ImageButton imageButtonSMS;
    LinearLayout relativeLayoutMail;
    LinearLayout linearLayoutLocation;
    ImageView imageViewProfile;
    ImageButton imageButtonFriends;
    ChipView chipViewTags;
    TextView textViewName;
    TextView textViewMobile;
    TextView textViewMail;
    TextView textViewPosition;
    TextView textViewWallet;
    TextView textViewCorrectionPoints;
    TextView textViewPiscine;
    View linePool;
    LinearLayout linearLayoutPool;
    Spinner spinnerCursus;
    LinearLayout linearLayoutGrade;
    View viewSeparatorGrade;
    TextView textViewGrade;
    TextView textViewLvl;
    ProgressBar progressBarLevel;
    TextView textViewNoCursusAvailable;
    LinearLayout linearLayoutCursus;

    AppClass app;

    ValueEventListener friendsEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
            };
            HashMap<String, String> messages = snapshot.getValue(t);
            if (messages == null || user == null) {
                Log.d("fire", "no");
            } else {
                Set<String> s = messages.keySet();
                for (String k : s) {
                    if (user.id == Integer.decode(k)) {
                        setButtonFriends(true);
                        return;
                    }
                }
            }
            setButtonFriends(false);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w("fire", "Failed to read value.", error.toException());
        }
    };

    private OnFragmentInteractionListener mListener;

    public UserOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserOverviewFragment.
     */
    public static UserOverviewFragment newInstance() {

        return new UserOverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (UserActivity) getActivity();
        user = activity.user;

        app = (AppClass) activity.getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_overview, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        linearLayoutMobile = (LinearLayout) view.findViewById(R.id.linearLayoutMobile);
        linearLayoutPhone = (LinearLayout) view.findViewById(R.id.linearLayoutPhone);
        imageButtonSMS = (ImageButton) view.findViewById(R.id.imageButtonSMS);
        relativeLayoutMail = (LinearLayout) view.findViewById(R.id.linearLayoutMail);
        linearLayoutLocation = (LinearLayout) view.findViewById(R.id.linearLayoutLocation);
        imageViewProfile = (ImageView) view.findViewById(R.id.imageViewProfile);
        imageButtonFriends = (ImageButton) view.findViewById(R.id.imageButtonFriends);
        chipViewTags = (ChipView) view.findViewById(R.id.chipViewTags);

        textViewName = (TextView) view.findViewById(R.id.textViewName);
        textViewMobile = (TextView) view.findViewById(R.id.textViewMobile);
        textViewMail = (TextView) view.findViewById(R.id.textViewMail);
        textViewPosition = (TextView) view.findViewById(R.id.textViewPosition);
        textViewWallet = (TextView) view.findViewById(R.id.textViewWallet);
        textViewCorrectionPoints = (TextView) view.findViewById(R.id.textViewCorrectionPoints);
        linePool = view.findViewById(R.id.viewPool);
        linearLayoutPool = (LinearLayout) view.findViewById(R.id.linearLayoutPool);
        textViewPiscine = (TextView) view.findViewById(R.id.textViewPiscine);

        linearLayoutCursus = (LinearLayout) view.findViewById(R.id.linearLayoutCursus);
        textViewNoCursusAvailable = (TextView) view.findViewById(R.id.textViewNoCursusAvailable);
        spinnerCursus = (Spinner) view.findViewById(R.id.spinnerCursus);
        linearLayoutGrade = (LinearLayout) view.findViewById(R.id.linearLayoutGrade);
        viewSeparatorGrade = view.findViewById(R.id.viewSeparatorGrade);
        textViewGrade = (TextView) view.findViewById(R.id.textViewGrade);
        textViewLvl = (TextView) view.findViewById(R.id.textViewLvl);
        progressBarLevel = (ProgressBar) view.findViewById(R.id.progressBarLevel);

        setView();

        return view;
    }

    void setView() {

        if (activity == null || activity.user == null)
            return;
        user = activity.user;

        swipeRefreshLayout.setOnRefreshListener(this);

        if (AppSettings.Advanced.getAllowFriends(getContext())) {
            imageButtonFriends.setOnClickListener(this);
            setButtonFriends(null);

            if (app.firebaseRefFriends != null) {
                app.firebaseRefFriends.removeEventListener(friendsEventListener);
                app.firebaseRefFriends.addValueEventListener(friendsEventListener);
            }
        } else
            imageButtonFriends.setVisibility(View.GONE);

        String name = user.displayName + " - " + user.login;
        textViewName.setText(name);
        if (user.phone == null || user.phone.isEmpty())
            linearLayoutMobile.setVisibility(View.GONE);
        else
            textViewMobile.setText(user.phone);
        textViewMail.setText(user.email);

        String strLocation;
        if (user.location != null) {
            strLocation = user.location;
        } else
            strLocation = getResources().getString(R.string.unavailable);
        if (user.campus != null && !user.campus.isEmpty()) {
            strLocation += " - ";
            String sep = "";
            for (Campus c : user.campus) {
                strLocation += sep + c.name;
                sep = " | ";
            }
        }
        textViewPosition.setText(strLocation);

//        Toast t = Toast.makeText(getContext(), "ici", Toast.LENGTH_SHORT);
//        t.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
//        t.show();
        textViewWallet.setText(String.valueOf(user.wallet));
        textViewCorrectionPoints.setText(String.valueOf(user.correction_point));

        String pool = "";
        if (user.pool_month != null)
            pool += user.pool_month.substring(0, 1).toUpperCase() + user.pool_month.substring(1) + " - ";
        if (user.pool_year != null)
            pool += user.pool_year;
        if (user.pool_year != null || user.pool_month != null) {
            textViewPiscine.setText(pool);
            linearLayoutPool.setVisibility(View.VISIBLE);
            linePool.setVisibility(View.VISIBLE);
        } else {
            linearLayoutPool.setVisibility(View.GONE);
            linePool.setVisibility(View.GONE);
        }

        linearLayoutPhone.setOnClickListener(this);
        imageButtonSMS.setOnClickListener(this);
        relativeLayoutMail.setOnClickListener(this);
        linearLayoutLocation.setOnClickListener(this);

        if (user.cursusUsers != null && user.cursusUsers.size() != 0) {
            linearLayoutCursus.setVisibility(View.VISIBLE);
            textViewNoCursusAvailable.setVisibility(View.GONE);
            SpinnerAdapterCursusAccent adapterUserCursus = new SpinnerAdapterCursusAccent(getActivity(), user.cursusUsers);
            spinnerCursus.setAdapter(adapterUserCursus);
            spinnerCursus.setOnItemSelectedListener(this);
            for (int i = 0; i < user.cursusUsers.size(); i++) {
                if (user.cursusUsers.get(i).cursus.id == 1)
                    spinnerCursus.setSelection(i, false);
            }
        } else {
            linearLayoutCursus.setVisibility(View.GONE);
            textViewNoCursusAvailable.setVisibility(View.VISIBLE);
        }

        UserImage.setImage(getContext(), user, imageViewProfile);
        Tag.setTagUsers(getContext(), user.groups, chipViewTags);

        swipeRefreshLayout.setRefreshing(false);
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
    public void onResume() {
        super.onResume();
        if (AppSettings.Advanced.getAllowFriends(getContext()) && app.firebaseRefFriends != null)
            app.firebaseRefFriends.addValueEventListener(friendsEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (AppSettings.Advanced.getAllowFriends(getContext()) && app.firebaseRefFriends != null)
            app.firebaseRefFriends.removeEventListener(friendsEventListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v == linearLayoutPhone) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + user.phone));
            getActivity().startActivity(intent);
        } else if (v == imageButtonSMS) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + user.phone));
            startActivity(sendIntent);
        } else if (v == relativeLayoutMail) {
            Intent testIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?to=" + user.email);
            testIntent.setData(data);
            startActivity(testIntent);
        } else if (v == linearLayoutLocation) {
            if (user.location == null)
                seeLastLocation();
        } else if (v == imageButtonFriends) {
            Friends.actionAddRemove(app.firebaseRefFriends, user);
            setButtonFriends(null);
        }
    }

    void setButtonFriends(Boolean friendsFound) {

        if (friendsFound == null)
            imageButtonFriends.setColorFilter(Color.argb(200, 150, 150, 150));
        else if (friendsFound)
            imageButtonFriends.setColorFilter(Color.argb(255, 247, 202, 24));
        else
            imageButtonFriends.setColorFilter(Color.argb(255, 255, 255, 255));

    }

    void seeLastLocation() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater(null);
        View dialogView = inflater.inflate(R.layout.alert_last_location, null);
        dialogBuilder.setView(dialogView);

        final LinearLayout linearLayoutLoadingData = (LinearLayout) dialogView.findViewById(R.id.layoutGetData);
        final LinearLayout linearLayoutLocation = (LinearLayout) dialogView.findViewById(R.id.layoutLocation);
        final TextView textViewLocation = (TextView) dialogView.findViewById(R.id.textViewLocation);
        final TextView textViewDate = (TextView) dialogView.findViewById(R.id.textViewDate);

        linearLayoutLoadingData.setVisibility(View.VISIBLE);
        linearLayoutLocation.setVisibility(View.GONE);

        String lastLocationOf = "Last location of " + user.login;
        dialogBuilder.setTitle(lastLocationOf);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        activity.app.getApiService().getLastLocations(user.login).enqueue(new Callback<List<Locations>>() {
            @Override
            public void onResponse(Call<List<Locations>> call, Response<List<Locations>> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    Locations location = response.body().get(0);
                    linearLayoutLoadingData.setVisibility(View.GONE);
                    linearLayoutLocation.setVisibility(View.VISIBLE);
                    textViewLocation.setText(location.host);
                    if (location.endAt != null)
                        textViewDate.setText(DateTool.getDateTimeLong(location.endAt));
                    else
                        textViewDate.setVisibility(View.GONE);
                } else if (response.isSuccessful()) {
                    Toast.makeText(getContext(), R.string.nothing_found, Toast.LENGTH_SHORT).show();
                    alertDialog.cancel();
                } else {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    alertDialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<List<Locations>> call, Throwable t) {

            }
        });
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.userCursus = user.cursusUsers.get(position);
        if (activity.userCursus.grade == null) {
            linearLayoutGrade.setVisibility(View.GONE);
            viewSeparatorGrade.setVisibility(View.GONE);
        } else {
            linearLayoutGrade.setVisibility(View.VISIBLE);
            viewSeparatorGrade.setVisibility(View.VISIBLE);
            textViewGrade.setText(activity.userCursus.grade);
        }
        textViewLvl.setText(String.valueOf(activity.userCursus.level));
        progressBarLevel.setProgress((int) (activity.userCursus.level / 21.0 * 100.0));
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRefresh() {
        if (activity != null)
            activity.refresh(new Runnable() {
                @Override
                public void run() {
                    setView();
                }
            });
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
