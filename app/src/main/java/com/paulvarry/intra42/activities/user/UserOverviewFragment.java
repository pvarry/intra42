package com.paulvarry.intra42.activities.user;

import android.content.Context;
import android.content.DialogInterface;
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
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.adapters.SpinnerAdapterCursusAccent;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Friends;
import com.paulvarry.intra42.utils.Share;
import com.paulvarry.intra42.utils.Tag;
import com.paulvarry.intra42.utils.UserImage;
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
public class UserOverviewFragment
        extends Fragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, View.OnLongClickListener {

    final static private String STATE_SELECTED_CURSUS = "selected_cursus";
    @Nullable
    UserActivity activity;
    Users user;
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
    TextView textViewCursusDate;
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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        linearLayoutMobile = view.findViewById(R.id.linearLayoutMobile);
        linearLayoutPhone = view.findViewById(R.id.linearLayoutPhone);
        imageButtonSMS = view.findViewById(R.id.imageButtonSMS);
        relativeLayoutMail = view.findViewById(R.id.linearLayoutMail);
        linearLayoutLocation = view.findViewById(R.id.linearLayoutLocation);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        imageButtonFriends = view.findViewById(R.id.imageButtonFriends);
        chipViewTags = view.findViewById(R.id.chipViewTags);

        textViewName = view.findViewById(R.id.textViewName);
        textViewMobile = view.findViewById(R.id.textViewMobile);
        textViewMail = view.findViewById(R.id.textViewMail);
        textViewPosition = view.findViewById(R.id.textViewPosition);
        textViewWallet = view.findViewById(R.id.textViewWallet);
        textViewCorrectionPoints = view.findViewById(R.id.textViewCorrectionPoints);
        linePool = view.findViewById(R.id.viewPool);
        linearLayoutPool = view.findViewById(R.id.linearLayoutPool);
        textViewPiscine = view.findViewById(R.id.textViewPiscine);

        linearLayoutCursus = view.findViewById(R.id.linearLayoutCursus);
        textViewNoCursusAvailable = view.findViewById(R.id.textViewNoCursusAvailable);
        spinnerCursus = view.findViewById(R.id.spinnerCursus);
        linearLayoutGrade = view.findViewById(R.id.linearLayoutGrade);
        viewSeparatorGrade = view.findViewById(R.id.viewSeparatorGrade);
        textViewGrade = view.findViewById(R.id.textViewGrade);
        textViewLvl = view.findViewById(R.id.textViewLvl);
        progressBarLevel = view.findViewById(R.id.progressBarLevel);
        textViewCursusDate = view.findViewById(R.id.textViewCursusDate);

        setView();

        if (savedInstanceState != null && spinnerCursus != null)
            spinnerCursus.setSelection(savedInstanceState.getInt(STATE_SELECTED_CURSUS), false);

        return view;
    }

    void setView() {

        if (activity == null || activity.user == null || isDetached())
            return;
        user = activity.user;

        swipeRefreshLayout.setOnRefreshListener(this);

        imageButtonFriends.setOnClickListener(this);
        setButtonFriends(null);

        if (app.firebaseRefFriends != null) {
            app.firebaseRefFriends.removeEventListener(friendsEventListener);
            app.firebaseRefFriends.addValueEventListener(friendsEventListener);
        }

        String name = user.displayName + " - " + user.login;
        textViewName.setText(name);
        if (user.phone == null || user.phone.isEmpty())
            linearLayoutMobile.setVisibility(View.GONE);
        else
            textViewMobile.setText(user.phone);
        textViewMail.setText(user.email);

        StringBuilder strLocation = new StringBuilder();
        if (user.location != null) {
            strLocation.append(user.location);
        } else
            strLocation.append(getString(R.string.unavailable));
        if (user.campus != null && !user.campus.isEmpty()) {
            strLocation.append(" - ");
            String sep = "";
            for (Campus c : user.campus) {
                strLocation.append(sep).append(c.name);
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

        linearLayoutPhone.setOnLongClickListener(this);
        imageButtonSMS.setOnLongClickListener(this);
        relativeLayoutMail.setOnLongClickListener(this);
        linearLayoutLocation.setOnLongClickListener(this);

        CursusUsers selected = user.getCursusUsersToDisplay(getContext());
        if (selected != null && user.cursusUsers != null) {
            linearLayoutCursus.setVisibility(View.VISIBLE);
            textViewNoCursusAvailable.setVisibility(View.GONE);
            SpinnerAdapterCursusAccent adapterUserCursus = new SpinnerAdapterCursusAccent(getActivity(), user.cursusUsers);
            spinnerCursus.setAdapter(adapterUserCursus);
            spinnerCursus.setOnItemSelectedListener(this);

            for (int i = 0; i < user.cursusUsers.size(); i++) {
                if (user.cursusUsers.get(i).cursusId == selected.cursusId)
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
        if (app.firebaseRefFriends != null)
            app.firebaseRefFriends.addValueEventListener(friendsEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (app.firebaseRefFriends != null)
            app.firebaseRefFriends.removeEventListener(friendsEventListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (spinnerCursus != null)
            outState.putInt(STATE_SELECTED_CURSUS, spinnerCursus.getSelectedItemPosition());
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
            else
                ClusterMapActivity.openIt(getContext(), user.location);
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
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.alert_last_location, null);
        dialogBuilder.setView(dialogView);

        final LinearLayout linearLayoutLoadingData = dialogView.findViewById(R.id.layoutGetData);
        final LinearLayout linearLayoutLocation = dialogView.findViewById(R.id.layoutLocation);
        final TextView textViewLocation = dialogView.findViewById(R.id.textViewLocation);
        final TextView textViewDate = dialogView.findViewById(R.id.textViewDate);

        linearLayoutLoadingData.setVisibility(View.VISIBLE);
        linearLayoutLocation.setVisibility(View.GONE);

        String lastLocationOf = "Last location of " + user.login;
        dialogBuilder.setTitle(lastLocationOf);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        if (activity == null)
            return;
        activity.app.getApiService().getLastLocations(user.login).enqueue(new Callback<List<Locations>>() {
            @Override
            public void onResponse(Call<List<Locations>> call, Response<List<Locations>> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    Locations location = response.body().get(0);
                    linearLayoutLoadingData.setVisibility(View.GONE);
                    linearLayoutLocation.setVisibility(View.VISIBLE);

                    Campus campus;
                    String host;

                    campus = CacheCampus.get(app.cacheSQLiteHelper, location.campus);
                    host = location.host;
                    if (campus != null)
                        host += " • " + campus.name;
                    textViewLocation.setText(host);
                    if (location.endAt != null || location.beginAt != null) {
                        String date = "";
                        if (location.beginAt != null)
                            date += DateTool.getDateTimeLong(location.beginAt);
                        else
                            date += "?";
                        date += " to ";
                        if (location.endAt != null)
                            date += DateTool.getDateTimeLong(location.endAt);
                        else
                            date += "?";
                        textViewDate.setText(date);
                    } else
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
        if (user.cursusUsers == null)
            return;
        CursusUsers userCursus = user.cursusUsers.get(position);
        if (activity != null) {
            activity.selectedCursus = userCursus;
        }

        if (userCursus.grade == null) {
            linearLayoutGrade.setVisibility(View.GONE);
            viewSeparatorGrade.setVisibility(View.GONE);
        } else {
            linearLayoutGrade.setVisibility(View.VISIBLE);
            viewSeparatorGrade.setVisibility(View.VISIBLE);
            textViewGrade.setText(userCursus.grade);
        }
        textViewLvl.setText(String.valueOf(userCursus.level));
        progressBarLevel.setProgress((int) (userCursus.level / 21.0 * 100.0));

        String dateInfo;

        if (userCursus.begin_at == null && userCursus.end_at == null)
            dateInfo = "Not begun and finish yet";
        else {
            if (userCursus.begin_at != null)
                dateInfo = DateTool.getDateLong(userCursus.begin_at);
            else
                dateInfo = "Not begun yet";

            dateInfo += " • ";

            if (userCursus.end_at != null)
                dateInfo += DateTool.getDateLong(userCursus.end_at);
            else
                dateInfo += "Not finished yet";
        }
        textViewCursusDate.setText(dateInfo);
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

    @Override
    public boolean onLongClick(View v) {

        if (v == linearLayoutPhone)
            dialogCopyOrShare(user.phone);
        else if (v == imageButtonSMS)
            dialogCopyOrShare(user.phone);
        else if (v == relativeLayoutMail)
            dialogCopyOrShare(user.email);
        else if (v == linearLayoutLocation)
            dialogCopyOrShare(user.location);
        else
            return false;
        return true;
    }

    void dialogCopyOrShare(final String string) {
        final Context context = getContext();
        CharSequence action[] = new CharSequence[]{
                context.getString(R.string.copy),
                context.getString(R.string.share)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(string);
        builder.setItems(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    Share.copyString(context, string);
                else
                    Share.shareString(activity, string);
            }
        });
        builder.show();
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
